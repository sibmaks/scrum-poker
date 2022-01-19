package com.github.sibmaks.sp.service;

import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.exception.LoginIsBusyException;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

/**
 * @author drobyshev-ma
 * Created at 20-01-2022
 */
@Import(DataSourceStub.class)
@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTest {
    @MockBean
    private SessionService sessionService;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    void testLogin() {
        String password = "password";

        User user = new User();
        user.setLogin("login");
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setId(42);

        Mockito.when(userRepository.findByLogin(user.getLogin().toLowerCase(Locale.ROOT))).thenReturn(user);

        String sessionId = UUID.randomUUID().toString();
        Mockito.when(sessionService.createSession(user)).thenReturn(sessionId);

        Assertions.assertEquals(sessionId, userService.login(user.getLogin(), password));
    }

    @Test
    void testLogin_notFound() {
        Mockito.when(userRepository.findByLogin(Mockito.anyString())).thenReturn(null);
        Assertions.assertThrows(NotFoundException.class, () -> userService.login("login", "password"));
    }

    @Test
    void testLogin_wrongPassword() {
        String login = "login";
        String password = "password";

        User user = new User();
        user.setLogin(login);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setId(42);

        Mockito.when(userRepository.findByLogin(user.getLogin().toLowerCase(Locale.ROOT))).thenReturn(user);

        String sessionId = UUID.randomUUID().toString();
        Mockito.when(sessionService.createSession(user)).thenReturn(sessionId);

        Assertions.assertThrows(NotFoundException.class, () -> userService.login(login, password + "1"));
    }

    @Test
    void testCreateUser() {
        String login = "login";
        String password = "password";
        String firstName = "first";
        String lastName = "last";

        Mockito.when(userRepository.existsByLogin(login)).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer(it -> it.getArguments()[0]);

        String sessionId = UUID.randomUUID().toString();
        Mockito.when(sessionService.createSession(Mockito.any())).thenReturn(sessionId);

        Assertions.assertEquals(sessionId, userService.createUser(login, password, firstName, lastName));
    }

    @Test
    void testCreateUser_busyLogin() {
        String login = "login";
        String password = "password";
        String firstName = "first";
        String lastName = "last";

        Mockito.when(userRepository.existsByLogin(login)).thenReturn(true);

        Assertions.assertThrows(LoginIsBusyException.class, () -> userService.createUser(login, password, firstName, lastName));
    }

    @Test
    void testGetUser() {
        User user = new User();
        user.setId(42);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Assertions.assertEquals(user, userService.getUser(user.getId()));
    }

    @Test
    void testGetUser_notFound() {
        User user = new User();
        user.setId(42);
        long userId = user.getId();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void testUpdate() {
        User user = new User();
        user.setFirstName("first1");
        user.setLastName("last1");

        Assertions.assertTrue(userService.update(user, "first2", "last2"));

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void testUpdate_notChanged() {
        User user = new User();
        user.setFirstName("first1");
        user.setLastName("last1");

        Assertions.assertFalse(userService.update(user, "first1", "last1"));

        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    void testUpdate_notChanged_null() {
        User user = new User();
        user.setFirstName("first1");
        user.setLastName("last1");

        Assertions.assertFalse(userService.update(user, null, null));

        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    void testChangePassword() {
        User user = new User();
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));

        userService.changePassword(user, "password2");

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void testChangePassword_null() {
        testNoChangePassword(null);
    }

    @Test
    void testChangePassword_empty() {
        testNoChangePassword("");
    }

    @Test
    void testChangePassword_same() {
        testNoChangePassword("password");
    }

    private void testNoChangePassword(String password) {
        User user = new User();
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));

        userService.changePassword(user, password);

        Mockito.verify(userRepository, Mockito.never()).save(user);
    }
}