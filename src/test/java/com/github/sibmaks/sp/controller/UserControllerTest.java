package com.github.sibmaks.sp.controller;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import com.github.sibmaks.sp.api.constant.CommonConstant;
import com.github.sibmaks.sp.api.request.ChangePasswordRequest;
import com.github.sibmaks.sp.api.request.LoginRequest;
import com.github.sibmaks.sp.api.request.RegistrationUserRequest;
import com.github.sibmaks.sp.api.request.UpdateUserRequest;
import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.ClientSession;
import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.UnauthorizedException;
import com.github.sibmaks.sp.service.SessionService;
import com.github.sibmaks.sp.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author drobyshev-ma
 * Created at 30-12-2021
 */
@Import(DataSourceStub.class)
@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        var login = "login@company.com";
        var password = "password";
        var sessionId = UUID.randomUUID().toString();

        when(userService.login(login, password))
                .thenReturn(sessionId);
        var response = mock(HttpServletResponse.class);

        var rq = new LoginRequest(login, password);
        var standardResponse = controller.login(rq, response);
        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        verify(response)
                .setHeader(CommonConstant.HEADER_SESSION_ID, sessionId);
    }

    @Test
    @DisplayName("Successfully logout user")
    void testLogoutUser() {
        var sessionId = UUID.randomUUID().toString();

        var standardResponse = controller.logout(sessionId);
        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        verify(sessionService)
                .logout(sessionId);
    }

    @Test
    @DisplayName("User successfully registered")
    void testUserRegistered() {
        var login = "login@company.com";
        var password = "password";
        var firstName = "firstName";
        var lastName = "lastName";
        var sessionId = UUID.randomUUID().toString();

        when(userService.createUser(login, password, firstName, lastName))
                .thenReturn(sessionId);
        var response = mock(HttpServletResponse.class);

        var request = new RegistrationUserRequest(login, password, firstName, lastName);
        var standardResponse = controller.doRegistration(request, response);
        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        verify(userService)
                .createUser(login, password, firstName, lastName);
        verify(response)
                .setHeader(CommonConstant.HEADER_SESSION_ID, sessionId);
    }

    @Test
    @DisplayName("User profile successfully changed")
    void testUserProfileUpdate() {
        var userId = 1234L;
        var firstName = "firstName";
        var lastName = "lastName";
        var sessionId = UUID.randomUUID().toString();

        var user = new User();
        user.setId(userId);

        var clientSession = ClientSession.builder()
                .userId(userId)
                .sessionId(sessionId)
                .build();
        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);
        when(userService.getUser(userId))
                .thenReturn(user);

        when(userService.update(user, firstName, lastName))
                .thenReturn(true);

        var request = new UpdateUserRequest(firstName, lastName);
        var standardResponse = controller.update(sessionId, request);

        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());
        verify(userService)
                .update(user, firstName, lastName);
    }

    @Test
    @DisplayName("User profile not changed when client unauthorized")
    void testUserProfileUpdate_unauthorized() {
        var firstName = "firstName";
        var lastName = "lastName";
        var sessionId = UUID.randomUUID().toString();

        when(sessionService.getSession(sessionId))
                .thenThrow(new NotFoundException());

        var request = new UpdateUserRequest(firstName, lastName);
        assertThrows(UnauthorizedException.class, () -> controller.update(sessionId, request));
    }

    @Test
    @DisplayName("User password successfully changed")
    void testUserProfileChanged() {
        var userId = 1234L;
        var password = "password";
        var sessionId = UUID.randomUUID().toString();

        var user = new User();
        user.setId(userId);

        var clientSession = ClientSession.builder().userId(userId).sessionId(sessionId).build();
        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);
        when(userService.getUser(userId))
                .thenReturn(user);

        var request = new ChangePasswordRequest(password);
        var standardResponse = controller.changePassword(sessionId, request);

        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());
        verify(userService)
                .changePassword(user, password);
    }
}