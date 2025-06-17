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
     *
     * @see RoomRepository used for cleanup expired rooms
     */
    @Scheduled(
            fixedDelayString = "${app.cleaner.room.delay:7200000}",
            initialDelayString = "${app.cleaner.room.initial.delay:10000}"
    )
    public void cleanUp() {
        var start = System.currentTimeMillis();
        log.debug("Start rooms cleanup");
        var items = roomRepository.deleteExpired();
        log.debug("Rooms cleanup finished: {} items for {}ms", items, (System.currentTimeMillis() - start));
    }
}
