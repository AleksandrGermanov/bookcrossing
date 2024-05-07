package bookrequest;

import bookrequest.service.BookRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import util.beanlib.ServiceLib;
import out.ObjectMapperTuner;
import out.ResponseForm;
import out.ResponseFormer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static out.ResponseFormer.writeResponse;

@RequiredArgsConstructor
public class BookRequestController extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final BookRequestService bookRequestService;
    private final ResponseFormer responseFormer;

    public BookRequestController() {
        objectMapper = ObjectMapperTuner.getTuned();
        bookRequestService = ServiceLib.getDefaultBookRequestService();
        responseFormer = ResponseFormer.DEFAULT_INSTANCE;
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod().toUpperCase();
        switch (method) {
            case "GET" -> doGet(request, response);
            case "POST" -> doPost(request, response);
            case "DELETE" -> doDelete(request, response);
            default -> writeResponse(ResponseForm.METHOD_IS_NOT_ALLOWED, response);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/requests/[0-9]+")) {
            Long id = Long.parseLong(request.getRequestURI().substring("/requests/".length()));
            responseForm = responseFormer.getResponse(() -> bookRequestService.retrieveBookRequest(id));
        }

        writeResponse(responseForm, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/requests/?")) {
            Long userId = Long.parseLong(request.getParameter("user"));
            Long bookId = Long.parseLong(request.getParameter("book"));
            responseForm = responseFormer.getResponse(() -> bookRequestService.createBookRequest(userId, bookId));
        }

        writeResponse(responseForm, response);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/requests/[0-9]+")) {
            Long userId = Long.parseLong(request.getParameter("user"));
            Long requestId = Long.parseLong(request.getRequestURI().substring("/requests/".length()));
            responseForm = responseFormer.getResponse(() -> bookRequestService.deleteBookRequest(userId, requestId));
        }

        writeResponse(responseForm, response);
    }
}
