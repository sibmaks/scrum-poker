package com.github.sibmaks.sp.repository;

import com.github.sibmaks.sp.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.github.sibmaks.sp.domain.ParticipantId;
import com.github.sibmaks.sp.domain.Room;
import com.github.sibmaks.sp.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Participant repository interface
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {
    /**
     * Method for removing participant by user and room.
     * Used for leaving room.
     *
     * @param user user domain
     * @param room room domain
     */
    @Modifying
    @Transactional
    void deleteByParticipantIdUserAndParticipantIdRoom(User user, Room room);

    /**
     * Method for getting all participants in rooms
     *
     * @param room room domain
     * @return list of participants
     */
    List<Participant> findAllByParticipantIdRoom(Room room);

    /**
     * Get participant by participant id.
     * If participant not exists then null will be returned.
     *
     * @param participantId participant identifier
     * @return participant or null
     */
    Optional<Participant> findByParticipantId(ParticipantId participantId);

    /**
     * Reset participant scores in specific room.
     * Used for starting new vote in room
     *
     * @param room room domain
     */
    @Modifying
    @Query("update Participant p set p.score = null where p.participantId.room = ?1")
    void resetScore(Room room);

    /**
     * Get count of participant in room
     * @param room room domain
     * @return count of participant in rooms
     */
    long countByParticipantIdRoom(Room room);
}
