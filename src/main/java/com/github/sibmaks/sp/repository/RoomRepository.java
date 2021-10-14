package com.github.sibmaks.sp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.github.sibmaks.sp.domain.Room;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Room repository interface
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
public interface RoomRepository extends JpaRepository<Room, Long> {
    /**
     * Method for getting user's rooms.
     *
     * @param userId user identifier
     * @return list of user rooms
     */
    @Query("select p.participantId.room from Participant p where p.participantId.user.id = ?1 and p.participantId.room.expired >= CURRENT_TIMESTAMP order by p.participantId.room.id")
    List<Room> findByUserId(long userId);

    /**
     * Method for getting room by user and rooms identifier.
     * If user is noy participant of this room or room doesn't exist then null will be returned
     *
     * @param userId user identifier
     * @param roomId room identifier
     * @return room domain
     */
    @Query("select p.participantId.room from Participant p where p.participantId.user.id = ?1 and p.participantId.room.id = ?2")
    Room findByParticipantAndId(long userId, long roomId);

    /**
     * Method for removing all expired rooms
     */
    @Transactional
    @Modifying
    @Query("delete from Room where expired < CURRENT_TIMESTAMP")
    int deleteExpired();
}
