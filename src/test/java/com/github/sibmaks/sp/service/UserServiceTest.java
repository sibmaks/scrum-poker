package com.github.sibmaks.sp.service;

import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.exception.LoginIsBusyException;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author drobyshev-ma
 * Created at 20-01-2022
 */
@Import(DataSourceStub.class)
@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTest {
    @MockBean
    private SessionService sessionService;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    void testLogin() {
        var password = "password";

        var user = new User();
        user.setLogin("login");
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setId(42);

        when(userRepository.findByLogin(user.getLogin().toLowerCase(Locale.ROOT)))
                .thenReturn(user);

        var sessionId = UUID.randomUUID().toString();
        when(sessionService.createSession(user))
                .thenReturn(sessionId);

        assertEquals(sessionId, userService.login(user.getLogin(), password));
    }

    @Test
    void testLogin_notFound() {
        when(userRepository.findByLogin(anyString()))
                .thenReturn(null);
        assertThrows(NotFoundException.class, () -> userService.login("login", "password"));
    }

    @Test
    void testLogin_wrongPassword() {
        var login = "login";
        var password = "password";

        var user = new User();
        user.setLogin(login);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setId(42);

        when(userRepository.findByLogin(user.getLogin().toLowerCase(Locale.ROOT)))
                .thenReturn(user);

        var sessionId = UUID.randomUUID().toString();
        when(sessionService.createSession(user))
                .thenReturn(sessionId);

        assertThrows(NotFoundException.class, () -> userService.login(login, password + "1"));
    }

    @Test
    void testCreateUser() {
        var login = "login";
        var password = "password";
        var firstName = "first";
        var lastName = "last";

        when(userRepository.existsByLogin(login))
                .thenReturn(false);
        when(userRepository.save(any()))
                .thenAnswer(it -> it.getArguments()[0]);

        var sessionId = UUID.randomUUID().toString();
        when(sessionService.createSession(any()))
                .thenReturn(sessionId);

        assertEquals(sessionId, userService.createUser(login, password, firstName, lastName));
    }

    @Test
    void testCreateUser_busyLogin() {
        var login = "login";
        var password = "password";
        var firstName = "first";
        var lastName = "last";

        when(userRepository.existsByLogin(login))
                .thenReturn(true);

        assertThrows(
                LoginIsBusyException.class,
                () -> userService.createUser(login, password, firstName, lastName)
        );
    }

    @Test
    void testGetUser() {
        var user = new User();
        user.setId(42);
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        assertEquals(user, userService.getUser(user.getId()));
    }

    @Test
    void testGetUser_notFound() {
        var user = new User();
        user.setId(42);

        var userId = user.getId();
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void testUpdate() {
        var user = new User();
        user.setFirstName("first1");
        user.setLastName("last1");

        assertTrue(userService.update(user, "first2", "last2"));

        verify(userRepository)
                .save(user);
    }

    @Test
    void testUpdate_notChanged() {
        var user = new User();
        user.setFirstName("first1");
        user.setLastName("last1");

        assertFalse(userService.update(user, "first1", "last1"));

        verify(userRepository, never())
                .save(user);
    }

    @Test
    void testUpdate_notChanged_null() {
        var user = new User();
        user.setFirstName("first1");
        user.setLastName("last1");

        assertFalse(userService.update(user, null, null));

        verify(userRepository, never())
                .save(user);
    }

    @Test
    void testChangePassword() {
        var user = new User();
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));

        userService.changePassword(user, "password2");

        verify(userRepository)
                .save(user);
    }

    @ParameterizedTest
    @MethodSource("passwordNotChangedCases")
    void testNoChangePassword(String password) {
        var user = new User();
        if(password != null) {
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        }

        userService.changePassword(user, password);

        verify(userRepository, never())
                .save(user);
    }

    public static Stream<Arguments> passwordNotChangedCases() {
        return Stream.of(
                Arguments.of((String) null),
                Arguments.of(""),
                Arguments.of(UUID.randomUUID().toString())
        );
    }
}