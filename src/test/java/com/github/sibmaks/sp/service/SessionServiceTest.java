package com.github.sibmaks.sp.service;

import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.ClientSession;
import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.repository.ClientSessionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.UUID;

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
class SessionServiceTest {
    @MockBean
    private ClientSessionRepository clientSessionRepository;
    @Autowired
    private SessionService service;

    @Test
    void testCreateSession() {
        var user = new User();
        user.setId(42);

        when(clientSessionRepository.save(any()))
                .thenAnswer(it -> it.getArguments()[0]);

        var session = service.createSession(user);

        var captor = ArgumentCaptor.forClass(ClientSession.class);
        verify(clientSessionRepository)
                .save(captor.capture());

        var clientSession = captor.getValue();

        assertEquals(session, clientSession.getSessionId());
        assertEquals(user.getId(), clientSession.getUserId());
        assertNotNull(clientSession.getCreatedAt());
        assertNotNull(clientSession.getValidTo());
        assertTrue(clientSession.getCreatedAt().before(clientSession.getValidTo()));
    }

    @Test
    void testGetSession() {
        var sessionId = UUID.randomUUID().toString();
        var clientSession = new ClientSession();
        when(clientSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(clientSession));

        assertEquals(clientSession, service.getSession(sessionId));
    }

    @Test
    void testGetSession_notExists() {
        var sessionId = UUID.randomUUID().toString();

        when(clientSessionRepository.findById(sessionId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getSession(sessionId));
    }

    @Test
    void testIsAuthorized_sessionId_null() {
        assertFalse(service.isAuthorized(null));
    }

    @Test
    void testIsAuthorized_true() {
        var sessionId = UUID.randomUUID().toString();

        when(clientSessionRepository.existsById(sessionId))
                .thenReturn(true);

        assertTrue(service.isAuthorized(sessionId));
    }

    @Test
    void testIsAuthorized_false() {
        var sessionId = UUID.randomUUID().toString();

        when(clientSessionRepository.existsById(sessionId))
                .thenReturn(false);

        assertFalse(service.isAuthorized(sessionId));
    }

    @Test
    void testLogout_null() {
        service.logout(null);

        verify(clientSessionRepository, never())
                .deleteById(anyString());
    }

    @Test
    void testLogout() {
        var sessionId = UUID.randomUUID().toString();

        service.logout(sessionId);

        verify(clientSessionRepository)
                .deleteById(sessionId);
    }
}