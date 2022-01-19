package com.github.sibmaks.sp.controller;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import com.github.sibmaks.sp.api.constant.CommonConstant;
import com.github.sibmaks.sp.api.request.ChangePasswordRequest;
import com.github.sibmaks.sp.api.request.LoginRequest;
import com.github.sibmaks.sp.api.request.RegistrationUserRequest;
import com.github.sibmaks.sp.api.request.UpdateUserRequest;
import com.github.sibmaks.sp.api.response.StandardResponse;
import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.ClientSession;
import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.UnauthorizedException;
import com.github.sibmaks.sp.service.SessionService;
import com.github.sibmaks.sp.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author drobyshev-ma
 * Created at 30-12-2021
 */
@Import(DataSourceStub.class)
@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    private SessionService sessionService;
    @Autowired
    private UserController controller;

    @Test
    @DisplayName("Successfully login user")
    void testLoginUser() {
        String login = "login@company.com";
        String password = "password";
        String sessionId = UUID.randomUUID().toString();

        Mockito.when(userService.login(login, password)).thenReturn(sessionId);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        StandardResponse standardResponse = controller.login(new LoginRequest(login, password), response);
        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        Mockito.verify(response, Mockito.times(1)).setHeader(CommonConstant.HEADER_SESSION_ID, sessionId);
    }

    @Test
    @DisplayName("Successfully logout user")
    void testLogoutUser() {
        String sessionId = UUID.randomUUID().toString();

        StandardResponse standardResponse = controller.logout(sessionId);
        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        Mockito.verify(sessionService, Mockito.times(1)).logout(sessionId);
    }

    @Test
    @DisplayName("User successfully registered")
    void testUserRegistered() {
        String login = "login@company.com";
        String password = "password";
        String firstName = "firstName";
        String lastName = "lastName";
        String sessionId = UUID.randomUUID().toString();

        Mockito.when(userService.createUser(login, password, firstName, lastName)).thenReturn(sessionId);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        RegistrationUserRequest request = new RegistrationUserRequest(login, password, firstName, lastName);
        StandardResponse standardResponse = controller.doRegistration(request, response);
        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        Mockito.verify(userService, Mockito.times(1)).createUser(login, password, firstName, lastName);
        Mockito.verify(response, Mockito.times(1)).setHeader(CommonConstant.HEADER_SESSION_ID, sessionId);
    }

    @Test
    @DisplayName("User profile successfully changed")
    void testUserProfileUpdate() {
        long userId = 1234L;
        String firstName = "firstName";
        String lastName = "lastName";
        String sessionId = UUID.randomUUID().toString();

        User user = new User();
        user.setId(userId);

        ClientSession clientSession = ClientSession.builder().userId(userId).sessionId(sessionId).build();
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);
        Mockito.when(userService.getUser(userId)).thenReturn(user);

        Mockito.when(userService.update(user, firstName, lastName)).thenReturn(true);

        UpdateUserRequest request = new UpdateUserRequest(firstName, lastName);
        StandardResponse standardResponse = controller.update(sessionId, request);

        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());
        Mockito.verify(userService, Mockito.times(1)).update(user, firstName, lastName);
    }

    @Test
    @DisplayName("User profile not changed when client unauthorized")
    void testUserProfileUpdate_unauthorized() {
        String firstName = "firstName";
        String lastName = "lastName";
        String sessionId = UUID.randomUUID().toString();

        Mockito.when(sessionService.getSession(sessionId)).thenThrow(new NotFoundException());

        UpdateUserRequest request = new UpdateUserRequest(firstName, lastName);
        Assertions.assertThrows(UnauthorizedException.class, () -> controller.update(sessionId, request));
    }

    @Test
    @DisplayName("User password successfully changed")
    void testUserProfileChanged() {
        long userId = 1234L;
        String password = "password";
        String sessionId = UUID.randomUUID().toString();

        User user = new User();
        user.setId(userId);

        ClientSession clientSession = ClientSession.builder().userId(userId).sessionId(sessionId).build();
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);
        Mockito.when(userService.getUser(userId)).thenReturn(user);

        ChangePasswordRequest request = new ChangePasswordRequest(password);
        StandardResponse standardResponse = controller.changePassword(sessionId, request);

        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());
        Mockito.verify(userService, Mockito.times(1)).changePassword(user, password);
    }
}