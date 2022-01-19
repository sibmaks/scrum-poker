package com.github.sibmaks.sp.scheduler;

import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.ClientSession;
import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.repository.ClientSessionRepository;
import com.github.sibmaks.sp.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.UUID;

/**
 * @author drobyshev-ma
 * Created at 19-01-2022
 */
@Import(DataSourceStub.class)
@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class SessionCleanerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionCleaner sessionCleaner;
    @Autowired
    private ClientSessionRepository clientSessionRepository;

    @Test
    void testCleanUp() {
        User user = new User();
        user.setFirstName("first");
        user.setLastName("last");
        user.setLogin(UUID.randomUUID().toString());
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));

        user = userRepository.save(user);

        ClientSession clientSession = new ClientSession();
        clientSession.setSessionId(UUID.randomUUID().toString());
        clientSession.setUserId(user.getId());
        clientSession.setCreatedAt(new Date());
        clientSession.setValidTo(new Date(new Date().getTime() - 1));

        clientSession = clientSessionRepository.save(clientSession);

        sessionCleaner.cleanUp();

        Assertions.assertFalse(clientSessionRepository.existsById(clientSession.getSessionId()));
    }
}