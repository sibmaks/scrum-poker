package com.github.sibmaks.sp.scheduler;

import com.github.sibmaks.sp.repository.ClientSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Session scheduler cleaner
 *
 * @author sibmaks
 * Created at 25-12-2021
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SessionCleaner {
    private final ClientSessionRepository clientSessionRepository;

    /**
     * Method executed at fixed rate and remove old sessions from DB
     * @see ClientSessionRepository
     */
    @Scheduled(fixedDelay = 1000L * 60 * 60 * 2, initialDelay = 10000)
    public void cleanUp() {
        long start = System.currentTimeMillis();
        log.debug("Start session cleanup");
        int items = clientSessionRepository.deleteExpired();
        log.debug("Session cleanup finished: {} items for {}ms", items, (System.currentTimeMillis() - start));
    }
}
