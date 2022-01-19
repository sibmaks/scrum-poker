package com.github.sibmaks.sp.service;

import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.ClientSession;
import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.repository.ClientSessionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

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
class SessionServiceTest {
    @MockBean
    private ClientSessionRepository clientSessionRepository;
    @Autowired
    private SessionService service;

    @Test
    void testCreateSession() {
        User user = new User();
        user.setId(42);

        Mockito.when(clientSessionRepository.save(Mockito.any())).thenAnswer(it -> it.getArguments()[0]);

        String session = service.createSession(user);

        ArgumentCaptor<ClientSession> captor = ArgumentCaptor.forClass(ClientSession.class);
        Mockito.verify(clientSessionRepository, Mockito.times(1)).save(captor.capture());

        ClientSession clientSession = captor.getValue();

        Assertions.assertEquals(session, clientSession.getSessionId());
        Assertions.assertEquals(user.getId(), clientSession.getUserId());
        Assertions.assertNotNull(clientSession.getCreatedAt());
        Assertions.assertNotNull(clientSession.getValidTo());
        Assertions.assertTrue(clientSession.getCreatedAt().before(clientSession.getValidTo()));
    }

    @Test
    void testGetSession() {
        String sessionId = UUID.randomUUID().toString();
        ClientSession clientSession = new ClientSession();
        Mockito.when(clientSessionRepository.findById(sessionId)).thenReturn(Optional.of(clientSession));

        Assertions.assertEquals(clientSession, service.getSession(sessionId));
    }

    @Test
    void testGetSession_notExists() {
        String sessionId = UUID.randomUUID().toString();

        Mockito.when(clientSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> service.getSession(sessionId));
    }

    @Test
    void testIsAuthorized_sessionId_null() {
        Assertions.assertFalse(service.isAuthorized(null));
    }

    @Test
    void testIsAuthorized_true() {
        String sessionId = UUID.randomUUID().toString();

        Mockito.when(clientSessionRepository.existsById(sessionId)).thenReturn(true);

        Assertions.assertTrue(service.isAuthorized(sessionId));
    }

    @Test
    void testIsAuthorized_false() {
        String sessionId = UUID.randomUUID().toString();

        Mockito.when(clientSessionRepository.existsById(sessionId)).thenReturn(false);

        Assertions.assertFalse(service.isAuthorized(sessionId));
    }

    @Test
    void testLogout_null() {
        service.logout(null);

        Mockito.verify(clientSessionRepository, Mockito.never()).deleteById(Mockito.anyString());
    }

    @Test
    void testLogout() {
        String sessionId = UUID.randomUUID().toString();

        service.logout(sessionId);

        Mockito.verify(clientSessionRepository, Mockito.times(1)).deleteById(sessionId);
    }
}