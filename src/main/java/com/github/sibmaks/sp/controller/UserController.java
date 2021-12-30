package com.github.sibmaks.sp.controller;

import com.github.sibmaks.sp.api.constant.CommonConstant;
import com.github.sibmaks.sp.api.request.ChangePasswordRequest;
import com.github.sibmaks.sp.api.request.LoginRequest;
import com.github.sibmaks.sp.api.request.RegistrationUserRequest;
import com.github.sibmaks.sp.api.request.UpdateUserRequest;
import com.github.sibmaks.sp.api.response.StandardResponse;
import com.github.sibmaks.sp.domain.ClientSession;
import com.github.sibmaks.sp.service.SessionService;
import com.github.sibmaks.sp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Rest controller for operation with user
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;
    private final SessionService sessionService;

    /**
     * Api endpoint for user login.
     * Session id will be saved in http header: X-Session-Id.
     * After successful execution empty response will be returned.
     *
     * @param request API request DTO {@link LoginRequest}
     * @param response http servlet response
     * @see StandardResponse
     * @return empty response or response with error description
     */
    @PostMapping(value = "login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StandardResponse login(@RequestBody @Validated LoginRequest request, HttpServletResponse response) {
        String sessionId = userService.login(request.getLogin(), request.getPassword());
        response.setHeader(CommonConstant.HEADER_SESSION_ID, sessionId);
        return new StandardResponse();
    }

    /**
     * Api endpoint for user logout.
     * After successful execution empty response will be returned.
     *
     * @param sessionId session identifier for logout
     * @see StandardResponse
     * @return empty response or response with error description
     */
    @GetMapping(value = "logout")
    public StandardResponse logout(@RequestHeader(CommonConstant.HEADER_SESSION_ID) String sessionId) {
        sessionService.logout(sessionId);
        return new StandardResponse();
    }

    /**
     * Api endpoint for user registration.
     * Session id will be saved in http header: X-Session-Id.
     * After successful execution empty response will be returned.
     *
     * @param request API request DTO {@link RegistrationUserRequest}
     * @param response http servlet response
     * @see StandardResponse
     * @return empty response or response with error description
     */
    @PostMapping(value = "registration")
    @Transactional
    public StandardResponse doRegistration(@RequestBody @Validated RegistrationUserRequest request,
                                           HttpServletResponse response) {
        String sessionId = userService.createUser(request.getLogin(), request.getPassword(), request.getFirstName(),
                request.getLastName());
        response.setHeader(CommonConstant.HEADER_SESSION_ID, sessionId);
        return new StandardResponse();
    }

    /**
     * Api endpoint for updating user information.
     * In case if session not exists or unauthorized then Unauthorized result code will be returned.
     * After successful execution empty response will be returned.
     *
     * @param sessionId session identifier for logout
     * @param request API request DTO {@link UpdateUserRequest}
     * @see StandardResponse
     * @return empty response or response with error description
     */
    @PostMapping(value = "update")
    @Transactional
    public StandardResponse update(@RequestHeader(CommonConstant.HEADER_SESSION_ID) String sessionId,
                                   @RequestBody @Validated UpdateUserRequest request) {
        ClientSession session = sessionService.getSession(sessionId);
        userService.update(session.getUserId(), request.getFirstName(), request.getLastName());
        return new StandardResponse();
    }

    /**
     * Api endpoint for updating user password.
     * In case if session not exists or unauthorized then Unauthorized result code will be returned.
     * After successful execution empty response will be returned.
     *
     * @param sessionId session identifier for logout
     * @param request API request DTO {@link ChangePasswordRequest}
     * @see StandardResponse
     * @return empty response or response with error description
     */
    @PostMapping(value = "changePassword")
    @Transactional
    public StandardResponse changePassword(@RequestHeader(CommonConstant.HEADER_SESSION_ID) String sessionId,
                                   @RequestBody @Validated ChangePasswordRequest request) {
        ClientSession session = sessionService.getSession(sessionId);
        userService.changePassword(session.getUserId(), request.getPassword());
        return new StandardResponse();
    }
}
