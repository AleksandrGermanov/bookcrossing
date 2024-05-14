package book.controller;

import book.dto.BookDto;
import book.service.BookFetchOrder;
import book.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import out.ObjectMapperTuner;
import out.ResponseForm;
import out.ResponseFormer;
import util.beanlib.ServiceLib;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static out.ResponseFormer.writeResponse;

@RequiredArgsConstructor
public class BookController extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final BookService bookService;
    private final ResponseFormer responseFormer;

    public BookController() {
        objectMapper = ObjectMapperTuner.getTuned();
        bookService = ServiceLib.getDefaultBookService();
        responseFormer = ResponseFormer.DEFAULT_INSTANCE;
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod().toUpperCase();

        switch (method) {
            case "GET" -> {
                if (request.getRequestURI().matches("/books/search/?")) {
                    doSearch(request,response);
                } else {
                    doGet(request, response);
                }
            }
            case "POST" -> {
                switch (request.getRequestURI()) {
                    case "/books", "/books/" -> doPost(request, response);
                    case "/books/search", "/books/search/" -> doSearch(request, response);
                }
            }
            case "PATCH" -> {
                if (request.getRequestURI().matches("/books/[0-9]+")) {
                    doPatch(request, response);
                } else {
                    doGiveAway(request, response);
                }
            }
            case "DELETE" -> doDelete(request, response);
            default -> writeResponse(ResponseForm.METHOD_IS_NOT_ALLOWED, response);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/books/?")) {
            responseForm = responseFormer.getResponse(bookService::findAll);
        }
        if (request.getRequestURI().matches("/books/[0-9]+")) {
            Long id = Long.parseLong(request.getRequestURI().substring("/books/".length()));
            responseForm = responseFormer.getResponse(() -> bookService.retrieveBook(id));
        }

        writeResponse(responseForm, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/books/?")) {
            Long userId = request.getParameter("user") != null
                    ? Long.parseLong(request.getParameter("user"))
                    : null;
            BookDto bookDto = objectMapper.readValue(request.getReader(), BookDto.class);
            if (necessaryParamsExist(userId, bookDto)) {
                responseForm = responseFormer.getResponse(() -> bookService.createBook(userId, bookDto));
            } else {
                responseForm = ResponseForm.NECESSARY_PARAMETER_MISSING;
            }
        }

        writeResponse(responseForm, response);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/books/[0-9]+")) {
            Long userId = request.getParameter("user") != null
                    ? Long.parseLong(request.getParameter("user"))
                    : null;
            Long bookId = Long.parseLong(request.getRequestURI().substring("/books/".length()));
            if (necessaryParamsExist(userId)) {
                responseForm = responseFormer.getResponse(() -> bookService.deleteBook(userId, bookId));
            } else {
                responseForm = ResponseForm.NECESSARY_PARAMETER_MISSING;
            }
        }

        writeResponse(responseForm, response);
    }

    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/books/[0-9]+")) {
            Long userId = request.getParameter("user") != null
                    ? Long.parseLong(request.getParameter("user"))
                    : null;
            Long bookId = Long.parseLong(request.getRequestURI().substring("/books/".length()));
            BookDto bookDto = objectMapper.readValue(request.getReader(), BookDto.class);
            if (necessaryParamsExist(userId, bookDto)) {
                responseForm = responseFormer.getResponse(() -> bookService.updateBook(userId, bookId, bookDto));
            } else {
                responseForm = ResponseForm.NECESSARY_PARAMETER_MISSING;
            }
        }

        writeResponse(responseForm, response);
    }

    public void doGiveAway(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;
        String uri = request.getRequestURI();

        if (uri.matches("/books/[0-9]+/give-away")) {
            Long userFromId = request.getParameter("user") != null
                    ? Long.parseLong(request.getParameter("user"))
                    : null;
            Long userToId = request.getParameter("user-to") != null
                    ? Long.parseLong(request.getParameter("user-to"))
                    : null;
            Long bookId = Long.parseLong(uri.substring("/books/".length(),
                    uri.lastIndexOf('/')));
            if (necessaryParamsExist(userFromId, userToId, bookId)) {
                responseForm = responseFormer.getResponse(() -> bookService.giveBookAway(userFromId, userToId, bookId));
            } else {
                responseForm = ResponseForm.NECESSARY_PARAMETER_MISSING;
            }
        }

        writeResponse(responseForm, response);
    }

    public void doSearch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/books/search/?")) {
            String orderString = request.getParameter("order");
            BookFetchOrder order = orderString != null
                    ? BookFetchOrder.valueOf(orderString.toUpperCase())
                    : BookFetchOrder.DEFAULT;
            LinkedHashMap<String, String> params = formParamsMap(request.getParameterMap());
            responseForm = responseFormer.getResponse(() -> bookService.searchByParams(params, order));
        }

        writeResponse(responseForm, response);
    }

    private LinkedHashMap<String, String> formParamsMap(Map<String, String[]> parameterMap) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        Set<String> parameterSet = Set.of("title", "author", "published-since", "is-available");
        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            if (parameterSet.contains(param.getKey())) {
                params.put(param.getKey(), param.getValue()[0]);
            }
        }
        return params;
    }

    private boolean necessaryParamsExist(Object... params) {
        for (Object param : params) {
            if (param == null) {
                return false;
            }
        }
        return true;
    }
}
