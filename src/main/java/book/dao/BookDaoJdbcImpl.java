package book.dao;

import book.model.Book;
import book.service.BookFetchOrder;
import exception.DbException;
import exception.notfound.BookNotFoundException;
import user.dao.UserLazyInitProxy;
import user.model.User;
import util.jdbc.InConnectionRunnable;
import util.jdbc.InConnectionSupplier;
import util.jdbc.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class BookDaoJdbcImpl implements BookDao {
    @Override
    public Book create(Book book) {
        InConnectionSupplier<Long> bookCreate = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.BOOK_INSERT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, book.getTitle());
                preparedStatement.setString(2, book.getAuthor());
                preparedStatement.setInt(3, book.getPublicationYear());
                preparedStatement.setBoolean(4, book.getIsAvailable());
                preparedStatement.executeUpdate();
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            } catch (SQLException e) {
                throw new DbException("Book creation failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
            return null;
        };

        Long generatedId = JdbcUtils.inTransactionGet(bookCreate);
        return obtain(generatedId).orElseThrow(() -> new BookNotFoundException(generatedId));
    }


    @Override
    public Book update(Book book) {
        InConnectionRunnable bookUpdate = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QueryPool.BOOK_UPDATE)) {
                preparedStatement.setString(1, book.getTitle());
                preparedStatement.setString(2, book.getAuthor());
                preparedStatement.setInt(3, book.getPublicationYear());
                preparedStatement.setBoolean(4, book.getIsAvailable());
                preparedStatement.setLong(5, book.getId());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DbException("Book update failed.");
            }
        };

        JdbcUtils.inTransactionRun(bookUpdate);
        return obtain(book.getId()).orElseThrow(() -> new BookNotFoundException(book.getId()));
    }

    @Override
    public Optional<Book> obtain(Long id) {
        InConnectionSupplier<Book> bookRead = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.BOOK_SELECT_BY_ID);
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                return RowMapper.mapSingleResult(resultSet);
            } catch (SQLException e) {
                throw new DbException("Obtaining the book failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
        };

        return Optional.ofNullable(JdbcUtils.inTransactionGet(bookRead));
    }

    @Override
    public void delete(Long id) {
        InConnectionRunnable bookDelete = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QueryPool.BOOK_DELETE)) {
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DbException("Book deletion failed.");
            }
        };

        JdbcUtils.inTransactionRun(bookDelete);
    }

    @Override
    public Boolean exists(Long id) {
        InConnectionSupplier<Boolean> bookExists = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.BOOK_EXISTS);
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            } catch (SQLException e) {
                throw new DbException("Book existence check failed.");
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
            return null;
        };

        return JdbcUtils.inTransactionGet(bookExists);
    }

    @Override
    public List<Book> findAll() {
        return searchByParams(new LinkedHashMap<>(), null);
    }

    @Override
    public List<Book> searchByParams(LinkedHashMap<String, String> params, BookFetchOrder order) {
        List<String> paramKeys = new ArrayList<>(params.keySet());
        InConnectionSupplier<List<Book>> bookSearch = connection -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(QueryPool.formSearch(paramKeys, order));
                insertParams(params, preparedStatement);
                resultSet = preparedStatement.executeQuery();
                return RowMapper.mapResultList(resultSet);
            } catch (SQLException e) {
                throw new DbException(String.format("Book search failed due to %s with message %s."
                        , e.getClass(), e.getMessage()));
            } finally {
                JdbcUtils.tryClose(resultSet, preparedStatement);
            }
        };

        return JdbcUtils.inTransactionGet(bookSearch);
    }

    private void insertParams(LinkedHashMap<String, String> params,
                              PreparedStatement preparedStatement) throws SQLException {
        if (params == null || params.isEmpty()) {
            return;
        }

        int index = 0;
        for (Map.Entry<String, String> param : params.entrySet()) {
            switch (param.getKey()) {
                case "title", "author" -> preparedStatement.setString(++index, '%' + param.getValue() + '%');
                case "publication-year" -> preparedStatement.setInt(++index, Integer.parseInt(param.getValue()));
                case "is-available" -> preparedStatement.setBoolean(++index, Boolean.parseBoolean(param.getValue()));
                default -> throw new IllegalArgumentException("BookSearch parameter is invalid.");
            }
        }
    }

    private static class QueryPool {
        private static final String BOOK_SELECT_BY_ID = "SELECT "
                + "b.*, oc.owner_id u_id "
                + "FROM books b "
                + "LEFT JOIN owner_cards oc ON b.id = oc.book_id "
                + "WHERE b.id = ? "
                + "ORDER BY oc.owned_since;";
        private static final String BOOK_INSERT = "INSERT INTO books(title, author, "
                + "publication_year, is_available) "
                + "VALUES(?,?,?,?);";
        private static final String BOOK_UPDATE = "UPDATE books "
                + "SET  title = ?, author = ?, publication_year = ?, is_available = ? "
                + "WHERE id = ?;";
        private static final String BOOK_DELETE = "DELETE FROM books WHERE id = ?;";
        private static final String BOOK_EXISTS = "SELECT EXISTS(SELECT 1 FROM books WHERE id = ?);";

        private static String formSearch(List<String> params, BookFetchOrder order) {
            return new SearchFormer().formSearch(params, order);
        }

        private static class SearchFormer {
            private static final String BASIC_SEARCH_START = "SELECT "
                    + "b.*, oc.owner_id u_id "
                    + "FROM books b "
                    + "LEFT JOIN owner_cards oc ON b.id = oc.book_id ";
            private static final String TITLE_SEARCH = "UPPER (title) LIKE UPPER (?) ";
            private static final String AUTHOR_SEARCH = "UPPER (author) LIKE UPPER (?) ";
            private static final String PUBLICATION_YEAR_SEARCH = "publication_year >= ? ";
            private static final String IS_AVAILABLE_SEARCH = "is_available = ? ";
            private static final String WHERE = "WHERE ";
            private static final String AND = "AND ";
            private static final String ORDER_BY = "ORDER BY ";
            private static final String PUBLICATION_YEAR_DESC = "publication_year DESC, ";
            private static final String IS_AVAILABLE_DESC = "is_available DESC, ";
            private static final String ID_AND_OWNED_SINCE = "id, owned_since; ";

            private StringBuilder currentSearch;
            private boolean isParametrized;

            private String formSearch(List<String> params, BookFetchOrder order) {
                currentSearch = new StringBuilder(BASIC_SEARCH_START);
                if (params != null && !params.isEmpty()) {
                    for (String param : params) {
                        addPrefixKeyWord();
                        addParamSearchString(param);
                    }
                }
                addOrder(order);

                return currentSearch.toString();
            }

            private void addPrefixKeyWord() {
                if (!isParametrized) {
                    currentSearch.append(WHERE);
                    isParametrized = true;
                } else {
                    currentSearch.append(AND);
                }
            }

            private void addOrder(BookFetchOrder order) {
                currentSearch.append(ORDER_BY);
                if (order == null) {
                    order = BookFetchOrder.DEFAULT;
                }
                switch (order) {
                    case PUBLICATION_YEAR_DESC -> currentSearch.append(PUBLICATION_YEAR_DESC);
                    case IS_AVAILABLE_DESC -> currentSearch.append(IS_AVAILABLE_DESC);
                }
                currentSearch.append(ID_AND_OWNED_SINCE);
            }

            private void addParamSearchString(String param) {
                switch (param) {
                    case "title" -> currentSearch.append(TITLE_SEARCH);
                    case "author" -> currentSearch.append(AUTHOR_SEARCH);
                    case "publication-year" -> currentSearch.append(PUBLICATION_YEAR_SEARCH);
                    case "is-available" -> currentSearch.append(IS_AVAILABLE_SEARCH);
                    default -> throw new IllegalArgumentException("BookSearch parameter is invalid.");
                }
            }
        }
    }

    private static class RowMapper {

        private static Book mapSingleResult(ResultSet resultSet) throws SQLException {
            Book book = new Book();
            Map<Long, User> ownedBy = new LinkedHashMap<>();

            while (resultSet.next()) {
                fillBookFields(book, resultSet);
                addOwnedBy(ownedBy, resultSet);
            }
            book.setOwnedBy(new ArrayList<>(ownedBy.values()));

            return book.getId() != null
                    ? book
                    : null;
        }

        private static void addOwnedBy(Map<Long, User> ownedBy, ResultSet resultSet) throws SQLException {
            Long userId = resultSet.getLong("u_id");

            if (userId != 0 && !ownedBy.containsKey(userId)) {
                ownedBy.put(userId, new UserLazyInitProxy(userId));
            }
        }

        private static void fillBookFields(Book book, ResultSet resultSet) throws SQLException {
            if (book.getId() == null) {
                book.setId(resultSet.getLong("id"));
            }
            if (book.getTitle() == null) {
                book.setTitle(resultSet.getString("title"));
            }
            if (book.getAuthor() == null) {
                book.setAuthor(resultSet.getString("author"));
            }
            if (book.getPublicationYear() == null) {
                book.setPublicationYear(resultSet.getInt("publication_year"));
            }
            if (book.getIsAvailable() == null) {
                book.setIsAvailable(resultSet.getBoolean("is_available"));
            }
        }

        private static List<Book> mapResultList(ResultSet resultSet) throws SQLException {
            List<Book> books = new ArrayList<>();
            Map<Long, User> ownedBy = new LinkedHashMap<>();
            Book book = new Book();

            while (resultSet.next()) {
                if (book.getId() != null
                        && !book.getId().equals(resultSet.getLong("id"))) {
                    book.setOwnedBy(new ArrayList<>(ownedBy.values()));
                    books.add(book);
                    book = new Book();
                    ownedBy.clear();
                }
                fillBookFields(book, resultSet);
                addOwnedBy(ownedBy, resultSet);
            }
            book.setOwnedBy(new ArrayList<>(ownedBy.values()));
            books.add(book);

            return books;
        }
    }
}