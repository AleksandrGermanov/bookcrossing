package out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.handler.ErrorReport;
import exception.handler.ExceptionHandler;

import java.util.function.Supplier;

public class ResponseFormerImpl implements ResponseFormer {
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();
    private final ObjectMapper objectMapper = ObjectMapperTuner.getTuned();

    @Override
    public ResponseForm getResponse(Runnable runnable) {
        ErrorReport errorReport = exceptionHandler.runWithHandler(runnable);
        return formResponse(errorReport);
    }

    @Override
    public <T> ResponseForm getResponse(Supplier<T> supplier) {
        Object body = exceptionHandler.getWithHandler(supplier);
        return formResponse(body);
    }

    private ResponseForm formResponse(Object body) {
        try {
            if (body == null) {
                return new ResponseForm(null, 200);
            }

            if (body instanceof ErrorReport) {
                ErrorReport errorReport = (ErrorReport) body;
                return new ResponseForm(objectMapper.writeValueAsString(errorReport), errorReport.getCode());
            }

            return new ResponseForm(objectMapper.writeValueAsString(body), 200);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}