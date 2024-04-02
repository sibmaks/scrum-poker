package com.github.sibmaks.sp.service;

import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.*;
import com.github.sibmaks.sp.exception.NotAllowedException;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.WrongSecretCodeException;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author drobyshev-ma
 * Created at 20-01-2022
 */
@Import(DataSourceStub.class)
@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    void testCreateRoom_notFound_roles() {
        List<Integer> roleIds = new ArrayList<>();
        roleIds.add(500);
        when(roleRepository.findAllByIdIn(roleIds)).
                thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> roomService.createRoom(null, null, null, roleIds, -1, -1));
    }

    @Test
    void testCreateRoom_notFound_one_roles() {
        List<Integer> roleIds = new ArrayList<>();
        roleIds.add(1);
        roleIds.add(2);
        when(roleRepository.findAllByIdIn(roleIds)).
                thenReturn(Collections.singletonList(new Role()));

        assertThrows(NotFoundException.class, () -> roomService.createRoom(null, null, null, roleIds, -1, -1));
    }

    @Test
    void testCreateRoom_notFound_role() {
        Role role1 = new Role();
        role1.setId(500);

        Role role = new Role();
        role.setId(501);

        List<Integer> roleIds = new ArrayList<>();
        roleIds.add(role1.getId());
        roleIds.add(role.getId());
        when(roleRepository.findAllByIdIn(roleIds)).
                thenReturn(Arrays.asList(role1, role));

        assertThrows(NotFoundException.class, () -> roomService.createRoom(null, null,
                null, roleIds, -1, -1));
    }

    @Test
    void testCreateRoom_withoutSecretCode() {
        Role role1 = new Role();
        role1.setId(500);

        Role role = new Role();
        role.setId(501);

        List<Integer> roleIds = new ArrayList<>();
        roleIds.add(role1.getId());
        roleIds.add(role.getId());
        when(roleRepository.findAllByIdIn(roleIds)).
                thenReturn(Arrays.asList(role1, role));

        int roomId = 100;
        ArgumentCaptor<Room> roomArgumentCaptor = ArgumentCaptor.forClass(Room.class);
        when(roomRepository.save(roomArgumentCaptor.capture())).
                thenAnswer(it -> {
                    Room room = (Room) it.getArguments()[0];
                    room.setId(roomId);
                    return room;
                });

        User user = new User();
        user.setId(200);

        String roomName = UUID.randomUUID() + "<body>";
        int days = 10;

        ArgumentCaptor<List<RoomRole>> roomRolesCaptor = ArgumentCaptor.forClass(List.class);
        when(roomRoleRepository.saveAll(roomRolesCaptor.capture())).
                thenAnswer(it -> it.getArguments()[0]);

        ArgumentCaptor<Participant> participantCaptor = ArgumentCaptor.forClass(Participant.class);
        when(participantRepository.save(participantCaptor.capture())).
                thenAnswer(it -> it.getArguments()[0]);

        Room getRoom = new Room();
        when(roomRepository.findByParticipantAndId(user.getId(), roomId)).
                thenReturn(getRoom);

        Room createdRoom = roomService.createRoom(user, roomName, null, roleIds, days, role.getId());
        assertEquals(getRoom, createdRoom);

        verify(roomSecretRepository, never()).save(any());

        Room savedRoom = roomArgumentCaptor.getValue();
        assertEquals(HtmlUtils.htmlEscape(roomName), savedRoom.getName());
        assertEquals(user, savedRoom.getAuthor());
        assertNotNull(savedRoom.getCreated());
        assertNotNull(savedRoom.getExpired());
        assertTrue(savedRoom.isVoting());

        List<RoomRole> roomRolesCaptorValue = roomRolesCaptor.getValue();
        assertEquals(2, roomRolesCaptorValue.size());
        for (RoomRole roomRole : roomRolesCaptorValue) {
            RoomRoleId roomRoleId = roomRole.getRoomRoleId();
            assertEquals(roomRoleId.getRoom(), savedRoom);
            if (roomRoleId.getRole() != role && roomRoleId.getRole() != role1) {
                fail("Unknown role");
            }
        }

        Participant participant = participantCaptor.getValue();
        assertEquals(participant.getParticipantId().getRoom(), savedRoom);
        assertEquals(participant.getParticipantId().getUser(), user);
        assertEquals(participant.getRole(), role);
    }

    @Test
    void testCreateRoom_withSecretCode() {
        Role role1 = new Role();
        role1.setId(500);

        Role role = new Role();
        role.setId(501);

        List<Integer> roleIds = new ArrayList<>();
        roleIds.add(role1.getId());
        roleIds.add(role.getId());
        when(roleRepository.findAllByIdIn(roleIds)).
                thenReturn(Arrays.asList(role1, role));

        int roomId = 100;
        ArgumentCaptor<Room> roomArgumentCaptor = ArgumentCaptor.forClass(Room.class);
        when(roomRepository.save(roomArgumentCaptor.capture())).
                thenAnswer(it -> {
                    Room room = (Room) it.getArguments()[0];
                    room.setId(roomId);
                    return room;
                });

        User user = new User();
        user.setId(200);

        String roomName = UUID.randomUUID() + "<body>";
        String secret = UUID.randomUUID().toString();
        int days = 10;

        ArgumentCaptor<List<RoomRole>> roomRolesCaptor = ArgumentCaptor.forClass(List.class);
        when(roomRoleRepository.saveAll(roomRolesCaptor.capture())).
                thenAnswer(it -> it.getArguments()[0]);

        ArgumentCaptor<Participant> participantCaptor = ArgumentCaptor.forClass(Participant.class);
        when(participantRepository.save(participantCaptor.capture())).
                thenAnswer(it -> it.getArguments()[0]);

        Room getRoom = new Room();
        when(roomRepository.findByParticipantAndId(user.getId(), roomId)).
                thenReturn(getRoom);

        ArgumentCaptor<RoomSecret> roomSecretArgumentCaptor = ArgumentCaptor.forClass(RoomSecret.class);
        when(roomSecretRepository.save(roomSecretArgumentCaptor.capture())).
                thenAnswer(it -> it.getArguments()[0]);

        Room createdRoom = roomService.createRoom(user, roomName, secret, roleIds, days, role.getId());
        assertEquals(getRoom, createdRoom);

        Room savedRoom = roomArgumentCaptor.getValue();
        assertEquals(HtmlUtils.htmlEscape(roomName), savedRoom.getName());
        assertEquals(user, savedRoom.getAuthor());
        assertNotNull(savedRoom.getCreated());
        assertNotNull(savedRoom.getExpired());
        assertTrue(savedRoom.isVoting());

        RoomSecret roomSecret = roomSecretArgumentCaptor.getValue();
        assertEquals(roomId, roomSecret.getRoomId());
        assertEquals(secret, roomSecret.getSecretCode());

        List<RoomRole> roomRolesCaptorValue = roomRolesCaptor.getValue();
        assertEquals(2, roomRolesCaptorValue.size());
        for (RoomRole roomRole : roomRolesCaptorValue) {
            RoomRoleId roomRoleId = roomRole.getRoomRoleId();
            assertEquals(roomRoleId.getRoom(), savedRoom);
            if (roomRoleId.getRole() != role && roomRoleId.getRole() != role1) {
                fail("Unknown role");
            }
        }

        Participant participant = participantCaptor.getValue();
        assertEquals(participant.getParticipantId().getRoom(), savedRoom);
        assertEquals(participant.getParticipantId().getUser(), user);
        assertEquals(participant.getRole(), role);
    }

    @Test
    void testJoinRoom_notFound_room() {
        long roomId = 500;
        when(roomRepository.findById(roomId)).
                thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roomService.joinRoom(null, roomId, -1, null));
    }

    @Test
    void testJoinRoom_notFound_role() {
        long roomId = 500;
        int roleId = 501;

        Room room = new Room();
        when(roomRepository.findById(roomId)).
                thenReturn(Optional.of(room));
        when(roomRoleRepository.findAllByRoomRoleIdRoom(room)).
                thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> roomService.joinRoom(null, roomId, roleId, null));
    }

    @Test
    void testJoinRoom_wrongSecret() {
        long roomId = 500;
        int roleId = 501;
        String secret = UUID.randomUUID().toString();

        Room room = new Room();
        when(roomRepository.findById(roomId)).
                thenReturn(Optional.of(room));

        Role role = new Role();
        role.setId(roleId);
        RoomRole roomRole = new RoomRole();
        roomRole.setRoomRoleId(new RoomRoleId(room, role));
        when(roomRoleRepository.findAllByRoomRoleIdRoom(room)).
                thenReturn(Collections.singletonList(roomRole));

        RoomSecret roomSecret = new RoomSecret();
        roomSecret.setSecretCode(secret + 1);
        when(roomSecretRepository.findById(roomId)).
                thenReturn(Optional.of(roomSecret));

        assertThrows(WrongSecretCodeException.class, () -> roomService.joinRoom(null, roomId, roleId, secret));
    }

    @Test
    void testJoinRoom_withSecretCode() {
        long roomId = 500;
        int roleId = 501;
        String secret = UUID.randomUUID().toString();

        Room room = new Room();
        room.setId(roomId);
        when(roomRepository.findById(roomId)).
                thenReturn(Optional.of(room));

        Role role = new Role();
        role.setId(roleId);
        RoomRole roomRole = new RoomRole();
        roomRole.setRoomRoleId(new RoomRoleId(room, role));
        when(roomRoleRepository.findAllByRoomRoleIdRoom(room)).
                thenReturn(Collections.singletonList(roomRole));

        RoomSecret roomSecret = new RoomSecret();
        roomSecret.setSecretCode(secret);
        when(roomSecretRepository.findById(roomId)).
                thenReturn(Optional.of(roomSecret));

        User user = new User();
        user.setId(100);

        ArgumentCaptor<Participant> participantArgumentCaptor = ArgumentCaptor.forClass(Participant.class);
        when(participantRepository.save(participantArgumentCaptor.capture())).
                thenAnswer(it -> it.getArguments()[0]);

        Room getRoom = new Room();
        when(roomRepository.findByParticipantAndId(user.getId(), roomId)).
                thenReturn(getRoom);

        Room joinRoom = roomService.joinRoom(user, roomId, roleId, secret);
        assertEquals(getRoom, joinRoom);

        Participant participant = participantArgumentCaptor.getValue();
        assertEquals(participant.getParticipantId().getRoom(), room);
        assertEquals(participant.getParticipantId().getUser(), user);
        assertEquals(participant.getRole(), role);
    }

    @Test
    void testJoinRoom_withoutSecretCode() {
        long roomId = 500;
        int roleId = 501;

        Room room = new Room();
        room.setId(roomId);
        when(roomRepository.findById(roomId)).
                thenReturn(Optional.of(room));

        Role role = new Role();
        role.setId(roleId);
        RoomRole roomRole = new RoomRole();
        roomRole.setRoomRoleId(new RoomRoleId(room, role));

        Role role2 = new Role();
        role2.setId(roleId + 1);
        RoomRole roomRole2 = new RoomRole();
        roomRole2.setRoomRoleId(new RoomRoleId(room, role2));
        when(roomRoleRepository.findAllByRoomRoleIdRoom(room)).
                thenReturn(Arrays.asList(roomRole2, roomRole));

        when(roomSecretRepository.findById(roomId)).
                thenReturn(Optional.empty());

        User user = new User();
        user.setId(100);

        ArgumentCaptor<Participant> participantArgumentCaptor = ArgumentCaptor.forClass(Participant.class);
        when(participantRepository.save(participantArgumentCaptor.capture())).
                thenAnswer(it -> it.getArguments()[0]);

        Room getRoom = new Room();
        when(roomRepository.findByParticipantAndId(user.getId(), roomId)).
                thenReturn(getRoom);

        Room joinRoom = roomService.joinRoom(user, roomId, roleId, null);
        assertEquals(getRoom, joinRoom);

        Participant participant = participantArgumentCaptor.getValue();
        assertEquals(participant.getParticipantId().getRoom(), room);
        assertEquals(participant.getParticipantId().getUser(), user);
        assertEquals(participant.getRole(), role);
    }

    @Test
    void testGetRooms() {
        int userId = 42;
        List<Room> rooms = Collections.singletonList(new Room());
        when(roomService.getRooms(42)).
                thenReturn(rooms);
        assertEquals(rooms, roomService.getRooms(userId));
    }

    @Test
    void testGetParticipantCount() {
        long participants = 100;
        Room room = new Room();
        when(roomService.getParticipantCount(room)).
                thenReturn(participants);
        assertEquals(participants, roomService.getParticipantCount(room));
    }

    @Test
    void testGetRoom() {
        User user = new User();
        user.setId(43);
        int roomId = 42;
        Room room = new Room();

        when(roomRepository.findByParticipantAndId(user.getId(), roomId)).
                thenReturn(room);

        assertEquals(room, roomService.getRoom(user, roomId));
    }

    @Test
    void testLeaveRoom() {
        User user = new User();
        user.setId(43);
        int roomId = 42;
        Room room = new Room();

        when(roomRepository.findByParticipantAndId(user.getId(), roomId)).
                thenReturn(room);

        roomService.leaveRoom(user, roomId);

        verify(participantRepository, times(1)).deleteByParticipantIdUserAndParticipantIdRoom(user, room);
    }

    @Test
    void testVote_notFound() {
        long roomId = 42;
        when(roomRepository.findById(roomId)).
                thenReturn(Optional.empty());
        User user = new User();
        assertThrows(NotFoundException.class, () -> roomService.vote(user, roomId, "score"));
    }

    @Test
    void testVote_notAllowed() {
        Room room = new Room();
        room.setId(42);

        when(roomRepository.findById(room.getId())).
                thenReturn(Optional.of(room));
        when(participantRepository.findByParticipantId(any())).
                thenReturn(Optional.empty());

        User user = new User();
        long roomId = room.getId();
        assertThrows(NotAllowedException.class, () -> roomService.vote(user, roomId, "score"));
    }

    @Test
    void testVote_notVoting() {
        User user = new User();

        Room room = new Room();
        room.setId(42);
        room.setVoting(false);

        when(roomRepository.findById(room.getId())).
                thenReturn(Optional.of(room));

        Participant participant = new Participant();
        participant.setParticipantId(new ParticipantId(user, room));

        when(participantRepository.findByParticipantId(any())).
                thenReturn(Optional.of(participant));

        long roomId = room.getId();
        assertThrows(NotAllowedException.class, () -> roomService.vote(user, roomId, "score"));
    }

    @Test
    void testVote() {
        String score = "score";
        User user = new User();

        Room room = new Room();
        room.setId(42);
        room.setVoting(true);

        when(roomRepository.findById(room.getId())).
                thenReturn(Optional.of(room));

        Participant participant = new Participant();
        participant.setParticipantId(new ParticipantId(user, room));

        when(participantRepository.findByParticipantId(any())).
                thenReturn(Optional.of(participant));

        roomService.vote(user, room.getId(), score);

        ArgumentCaptor<Participant> captor = ArgumentCaptor.forClass(Participant.class);
        verify(participantRepository, times(1)).save(captor.capture());

        Participant savedParticipant = captor.getValue();
        assertEquals(score, savedParticipant.getScore());
    }

    @Test
    void testGetParticipants() {
        Room room = new Room();

        List<Participant> participants = Collections.singletonList(new Participant());
        when(participantRepository.findAllByParticipantIdRoom(room)).
                thenReturn(participants);

        assertEquals(participants, roomService.getParticipants(room));
    }

    @Test
    void testSetVoting_notExists() {
        long roomId = 42;
        when(roomRepository.findById(roomId)).
                thenReturn(Optional.empty());
        User user = new User();
        assertThrows(NotFoundException.class, () -> roomService.setVoting(user, roomId, true));
    }

    @Test
    void testSetVoting_notAuthor() {
        User author = new User();
        author.setId(41);

        User user = new User();
        user.setId(42);

        Room room = new Room();
        room.setId(43);
        room.setAuthor(author);

        long roomId = room.getId();
        when(roomRepository.findById(roomId)).
                thenReturn(Optional.of(room));
        assertThrows(NotAllowedException.class, () -> roomService.setVoting(user, roomId, true));
    }

    @Test
    void testSetVoting_true() {
        User user = new User();
        user.setId(42);

        Room room = new Room();
        room.setId(43);
        room.setAuthor(user);

        when(roomRepository.findById(room.getId())).
                thenReturn(Optional.of(room));
        when(roomRepository.save(any())).
                thenAnswer(it -> it.getArguments()[0]);

        Room setVoting = roomService.setVoting(user, room.getId(), true);
        assertEquals(room, setVoting);

        verify(participantRepository, times(1)).resetScore(room);
    }

    @Test
    void testSetVoting_false() {
        User user = new User();
        user.setId(42);

        Room room = new Room();
        room.setId(43);
        room.setAuthor(user);

        when(roomRepository.findById(room.getId())).
                thenReturn(Optional.of(room));
        when(roomRepository.save(any())).
                thenAnswer(it -> it.getArguments()[0]);

        Room setVoting = roomService.setVoting(user, room.getId(), false);
        assertEquals(room, setVoting);

        verify(participantRepository, never()).resetScore(room);
    }

    @Test
    void testGetRoles_forRoom() {
        long roomId = 42;

        List<Role> roles = Collections.singletonList(new Role());
        when(roleRepository.findAllForRoom(roomId)).
                thenReturn(roles);

        assertEquals(roles, roomService.getRoles(roomId));
    }

    @Test
    void testGetRoles() {
        List<Role> roles = Collections.singletonList(new Role());
        when(roleRepository.findAllByOrderById()).
                thenReturn(roles);
        assertEquals(roles, roomService.getRoles());
    }

    @Test
    void testHasSecret() {
        long roomId = 42;

        when(roomSecretRepository.existsById(roomId)).
                thenReturn(true);
        assertTrue(roomService.hasSecret(roomId));
    }

    @Test
    void testHasSecret_false() {
        long roomId = 42;

        when(roomSecretRepository.existsById(roomId)).
                thenReturn(false);
        assertFalse(roomService.hasSecret(roomId));
    }

    @Test
    void testGetSecret_author() {
        User user = new User();
        user.setId(42);

        Room room = new Room();
        room.setId(43);
        room.setAuthor(user);

        RoomSecret roomSecret = new RoomSecret();
        roomSecret.setSecretCode("123");
        when(roomSecretRepository.findById(room.getId())).
                thenReturn(Optional.of(roomSecret));

        assertEquals(roomSecret.getSecretCode(), roomService.getSecret(user, room));

        verify(roomSecretRepository, times(1)).findById(room.getId());
    }

    @Test
    void testGetSecret_notAuthor() {
        User author = new User();
        author.setId(41);

        User user = new User();
        user.setId(42);

        Room room = new Room();
        room.setId(43);
        room.setAuthor(author);

        RoomSecret roomSecret = new RoomSecret();
        roomSecret.setSecretCode("123");
        when(roomSecretRepository.findById(room.getId())).
                thenReturn(Optional.of(roomSecret));

        assertNull(roomService.getSecret(user, room));
        verify(roomSecretRepository, never()).findById(room.getId());
    }
}