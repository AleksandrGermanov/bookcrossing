package exception.handler;

import exception.mismatch.MismatchException;
import exception.mismatch.OwnerMismatchException;
import exception.exists.ExistsException;
import exception.notfound.NotFoundException;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class ExceptionHandler {

    public <T> Object getWithHandler(Supplier<T> supplier) {
        Object value;

        try {
            value = supplier.get();
        } catch (NotFoundException e) {
            value = handleNotFound(e);
        } catch (ExistsException e) {
            value = handleExists(e);
        } catch (ConstraintViolationException e) {
            value = handleConstraintViolation(e);
        } catch (MismatchException e) {
            value = handleMismatchException(e);
        } catch (Exception e) {
            value = handleUnexpectedException(e);
        }

        return value;
    }


    public ErrorReport runWithHandler(Runnable runnable) {
        ErrorReport value = null;

        try {
            runnable.run();
        } catch (NotFoundException e) {
            value = handleNotFound(e);
        } catch (ExistsException e) {
            value = handleExists(e);
        } catch (MismatchException e) {
            value = handleMismatchException(e);
        } catch (ConstraintViolationException e) {
            value = handleConstraintViolation(e);
        } catch (Exception e) {
            value = handleUnexpectedException(e);
        }

        return value;
    }

    private ErrorReport handleNotFound(NotFoundException e) {

        return new ErrorReport(
                LocalDateTime.now(),
                String.format("Произошла ошибка %s c сообщением %s",
                        e.getClass().getName(), e.getMessage()),
                404
        );
    }

    private ErrorReport handleExists(ExistsException e) {

        return new ErrorReport(
                LocalDateTime.parse(LocalDateTime.now().toString()),
                String.format("Произошла ошибка %s c сообщением %s",
                        e.getClass().getName(), e.getMessage()),
                409
        );
    }

    private ErrorReport handleConstraintViolation(ConstraintViolationException e) {

        return new ErrorReport(
                LocalDateTime.parse(LocalDateTime.now().toString()),
                String.format("Произошла ошибка %s c сообщением %s",
                        e.getClass().getName(), e.getMessage()),
                403
        );
    }

    private ErrorReport handleMismatchException(MismatchException e) {

        return new ErrorReport(
                LocalDateTime.parse(LocalDateTime.now().toString()),
                String.format("Произошла ошибка %s c сообщением %s",
                        e.getClass().getName(), e.getMessage()),
                403
        );
    }

    private ErrorReport handleUnexpectedException(Exception e) {

        return new ErrorReport(
                LocalDateTime.parse(LocalDateTime.now().toString()),
                String.format("Произошла непредвиденная ошибка %s c сообщением %s",
                        e.getClass().getName(), e.getMessage()),
                500
        );
    }
}

