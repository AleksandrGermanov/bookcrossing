package user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import user.dto.UserDto;
import user.service.UserService;
import util.beanlib.ServiceLib;
import util.out.ObjectMapperTuner;
import util.out.ResponseForm;
import util.out.ResponseFormer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserController extends HttpServlet {
    private final ObjectMapper objectMapper = ObjectMapperTuner.getTuned();
    private final UserService userService = ServiceLib.getDefaultUserService();
    private final ResponseFormer responseFormer = new ResponseFormer();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = new ResponseForm("Requested URI is not found.", 404);

        if (request.getRequestURI().matches("/users/?")) {
            responseForm = responseFormer.getResponse(userService::findAll);
        }
        if (request.getRequestURI().matches("/users/[0-9]+")) {
            Long id = Long.parseLong(request.getRequestURI().substring("/users/".length()));
            responseForm = responseFormer.getResponse(() -> userService.retrieveUser(id));
        }
        response.setStatus(responseForm.getResponseCode());
        response.setContentType("Application/JSON");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(responseForm.getResponseBody());
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseForm responseForm = new ResponseForm("Requested URI is not found.", 404);

        if (request.getRequestURI().matches("/users/?")) {
            UserDto userDto = objectMapper.readValue(request.getReader(), UserDto.class);
            responseForm = responseFormer.getResponse(() -> userService.createUser(userDto));
        }
        response.setStatus(responseForm.getResponseCode());
        response.setContentType("Application/JSON");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(responseForm.getResponseBody());
        response.getWriter().flush();
        response.getWriter().close();
    }
}
