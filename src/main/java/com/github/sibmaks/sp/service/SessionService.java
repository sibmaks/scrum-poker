package com.github.sibmaks.sp.service;

import com.github.sibmaks.sp.domain.ClientSession;
import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.repository.ClientSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * User operation service
 *
 * @author sibmaks
 * Created at 24-12-2021
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SessionService {
    private final ClientSessionRepository clientSessionRepository;
    @Value("${app.session.ttl.type}")
    private ChronoUnit sessionTTLType;
    @Value("${app.session.ttl.value}")
    private int sessionTTLValue;

    /**
     * Create session for passed user.
     *
     * @param user user domain
     * @return new session identifier
     */
    @Transactional
    public String createSession(User user) {
        Date createdAt = new Date();

        Date validTo = getValidToDate(createdAt);

        ClientSession clientSession = ClientSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .userId(user.getId())
                .createdAt(createdAt)
                .validTo(validTo)
                .build();
        clientSession = clientSessionRepository.save(clientSession);
        return clientSession.getSessionId();
    }

    /**
     * Get session valid to date.
     * Valid to date calculate as: creationAt + sessionTTL
     *
     * @param createdAt date of session creation
     * @return valid to date
     */
    private Date getValidToDate(Date createdAt) {
        return Date.from(ZonedDateTime.ofInstant(createdAt.toInstant(), ZoneId.systemDefault())
                .plus(sessionTTLValue, sessionTTLType)
                .toInstant());
    }

    /**
     * Get current client session.
     * {@link NotFoundException} will be thrown if session not found
     *
     * @param sessionId session identifier
     * @return client session domain
     */
    public ClientSession getSession(String sessionId) {
        return Optional.ofNullable(sessionId)
                .flatMap(clientSessionRepository::findById)
                .orElseThrow(NotFoundException::new);
    }

    /**
     *  Check is session id belong to authorized session or not.
     *
     * @param sessionId session identifier
     * @return session is exists and valid or not
     */
    public boolean isAuthorized(String sessionId) {
        if(sessionId == null) {
            return false;
        }
        return clientSessionRepository.existsById(sessionId);
    }

    /**
     * Logout session by session id.
     * If session not exists or invalid then noting happened
     *
     * @param sessionId session identifier
     */
    public void logout(String sessionId) {
        if(sessionId != null) {
            clientSessionRepository.deleteById(sessionId);
        }
    }
}
