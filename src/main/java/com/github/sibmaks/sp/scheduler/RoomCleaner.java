package com.github.sibmaks.sp.scheduler;

import com.github.sibmaks.sp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Room scheduler cleaner
 *
 * @author sibmaks
 * Created at 15-10-2021
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoomCleaner {
    private final RoomRepository roomRepository;

    /**
     * Method executed at fixed rate and remove old rooms from DB
     * @see RoomRepository
     */
    @Scheduled(fixedDelay = 1000L * 60 * 60 * 2, initialDelay = 10000)
    public void cleanUp() {
        long start = System.currentTimeMillis();
        log.debug("Start rooms cleanup");
        int items = roomRepository.deleteExpired();
        log.debug("Rooms cleanup finished: {} items for {}ms", items, (System.currentTimeMillis() - start));
    }
}
