package com.github.sibmaks.sp.service;

import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.*;
import com.github.sibmaks.sp.exception.NotAllowedException;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.repository.*;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author drobyshev-ma
 * Created at 20-01-2022
 */
@Import(DataSourceStub.class)
@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomServiceTest {
    @MockBean
    private RoomRepository roomRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private ParticipantRepository participantRepository;
    @MockBean
    private RoomRoleRepository roomRoleRepository;
    @MockBean
    private RoomSecretRepository roomSecretRepository;
    @Autowired
    private RoomService roomService;

    @Test
    public void testGetRooms() {
        int userId = 42;
        List<Room> rooms = Collections.singletonList(new Room());
        Mockito.when(roomService.getRooms(42)).thenReturn(rooms);
        Assertions.assertEquals(rooms, roomService.getRooms(userId));
    }

    @Test
    public void testGetParticipantCount() {
        long participants = 100;
        Room room = new Room();
        Mockito.when(roomService.getParticipantCount(room)).thenReturn(participants);
        Assertions.assertEquals(participants, roomService.getParticipantCount(room));
    }

    @Test
    public void testGetRoom() {
        User user = new User();
        user.setId(43);
        int roomId = 42;
        Room room = new Room();

        Mockito.when(roomRepository.findByParticipantAndId(user.getId(), roomId)).thenReturn(room);

        Assertions.assertEquals(room, roomService.getRoom(user, roomId));
    }

    @Test
    public void testLeaveRoom() {
        User user = new User();
        user.setId(43);
        int roomId = 42;
        Room room = new Room();

        Mockito.when(roomRepository.findByParticipantAndId(user.getId(), roomId)).thenReturn(room);

        roomService.leaveRoom(user, roomId);

        Mockito.verify(participantRepository, Mockito.times(1)).deleteByParticipantIdUserAndParticipantIdRoom(user, room);
    }

    @Test
    public void testVote_notFound() {
        long roomId = 42;
        Mockito.when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> roomService.vote(new User(), roomId, "score"));
    }

    @Test
    public void testVote_notAllowed() {
        Room room = new Room();
        room.setId(42);

        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Mockito.when(participantRepository.findByParticipantId(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotAllowedException.class, () -> roomService.vote(new User(), room.getId(), "score"));
    }

    @Test
    public void testVote_notVoting() {
        User user = new User();

        Room room = new Room();
        room.setId(42);
        room.setVoting(false);

        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        Participant participant = new Participant();
        participant.setParticipantId(new ParticipantId(user, room));

        Mockito.when(participantRepository.findByParticipantId(Mockito.any())).thenReturn(Optional.of(participant));

        Assertions.assertThrows(NotAllowedException.class, () -> roomService.vote(user, room.getId(), "score"));
    }

    @Test
    public void testVote() {
        String score = "score";
        User user = new User();

        Room room = new Room();
        room.setId(42);
        room.setVoting(true);

        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        Participant participant = new Participant();
        participant.setParticipantId(new ParticipantId(user, room));

        Mockito.when(participantRepository.findByParticipantId(Mockito.any())).thenReturn(Optional.of(participant));

        roomService.vote(user, room.getId(), score);

        ArgumentCaptor<Participant> captor = ArgumentCaptor.forClass(Participant.class);
        Mockito.verify(participantRepository, Mockito.times(1)).save(captor.capture());

        Participant savedParticipant = captor.getValue();
        Assertions.assertEquals(score, savedParticipant.getScore());
    }

    @Test
    public void testGetParticipants() {
        Room room = new Room();

        List<Participant> participants = Collections.singletonList(new Participant());
        Mockito.when(participantRepository.findAllByParticipantIdRoom(room)).thenReturn(participants);

        Assertions.assertEquals(participants, roomService.getParticipants(room));
    }

    @Test
    public void testSetVoting_notExists() {
        long roomId = 42;
        Mockito.when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> roomService.setVoting(new User(), roomId, true));
    }

    @Test
    public void testSetVoting_notAuthor() {
        User author = new User();
        author.setId(41);

        User user = new User();
        user.setId(42);

        Room room = new Room();
        room.setId(43);
        room.setAuthor(author);

        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Assertions.assertThrows(NotAllowedException.class, () -> roomService.setVoting(user, room.getId(), true));
    }

    @Test
    public void testSetVoting_true() {
        User user = new User();
        user.setId(42);

        Room room = new Room();
        room.setId(43);
        room.setAuthor(user);

        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Mockito.when(roomRepository.save(Mockito.any())).thenAnswer(it -> it.getArguments()[0]);

        Room setVoting = roomService.setVoting(user, room.getId(), true);
        Assertions.assertEquals(room, setVoting);

        Mockito.verify(participantRepository, Mockito.times(1)).resetScore(room);
    }

    @Test
    public void testSetVoting_false() {
        User user = new User();
        user.setId(42);

        Room room = new Room();
        room.setId(43);
        room.setAuthor(user);

        Mockito.when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        Mockito.when(roomRepository.save(Mockito.any())).thenAnswer(it -> it.getArguments()[0]);

        Room setVoting = roomService.setVoting(user, room.getId(), false);
        Assertions.assertEquals(room, setVoting);

        Mockito.verify(participantRepository, Mockito.never()).resetScore(room);
    }

    @Test
    public void testGetRoles_forRoom() {
        long roomId = 42;

        List<Role> roles = Collections.singletonList(new Role());
        Mockito.when(roleRepository.findAllForRoom(roomId)).thenReturn(roles);

        Assertions.assertEquals(roles, roomService.getRoles(roomId));
    }

    @Test
    public void testGetRoles() {
        List<Role> roles = Collections.singletonList(new Role());
        Mockito.when(roleRepository.findAllByOrderById()).thenReturn(roles);
        Assertions.assertEquals(roles, roomService.getRoles());
    }

    @Test
    public void testHasSecret() {
        long roomId = 42;

        Mockito.when(roomSecretRepository.existsById(roomId)).thenReturn(true);
        Assertions.assertTrue(roomService.hasSecret(roomId));
    }

    @Test
    public void testHasSecret_false() {
        long roomId = 42;

        Mockito.when(roomSecretRepository.existsById(roomId)).thenReturn(false);
        Assertions.assertFalse(roomService.hasSecret(roomId));
    }

    @Test
    public void testGetSecret_author() {
        User user = new User();
        user.setId(42);

        Room room = new Room();
        room.setId(43);
        room.setAuthor(user);

        RoomSecret roomSecret = new RoomSecret();
        roomSecret.setSecretCode("123");
        Mockito.when(roomSecretRepository.findById(room.getId())).thenReturn(Optional.of(roomSecret));

        Assertions.assertEquals(roomSecret.getSecretCode(), roomService.getSecret(user, room));

        Mockito.verify(roomSecretRepository, Mockito.times(1)).findById(room.getId());
    }

    @Test
    public void testGetSecret_notAuthor() {
        User author = new User();
        author.setId(41);

        User user = new User();
        user.setId(42);

        Room room = new Room();
        room.setId(43);
        room.setAuthor(author);

        RoomSecret roomSecret = new RoomSecret();
        roomSecret.setSecretCode("123");
        Mockito.when(roomSecretRepository.findById(room.getId())).thenReturn(Optional.of(roomSecret));

        Assertions.assertNull(roomService.getSecret(user, room));
        Mockito.verify(roomSecretRepository, Mockito.never()).findById(room.getId());
    }
}