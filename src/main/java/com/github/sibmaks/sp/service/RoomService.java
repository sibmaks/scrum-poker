package com.github.sibmaks.sp.service;

import com.github.sibmaks.sp.domain.*;
import com.github.sibmaks.sp.exception.NotAllowedException;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.WrongSecretCodeException;
import com.github.sibmaks.sp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Room operation service
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoleRepository roleRepository;
    private final ParticipantRepository participantRepository;
    private final RoomRoleRepository roomRoleRepository;
    private final RoomSecretRepository roomSecretRepository;

    /**
     * Get rooms for user, where user is participant in
     *
     * @param userUd user identifier
     * @return lsit of rooms
     */
    public List<Room> getRooms(long userUd) {
        return roomRepository.findByUserId(userUd);
    }

    /**
     * Get participant count in room
     *
     * @param room room domain
     * @return count of participant in room
     */
    public long getParticipantCount(Room room) {
        return participantRepository.countByParticipantIdRoom(room);
    }

    /**
     * Create room with specific parameter
     *
     * @param user room author
     * @param name room name
     * @param secretCode room secret code, if null then not setted
     * @param roles roles allowed in room
     * @param days days of rooms existing
     * @param roleId author role identifier
     * @return created room domain
     */
    @Transactional
    public Room createRoom(User user, String name, String secretCode, List<Integer> roles, int days, int roleId) {
        List<Role> rolesList = roleRepository.findAllByIdIn(roles);
        if(rolesList.isEmpty() || roles.size() != rolesList.size()) {
            throw new NotFoundException();
        }
        Role role = rolesList.stream()
                .filter(it -> it.getId() == roleId)
                .findFirst()
                .orElseThrow(NotFoundException::new);

        Date created = new Date();

        Room room = Room.builder()
                .name(HtmlUtils.htmlEscape(name))
                .author(user)
                .created(created)
                .expired(addDays(created, days))
                .voting(true)
                .build();

        room = roomRepository.save(room);

        if(secretCode != null) {
            RoomSecret roomSecret = RoomSecret.builder()
                    .roomId(room.getId())
                    .secretCode(secretCode)
                    .build();
            roomSecretRepository.save(roomSecret);
        }

        Room finalRoom = room;
        List<RoomRole> roomRoles = rolesList.stream()
                .map(it -> RoomRole.builder().roomRoleId(RoomRoleId.builder().role(it).room(finalRoom).build()).build())
                .collect(Collectors.toList());

        roomRoleRepository.saveAll(roomRoles);

        return joinRoom(user, room, role);
    }

    /**
     * Add days to passed date
     * @param date date to add
     * @param days count of days
     * @return new date
     */
    private static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    /**
     * Get room for user.
     * If user is not participant then null will be returned
     *
     * @param user user domain
     * @param roomId room identifier
     * @return room or null
     */
    public Room getRoom(User user, long roomId) {
        return roomRepository.findByParticipantAndId(user.getId(), roomId);
    }

    /**
     * Method for joining user to room with specific role.
     * Secret code can be passed as null if it doesn't require for room
     * If room or role not found then {@link NotFoundException} will be thrown
     *
     * @param user domain to user domain
     * @param roomId room identifier
     * @param roleId role identifier
     * @param secretCode secret code
     * @return room domain in which user was joined
     */
    public Room joinRoom(User user, long roomId, int roleId, String secretCode) {
        Room room = roomRepository.findById(roomId).orElseThrow(NotFoundException::new);
        Role role = roomRoleRepository.findAllByRoomRoleIdRoom(room).stream()
                .map(RoomRole::getRoomRoleId)
                .map(RoomRoleId::getRole)
                .filter(it -> it.getId() == roleId)
                .findFirst()
                .orElseThrow(NotFoundException::new);
        RoomSecret roomSecret = roomSecretRepository.findById(roomId).orElse(null);
        if (roomSecret != null && !roomSecret.getSecretCode().equals(secretCode)) {
            throw new WrongSecretCodeException();
        }
        return joinRoom(user, room, role);
    }

    /**
     * Join user to passed room with specific role
     *
     * @param user user domain
     * @param room room domain
     * @param role role domain
     * @return room after user joining
     */
    private Room joinRoom(User user, Room room, Role role) {
        Participant participant = Participant.builder()
                .role(role)
                .participantId(
                        ParticipantId.builder()
                                .room(room)
                                .user(user)
                                .build()
                ).build();
        participantRepository.save(participant);
        return getRoom(user, room.getId());
    }

    /**
     * Leave room by user
     *
     * @param user user domain
     * @param roomId room identifier
     */
    public void leaveRoom(User user, long roomId) {
        Room room = getRoom(user, roomId);
        participantRepository.deleteByParticipantIdUserAndParticipantIdRoom(user, room);
    }

    /**
     * Get all participant in rooms
     *
     * @param room room domain
     * @return list of all participant
     */
    public List<Participant> getParticipants(Room room) {
        return participantRepository.findAllByParticipantIdRoom(room);
    }

    /**
     * Vote user in room with passed score
     *
     * @param user user domain
     * @param roomId room identifier
     * @param score user score
     */
    @Transactional
    public void vote(User user, long roomId, String score) {
        Room room = roomRepository.findById(roomId).orElseThrow(NotFoundException::new);
        ParticipantId participantId = ParticipantId.builder()
                .user(user)
                .room(room)
                .build();
        Participant participant = participantRepository.findByParticipantId(participantId).orElseThrow(NotAllowedException::new);
        if(!participant.getParticipantId().getRoom().isVoting()) {
            throw new NotAllowedException();
        }
        participant.setScore(HtmlUtils.htmlEscape(score));
        participantRepository.save(participant);
    }

    /**
     * Set voting state in room.
     * Action allowed only for room author.
     * {@link NotAllowedException} will be thrown if called not by room's author.
     * Also reset all participants scores.
     *
     * @param user user domain (author)
     * @param roomId room identifier
     * @param voting new state is voting in progress or not
     * @return new room state
     */
    @Transactional
    public Room setVoting(User user, long roomId, boolean voting) {
        Room room = roomRepository.findById(roomId).orElseThrow(NotFoundException::new);
        if(room.getAuthor().getId() != user.getId()) {
            throw new NotAllowedException();
        }
        room.setVoting(voting);
        if(voting) {
            participantRepository.resetScore(room);
        }
        return roomRepository.save(room);
    }

    /**
     * Get all roles available in room
     *
     * @param roomId room identifier
     * @return list of allowed roles
     */
    public List<Role> getRoles(long roomId) {
        return roleRepository.findAllForRoom(roomId);
    }

    /**
     * Get all available roles
     * @return list of roles
     */
    public List<Role> getRoles() {
        return roleRepository.findAllByOrderById();
    }

    /**
     * Method for getting fact of secret code existing for sepcific room
     *
     * @param roomId room identifier
     * @return true - secretCode existsm false otherwise
     */
    public boolean hasSecret(long roomId) {
        return roomSecretRepository.existsById(roomId);
    }

    /**
     * Get secret for room.
     * Allowed only for room's author, null will be returned otherwise.
     *
     * @param user user domain (author)
     * @param room room domain
     * @return room secret code
     */
    public String getSecret(User user, Room room) {
        if(room.getAuthor().getId() != user.getId()) {
            return null;
        }
        return roomSecretRepository.findById(room.getId())
                .map(RoomSecret::getSecretCode)
                .orElse(null);
    }
}
