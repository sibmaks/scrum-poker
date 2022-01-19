package com.github.sibmaks.sp.controller;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import com.github.sibmaks.sp.api.request.*;
import com.github.sibmaks.sp.api.response.CreateRoomResponse;
import com.github.sibmaks.sp.api.response.GetRoomResponse;
import com.github.sibmaks.sp.api.response.JoinRoomResponse;
import com.github.sibmaks.sp.api.response.StandardResponse;
import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.*;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.UnauthorizedException;
import com.github.sibmaks.sp.exception.ValidationErrorException;
import com.github.sibmaks.sp.service.RoomService;
import com.github.sibmaks.sp.service.SessionService;
import com.github.sibmaks.sp.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author drobyshev-ma
 * Created at 19-01-2022
 */
@Import(DataSourceStub.class)
@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        String name = UUID.randomUUID().toString();
        List<Integer> roles = Collections.singletonList(1);
        int days = 5;
        int roleId = 1;
        int roomId = 123;

        String sessionId = mockSession(123);
        User user = mockUser(123);

        Room room = new Room();
        room.setId(roomId);

        Mockito.when(roomService.createRoom(user, name, null, roles, days, roleId)).thenReturn(room);

        StandardResponse standardResponse = controller.createRoom(sessionId, new CreateRoomRequest(name, null, roles, days, roleId));
        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());
        Assertions.assertInstanceOf(CreateRoomResponse.class, standardResponse);

        CreateRoomResponse createRoomResponse = (CreateRoomResponse) standardResponse;
        Assertions.assertEquals(roomId, createRoomResponse.getRoomId());
    }

    @Test
    @DisplayName("Unauthorized create room without secret")
    void testCreateRoomWithoutSecret_unauthorized() {
        String name = UUID.randomUUID().toString();
        List<Integer> roles = Collections.singletonList(1);
        int days = 5;
        int roleId = 1;

        String sessionId = UUID.randomUUID().toString();
        Mockito.when(sessionService.getSession(sessionId)).thenThrow(new NotFoundException());

        CreateRoomRequest request = new CreateRoomRequest(name, null, roles, days, roleId);
        Assertions.assertThrows(UnauthorizedException.class, () -> controller.createRoom(sessionId, request));
    }

    @Test
    @DisplayName("Successfully create room with secret")
    void testCreateRoomWithSecret() {
        String name = UUID.randomUUID().toString();
        List<Integer> roles = Collections.singletonList(1);
        String secret = "1234";
        int days = 5;
        int roleId = 1;
        int roomId = 123;

        String sessionId = mockSession(123);
        User user = mockUser(123);

        Room room = new Room();
        room.setId(roomId);

        Mockito.when(roomService.createRoom(user, name, secret, roles, days, roleId)).thenReturn(room);

        StandardResponse standardResponse = controller.createRoom(sessionId, new CreateRoomRequest(name, secret, roles, days, roleId));
        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());
        Assertions.assertInstanceOf(CreateRoomResponse.class, standardResponse);

        CreateRoomResponse createRoomResponse = (CreateRoomResponse) standardResponse;
        Assertions.assertEquals(roomId, createRoomResponse.getRoomId());
    }

    @Test
    @DisplayName("Got exception when create room with short secret")
    void testCreateRoomWithShortSecret() {
        String name = UUID.randomUUID().toString();
        List<Integer> roles = Collections.singletonList(1);
        String secret = "123";
        int days = 5;
        int roleId = 1;

        String sessionId = mockSession(123);
        User user = mockUser(123);

        Room room = new Room();

        Mockito.when(roomService.createRoom(user, name, secret, roles, days, roleId)).thenReturn(room);

        CreateRoomRequest request = new CreateRoomRequest(name, secret, roles, days, roleId);
        Assertions.assertThrows(ValidationErrorException.class, () -> controller.createRoom(sessionId, request));
    }

    @Test
    @DisplayName("Got exception when create room with long secret")
    void testCreateRoomWithLongSecret() {
        String name = UUID.randomUUID().toString();
        List<Integer> roles = Collections.singletonList(1);
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i <= 128; i++) {
            builder.append("x");
        }
        String secret = builder.toString();
        int days = 5;
        int roleId = 1;

        String sessionId = mockSession(123);
        User user = mockUser(123);

        Room room = new Room();

        Mockito.when(roomService.createRoom(user, name, secret, roles, days, roleId)).thenReturn(room);

        CreateRoomRequest request = new CreateRoomRequest(name, secret, roles, days, roleId);
        Assertions.assertThrows(ValidationErrorException.class, () -> controller.createRoom(sessionId, request));
    }

    @Test
    @DisplayName("Successfully join to room")
    void testJoin() {
        String secretCode = UUID.randomUUID().toString();
        int roomId = 123;
        int roleId = 321;

        String sessionId = mockSession(123);
        User user = mockUser(123);

        Room room = new Room();
        room.setId(roomId);

        Mockito.when(roomService.joinRoom(user, roomId, roleId, secretCode)).thenReturn(room);

        StandardResponse standardResponse = controller.join(sessionId, new JoinRoomRequest(roomId, roleId, secretCode));
        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());
        Assertions.assertInstanceOf(JoinRoomResponse.class, standardResponse);

        JoinRoomResponse joinRoomResponse = (JoinRoomResponse) standardResponse;
        Assertions.assertEquals(roomId, joinRoomResponse.getRoomId());
    }

    @Test
    @DisplayName("Successfully leave to room")
    void testLeave() {
        int roomId = 123;

        String sessionId = mockSession(123);
        mockUser(123);

        Room room = new Room();
        room.setId(roomId);

        StandardResponse standardResponse = controller.leave(sessionId, new LeaveRoomRequest(roomId));
        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());
    }

    @Test
    @DisplayName("Successfully vote in room")
    void testVote() {
        int roomId = 123;
        String score = "score";

        String sessionId = mockSession(123);
        mockUser(123);

        Room room = new Room();
        room.setId(roomId);

        StandardResponse standardResponse = controller.vote(sessionId, new VoteRoomRequest(roomId, score));
        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());
    }

    @Test
    @DisplayName("Successfully set voting in room")
    void testSetVoting() {
        int roomId = 123;
        boolean voting = true;

        String sessionId = mockSession(123);
        User user = mockUser(123);

        Room room = new Room();
        room.setId(roomId);
        room.setAuthor(user);

        Participant participant = new Participant();
        participant.setParticipantId(new ParticipantId(user, room));
        participant.setRole(new Role());

        Mockito.when(roomService.setVoting(user, roomId, voting)).thenReturn(room);
        Mockito.when(roomService.getParticipants(room)).thenReturn(Collections.singletonList(participant));

        StandardResponse standardResponse = controller.setVoting(sessionId, new SetVotingRoomRequest(roomId, voting));
        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        GetRoomResponse getRoomResponse = (GetRoomResponse) standardResponse;
        Assertions.assertEquals(roomId, getRoomResponse.getRoomInfo().getId());
    }

    @Test
    @DisplayName("Successfully get room info")
    void testGetRoom() {
        int roomId = 123;

        String sessionId = mockSession(123);
        User user = mockUser(123);

        Room room = new Room();
        room.setId(roomId);
        room.setAuthor(user);

        Participant participant = new Participant();
        participant.setParticipantId(new ParticipantId(user, room));
        participant.setRole(new Role());

        Mockito.when(roomService.getRoom(user, roomId)).thenReturn(room);
        Mockito.when(roomService.getParticipants(room)).thenReturn(Collections.singletonList(participant));

        StandardResponse standardResponse = controller.getRoom(sessionId, new GetRoomRequest(roomId));
        Assertions.assertEquals(ApiResultCode.OK.code, standardResponse.getResultCode());

        GetRoomResponse getRoomResponse = (GetRoomResponse) standardResponse;
        Assertions.assertEquals(roomId, getRoomResponse.getRoomInfo().getId());
    }

    @Test
    @DisplayName("Not found on get room info")
    void testGetRoom_notFound() {
        int roomId = 123;

        String sessionId = mockSession(123);
        User user = mockUser(123);

        Mockito.when(roomService.getRoom(user, roomId)).thenReturn(null);

        GetRoomRequest request = new GetRoomRequest(roomId);
        Assertions.assertThrows(NotFoundException.class, () -> controller.getRoom(sessionId, request));
    }

    private String mockSession(long userId) {
        String sessionId = UUID.randomUUID().toString();
        ClientSession clientSession = new ClientSession();
        clientSession.setSessionId(sessionId);
        clientSession.setUserId(userId);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);
        return sessionId;
    }

    private User mockUser(long userId) {
        User user = new User();
        user.setId(userId);
        Mockito.when(userService.getUser(userId)).thenReturn(user);
        return user;
    }
}