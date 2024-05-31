package user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import user.dto.UserDto;
import user.service.UserService;
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
public class UserController extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final ResponseFormer responseFormer;

    public UserController(){
        objectMapper = ObjectMapperTuner.getTuned();
        userService = ServiceLib.getDefaultUserService();
        responseFormer = ResponseFormer.DEFAULT_INSTANCE;
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod().toUpperCase();
        switch (method) {
            case "GET" -> doGet(request, response);
            case "POST" -> doPost(request, response);
            case "PATCH" -> doPatch(request, response);
            case "DELETE" -> doDelete(request, response);
            default -> writeResponse(ResponseForm.METHOD_IS_NOT_ALLOWED, response);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/users/?")) {
            responseForm = responseFormer.getResponse(userService::findAll);
        }
        if (request.getRequestURI().matches("/users/[0-9]+")) {
            Long id = Long.parseLong(request.getRequestURI().substring("/users/".length()));
            responseForm = responseFormer.getResponse(() -> userService.retrieveUser(id));
        }

        writeResponse(responseForm, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/users/?")) {
            UserDto userDto = objectMapper.readValue(request.getReader(), UserDto.class);
            responseForm = responseFormer.getResponse(() -> userService.createUser(userDto));
        }

        writeResponse(responseForm, response);
    }

    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/users/[0-9]+")) {
            Long id = Long.parseLong(request.getRequestURI().substring("/users/".length()));
            UserDto userDto = objectMapper.readValue(request.getReader(), UserDto.class);
            responseForm = responseFormer.getResponse(() -> userService.updateUser(id, userDto));
        }

        writeResponse(responseForm, response);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = ResponseForm.URI_IS_NOT_FOUND;

        if (request.getRequestURI().matches("/users/[0-9]+")) {
            Long id = Long.parseLong(request.getRequestURI().substring("/users/".length()));
            responseForm = responseFormer.getResponse(() -> userService.deleteUser(id));
        }

        writeResponse(responseForm, response);
    }
}