package com.github.sibmaks.sp.controller;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import com.github.sibmaks.sp.api.request.*;
import com.github.sibmaks.sp.api.response.CreateRoomResponse;
import com.github.sibmaks.sp.api.response.GetRoomResponse;
import com.github.sibmaks.sp.api.response.JoinRoomResponse;
import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.*;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.UnauthorizedException;
import com.github.sibmaks.sp.exception.ValidationErrorException;
import com.github.sibmaks.sp.service.RoomService;
import com.github.sibmaks.sp.service.SessionService;
import com.github.sibmaks.sp.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * @author drobyshev-ma
 * Created at 19-01-2022
 */
@Import(DataSourceStub.class)
@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomControllerTest {
    @MockBean
    private RoomService roomService;
    @MockBean
    private UserService userService;
    @MockBean
    private SessionService sessionService;
    @Autowired
    private RoomController controller;

    @Test
    @DisplayName("Successfully create room without secret")
    void testCreateRoomWithoutSecret() {
        var name = UUID.randomUUID().toString();
        var roles = List.of(1);
        var days = 5;
        var roleId = 1;
        var roomId = 123;

        var sessionId = mockSession(123);
        var user = mockUser(123);

        var room = new Room();
        room.setId(roomId);

        when(roomService.createRoom(user, name, null, roles, days, roleId))
                .thenReturn(room);

        var rq = new CreateRoomRequest(name, null, roles, days, roleId);
        var standardResponse = controller.createRoom(sessionId, rq);
        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        var createRoomResponse = assertInstanceOf(CreateRoomResponse.class, standardResponse);
        assertEquals(roomId, createRoomResponse.getRoomId());
    }

    @Test
    @DisplayName("Unauthorized create room without secret")
    void testCreateRoomWithoutSecret_unauthorized() {
        var name = UUID.randomUUID().toString();
        var roles = List.of(1);
        var days = 5;
        var roleId = 1;

        var sessionId = UUID.randomUUID().toString();
        when(sessionService.getSession(sessionId))
                .thenThrow(new NotFoundException());

        var request = new CreateRoomRequest(name, null, roles, days, roleId);
        assertThrows(UnauthorizedException.class, () -> controller.createRoom(sessionId, request));
    }

    @Test
    @DisplayName("Successfully create room with secret")
    void testCreateRoomWithSecret() {
        var name = UUID.randomUUID().toString();
        var roles = Collections.singletonList(1);
        var secret = "1234";
        var days = 5;
        var roleId = 1;
        var roomId = 123;

        var sessionId = mockSession(123);
        var user = mockUser(123);

        var room = new Room();
        room.setId(roomId);

        when(roomService.createRoom(user, name, secret, roles, days, roleId))
                .thenReturn(room);

        var standardResponse = controller.createRoom(sessionId, new CreateRoomRequest(name, secret, roles, days, roleId));
        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        var createRoomResponse = assertInstanceOf(CreateRoomResponse.class, standardResponse);
        assertEquals(roomId, createRoomResponse.getRoomId());
    }

    @Test
    @DisplayName("Got exception when create room with short secret")
    void testCreateRoomWithShortSecret() {
        var name = UUID.randomUUID().toString();
        var roles = Collections.singletonList(1);
        var secret = "123";
        var days = 5;
        var roleId = 1;

        var sessionId = mockSession(123);
        var user = mockUser(123);

        var room = new Room();

        when(roomService.createRoom(user, name, secret, roles, days, roleId))
                .thenReturn(room);

        var request = new CreateRoomRequest(name, secret, roles, days, roleId);
        assertThrows(ValidationErrorException.class, () -> controller.createRoom(sessionId, request));
    }

    @Test
    @DisplayName("Got exception when create room with long secret")
    void testCreateRoomWithLongSecret() {
        var name = UUID.randomUUID().toString();
        var roles = Collections.singletonList(1);
        var secret = "x".repeat(129);
        var days = 5;
        var roleId = 1;

        var sessionId = mockSession(123);
        var user = mockUser(123);

        var room = new Room();

        when(roomService.createRoom(user, name, secret, roles, days, roleId))
                .thenReturn(room);

        var request = new CreateRoomRequest(name, secret, roles, days, roleId);
        assertThrows(ValidationErrorException.class, () -> controller.createRoom(sessionId, request));
    }

    @Test
    @DisplayName("Successfully join to room")
    void testJoin() {
        var secretCode = UUID.randomUUID().toString();
        var roomId = 123;
        var roleId = 321;

        var sessionId = mockSession(123);
        var user = mockUser(123);

        var room = new Room();
        room.setId(roomId);

        when(roomService.joinRoom(user, roomId, roleId, secretCode))
                .thenReturn(room);

        var rq = new JoinRoomRequest(roomId, roleId, secretCode);
        var standardResponse = controller.join(sessionId, rq);
        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        var joinRoomResponse = assertInstanceOf(JoinRoomResponse.class, standardResponse);
        assertEquals(roomId, joinRoomResponse.getRoomId());
    }

    @Test
    @DisplayName("Successfully leave to room")
    void testLeave() {
        var roomId = 123;

        var sessionId = mockSession(123);
        mockUser(123);

        var room = new Room();
        room.setId(roomId);

        var rq = new LeaveRoomRequest(roomId);
        var standardResponse = controller.leave(sessionId, rq);
        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());
    }

    @Test
    @DisplayName("Successfully vote in room")
    void testVote() {
        var roomId = 123;
        String score = "score";

        var sessionId = mockSession(123);
        mockUser(123);

        var room = new Room();
        room.setId(roomId);

        var standardResponse = controller.vote(sessionId, new VoteRoomRequest(roomId, score));
        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());
    }

    @Test
    @DisplayName("Successfully set voting in room")
    void testSetVoting() {
        var roomId = 123;
        var voting = true;

        var sessionId = mockSession(123);
        var user = mockUser(123);

        var room = new Room();
        room.setId(roomId);
        room.setAuthor(user);

        var participant = new Participant();
        participant.setParticipantId(new ParticipantId(user, room));
        participant.setRole(new Role());

        when(roomService.setVoting(user, roomId, voting))
                .thenReturn(room);
        when(roomService.getParticipants(room))
                .thenReturn(Collections.singletonList(participant));

        var rq = new SetVotingRoomRequest(roomId, voting);
        var standardResponse = controller.setVoting(sessionId, rq);
        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        GetRoomResponse getRoomResponse = (GetRoomResponse) standardResponse;
        assertEquals(roomId, getRoomResponse.getRoomInfo().getId());
    }

    @Test
    @DisplayName("Successfully get room info")
    void testGetRoom() {
        var roomId = 123;

        var sessionId = mockSession(123);
        var user = mockUser(123);

        var room = new Room();
        room.setId(roomId);
        room.setAuthor(user);

        var participant = new Participant();
        participant.setParticipantId(new ParticipantId(user, room));
        participant.setRole(new Role());

        when(roomService.getRoom(user, roomId))
                .thenReturn(room);
        when(roomService.getParticipants(room))
                .thenReturn(Collections.singletonList(participant));

        var standardResponse = controller.getRoom(sessionId, new GetRoomRequest(roomId));
        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        var getRoomResponse = assertInstanceOf(GetRoomResponse.class, standardResponse);
        assertEquals(roomId, getRoomResponse.getRoomInfo().getId());
    }

    @Test
    @DisplayName("Successfully get room info not for author")
    void testGetRoom_notAuthor() {
        var roomId = 123;

        var sessionId = mockSession(123);
        var user = mockUser(123);
        var userOther = mockUser(124);

        var room = new Room();
        room.setId(roomId);
        room.setAuthor(userOther);

        var participantOther = new Participant();
        participantOther.setParticipantId(new ParticipantId(userOther, room));
        participantOther.setRole(new Role());
        participantOther.getRole().setId(1);

        var participant = new Participant();
        participant.setParticipantId(new ParticipantId(user, room));
        participant.setRole(new Role());
        participant.getRole().setId(1);

        when(roomService.getRoom(user, roomId))
                .thenReturn(room);
        when(roomService.getParticipants(room))
                .thenReturn(Arrays.asList(participantOther, participant));

        var standardResponse = controller.getRoom(sessionId, new GetRoomRequest(roomId));
        assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        var getRoomResponse = assertInstanceOf(GetRoomResponse.class, standardResponse);
        assertEquals(roomId, getRoomResponse.getRoomInfo().getId());
    }

    @Test
    @DisplayName("Not found on get room info")
    void testGetRoom_notFound() {
        var roomId = 123;

        var sessionId = mockSession(123);
        var user = mockUser(123);

        when(roomService.getRoom(user, roomId))
                .thenReturn(null);

        var request = new GetRoomRequest(roomId);
        assertThrows(NotFoundException.class, () -> controller.getRoom(sessionId, request));
    }

    private String mockSession(long userId) {
        var sessionId = UUID.randomUUID().toString();
        var clientSession = new ClientSession();
        clientSession.setSessionId(sessionId);
        clientSession.setUserId(userId);
        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);
        return sessionId;
    }

    private User mockUser(long userId) {
        var user = new User();
        user.setId(userId);
        when(userService.getUser(userId))
                .thenReturn(user);
        return user;
    }
}