package com.github.sibmaks.sp.controller;

import com.github.sibmaks.sp.api.constant.CommonConstant;
import com.github.sibmaks.sp.api.entity.RoomInfo;
import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.*;
import com.github.sibmaks.sp.dto.RoomInfoDto;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.UnauthorizedException;
import com.github.sibmaks.sp.service.RoomService;
import com.github.sibmaks.sp.service.SessionService;
import com.github.sibmaks.sp.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author drobyshev-ma
 * Created at 19-01-2022
 */
@Import(DataSourceStub.class)
@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UIControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    private RoomService roomService;
    @MockBean
    private SessionService sessionService;
    @Autowired
    private UIController uiController;

    @Test
    void testIndex_authorized() {
        var sessionId = UUID.randomUUID().toString();

        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        when(sessionService.isAuthorized(sessionId))
                .thenReturn(true);

        var response = uiController.index(request);
        assertEquals("redirect:/rooms", response);
    }

    @Test
    void testIndex_authorized_cookie() {
        var sessionId = UUID.randomUUID().toString();

        var request = mock(HttpServletRequest.class);
        when(request.getCookies())
                .thenReturn(new Cookie[]{
                        new Cookie("test", "val"),
                        new Cookie(CommonConstant.HEADER_SESSION_ID, sessionId)
                });

        when(sessionService.isAuthorized(sessionId))
                .thenReturn(true);

        var response = uiController.index(request);
        assertEquals("redirect:/rooms", response);
    }

    @Test
    void testIndex_unauthorized() {
        var sessionId = UUID.randomUUID().toString();

        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        when(sessionService.isAuthorized(sessionId))
                .thenReturn(false);

        var response = uiController.index(request);
        assertEquals("index", response);
    }

    @Test
    void testShowRegistrationForm_authorized() {
        var sessionId = UUID.randomUUID().toString();

        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        when(sessionService.isAuthorized(sessionId))
                .thenReturn(true);

        var response = uiController.showRegistrationForm(request);
        assertEquals("redirect:/", response);
    }

    @Test
    void testShowRegistrationForm_unauthorized() {
        var sessionId = UUID.randomUUID().toString();

        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        when(sessionService.isAuthorized(sessionId))
                .thenReturn(false);

        var response = uiController.showRegistrationForm(request);
        assertEquals("registration", response);
    }

    @Test
    void testShowRegistrationForm_unauthorized_empty_cookies() {
        var sessionId = UUID.randomUUID().toString();

        var request = mock(HttpServletRequest.class);
        when(request.getCookies())
                .thenReturn(new Cookie[]{});

        when(sessionService.isAuthorized(sessionId))
                .thenReturn(false);

        var response = uiController.showRegistrationForm(request);
        assertEquals("registration", response);
    }

    @Test
    void testShowRegistrationForm_unauthorized_no_cookie_or_header() {
        var sessionId = UUID.randomUUID().toString();

        var request = mock(HttpServletRequest.class);

        when(sessionService.isAuthorized(sessionId))
                .thenReturn(false);

        var response = uiController.showRegistrationForm(request);
        assertEquals("registration", response);
    }

    @Test
    void testShowAccountForm() {
        var sessionId = UUID.randomUUID().toString();

        var clientSession = new ClientSession();
        clientSession.setUserId(123);

        var user = new User();
        user.setFirstName("first");
        user.setLastName("last");

        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);
        when(userService.getUser(clientSession.getUserId()))
                .thenReturn(user);

        var model = mock(Model.class);
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        var response = uiController.showAccountForm(request, model);
        assertEquals("account", response);

        verify(model)
                .addAttribute("firstName", user.getFirstName());
        verify(model)
                .addAttribute("lastName", user.getLastName());
    }

    @Test
    void testShowAccountForm_unauthorized() {
        var sessionId = UUID.randomUUID().toString();

        var clientSession = new ClientSession();
        clientSession.setUserId(123);

        var user = new User();
        user.setFirstName("first");
        user.setLastName("last");

        when(sessionService.getSession(sessionId))
                .thenThrow(new NotFoundException());

        var model = mock(Model.class);
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        assertThrows(UnauthorizedException.class, () -> uiController.showAccountForm(request, model));
    }

    @Test
    void testGetRooms() {
        var sessionId = UUID.randomUUID().toString();

        var clientSession = new ClientSession();
        clientSession.setUserId(123);
        var user = new User();
        user.setId(clientSession.getUserId());

        var participants = 42L;
        var room = new Room();
        room.setId(10);
        room.setVoting(true);
        room.setName("test name");

        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);
        when(userService.getUser(user.getId()))
                .thenReturn(user);
        when(roomService.getRooms(clientSession.getUserId()))
                .thenReturn(Collections.singletonList(room));
        when(roomService.getParticipantCount(room))
                .thenReturn(participants);

        var model = mock(Model.class);
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        var response = uiController.getRooms(request, model);
        assertEquals("rooms", response);

        ArgumentCaptor<List<RoomInfoDto>> captor = ArgumentCaptor.forClass(List.class);
        verify(model)
                .addAttribute(eq("rooms"), captor.capture());

        var roomInfoDtos = captor.getValue();
        assertEquals(1, roomInfoDtos.size());

        var roomInfoDto = roomInfoDtos.getFirst();
        assertEquals(room.getId(), roomInfoDto.getId());
        assertEquals(room.getName(), roomInfoDto.getName());
        assertEquals(room.isVoting(), roomInfoDto.isVoting());
        assertEquals(participants, roomInfoDto.getParticipants());
    }

    @Test
    void testCreateRoom_authorized() {
        var sessionId = UUID.randomUUID().toString();

        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        List<Role> roles = Collections.singletonList(new Role());
        when(roomService.getRoles())
                .thenReturn(roles);

        when(sessionService.isAuthorized(sessionId))
                .thenReturn(true);
        var model = mock(Model.class);

        var response = uiController.createRoom(request, model);
        assertEquals("create", response);

        verify(model)
                .addAttribute("roles", roles);
    }

    @Test
    void testCreateRoom_unauthorized() {
        var sessionId = UUID.randomUUID().toString();

        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        when(sessionService.isAuthorized(sessionId))
                .thenReturn(false);
        var model = mock(Model.class);

        var response = uiController.createRoom(request, model);
        assertEquals("redirect:/", response);
    }

    @Test
    void testGetRoom_wrongRoomId() {
        var model = mock(Model.class);
        var request = mock(HttpServletRequest.class);
        var response = uiController.getRoom(request, "string", model);
        assertEquals("redirect:/", response);
    }

    @Test
    void testGetRoom_userInRoom_voteInProgress() {
        var sessionId = UUID.randomUUID().toString();

        var clientSession = new ClientSession();
        clientSession.setUserId(123);
        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);

        var user = new User();
        when(userService.getUser(clientSession.getUserId()))
                .thenReturn(user);

        var room = new Room();
        room.setId(10);
        room.setVoting(true);
        room.setName("test name");
        room.setAuthor(user);
        when(roomService.getRoom(user, room.getId()))
                .thenReturn(room);

        var participant = new Participant();
        participant.setScore("score");
        participant.setParticipantId(new ParticipantId(user, room));
        var role = new Role();
        role.setName("role-name");
        participant.setRole(role);
        when(roomService.getParticipants(room))
                .thenReturn(Collections.singletonList(participant));

        var secretCode = UUID.randomUUID().toString();
        when(roomService.getSecret(user, room))
                .thenReturn(secretCode);

        var model = mock(Model.class);
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        var response = uiController.getRoom(request, String.valueOf(room.getId()), model);
        assertEquals("room", response);

        var captor = ArgumentCaptor.forClass(RoomInfo.class);
        verify(model)
                .addAttribute(eq("roomInfo"), captor.capture());
        verify(model)
                .addAttribute("secretCode", secretCode);

        var roomInfoDto = captor.getValue();
        assertEquals(room.getId(), roomInfoDto.getId());
        assertEquals(room.getName(), roomInfoDto.getName());
        assertEquals(room.isVoting(), roomInfoDto.isVoting());
        assertEquals(1, roomInfoDto.getGroupInfos().size());

        var groupInfo = roomInfoDto.getGroupInfos().getFirst();
        assertEquals(role.getName(), groupInfo.getName());

        var participantInfo = groupInfo.getParticipantInfos().getFirst();
        assertEquals(user.getId(), participantInfo.getId());
        assertEquals(user.getFirstName() + " " + user.getLastName(), participantInfo.getName());
        assertNull(participantInfo.getScore());
    }

    @Test
    void testGetRoom_userInRoom_noActiveVotes() {
        var sessionId = UUID.randomUUID().toString();

        var clientSession = new ClientSession();
        clientSession.setUserId(123);
        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);

        var user = new User();
        when(userService.getUser(clientSession.getUserId()))
                .thenReturn(user);

        var room = new Room();
        room.setId(10);
        room.setVoting(false);
        room.setName("test name");
        room.setAuthor(user);
        when(roomService.getRoom(user, room.getId()))
                .thenReturn(room);

        var participant = new Participant();
        participant.setScore("score");
        participant.setParticipantId(new ParticipantId(user, room));
        var role = new Role();
        role.setName("role-name");
        participant.setRole(role);
        when(roomService.getParticipants(room))
                .thenReturn(Collections.singletonList(participant));

        var secretCode = UUID.randomUUID().toString();
        when(roomService.getSecret(user, room))
                .thenReturn(secretCode);

        var model = mock(Model.class);
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        var response = uiController.getRoom(request, String.valueOf(room.getId()), model);
        assertEquals("room", response);

        var captor = ArgumentCaptor.forClass(RoomInfo.class);
        verify(model)
                .addAttribute(eq("roomInfo"), captor.capture());
        verify(model)
                .addAttribute("secretCode", secretCode);

        var roomInfoDto = captor.getValue();
        assertEquals(room.getId(), roomInfoDto.getId());
        assertEquals(room.getName(), roomInfoDto.getName());
        assertEquals(room.isVoting(), roomInfoDto.isVoting());
        assertEquals(1, roomInfoDto.getGroupInfos().size());

        var groupInfo = roomInfoDto.getGroupInfos().get(0);
        assertEquals(role.getName(), groupInfo.getName());

        var participantInfo = groupInfo.getParticipantInfos().get(0);
        assertEquals(user.getId(), participantInfo.getId());
        assertEquals(user.getFirstName() + " " + user.getLastName(), participantInfo.getName());
        assertEquals(participant.getScore(), participantInfo.getScore());
    }

    @Test
    void testGetRoom_userNotInRoom_rolesNull() {
        var sessionId = UUID.randomUUID().toString();

        var clientSession = new ClientSession();
        clientSession.setUserId(123);
        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);

        var user = new User();
        when(userService.getUser(clientSession.getUserId()))
                .thenReturn(user);

        var roomId = 42;
        when(roomService.getRoom(user, roomId))
                .thenReturn(null);
        when(roomService.getRoles(roomId))
                .thenReturn(null);

        var model = mock(Model.class);
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        var response = uiController.getRoom(request, String.valueOf(roomId), model);
        assertEquals("redirect:/", response);
    }

    @Test
    void testGetRoom_userNotInRoom_rolesEmpty() {
        var sessionId = UUID.randomUUID().toString();

        var clientSession = new ClientSession();
        clientSession.setUserId(123);
        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);

        var user = new User();
        when(userService.getUser(clientSession.getUserId()))
                .thenReturn(user);

        var roomId = 42;
        when(roomService.getRoom(user, roomId))
                .thenReturn(null);
        when(roomService.getRoles(roomId))
                .thenReturn(Collections.emptyList());

        var model = mock(Model.class);
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        var response = uiController.getRoom(request, String.valueOf(roomId), model);
        assertEquals("redirect:/", response);
    }

    @Test
    void testGetRoom_userNotInRoom_autoJoin() {
        var sessionId = UUID.randomUUID().toString();

        var clientSession = new ClientSession();
        clientSession.setUserId(123);
        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);

        var user = new User();
        when(userService.getUser(clientSession.getUserId()))
                .thenReturn(user);

        var roomId = 42;
        when(roomService.getRoom(user, roomId))
                .thenReturn(null);
        var role = new Role();
        role.setId(43);
        role.setName("test-name");
        when(roomService.getRoles(roomId))
                .thenReturn(Collections.singletonList(role));
        when(roomService.hasSecret(roomId))
                .thenReturn(false);

        var room = new Room();
        room.setId(roomId);
        room.setVoting(false);
        room.setName("test name");
        room.setAuthor(user);
        when(roomService.joinRoom(user, roomId, role.getId(), null))
                .thenReturn(room);

        var model = mock(Model.class);
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        var participant = new Participant();
        participant.setScore("score");
        participant.setParticipantId(new ParticipantId(user, room));
        participant.setRole(role);
        when(roomService.getParticipants(room))
                .thenReturn(Collections.singletonList(participant));

        var secretCode = UUID.randomUUID().toString();
        when(roomService.getSecret(user, room))
                .thenReturn(secretCode);

        var response = uiController.getRoom(request, String.valueOf(roomId), model);
        assertEquals("room", response);

        var captor = ArgumentCaptor.forClass(RoomInfo.class);
        verify(model)
                .addAttribute(eq("roomInfo"), captor.capture());
        verify(model)
                .addAttribute("secretCode", secretCode);

        var roomInfoDto = captor.getValue();
        assertEquals(room.getId(), roomInfoDto.getId());
        assertEquals(room.getName(), roomInfoDto.getName());
        assertEquals(room.isVoting(), roomInfoDto.isVoting());
        assertEquals(1, roomInfoDto.getGroupInfos().size());

        var groupInfo = roomInfoDto.getGroupInfos().get(0);
        assertEquals(role.getName(), groupInfo.getName());

        var participantInfo = groupInfo.getParticipantInfos().get(0);
        assertEquals(user.getId(), participantInfo.getId());
        assertEquals(user.getFirstName() + " " + user.getLastName(), participantInfo.getName());
        assertEquals(participant.getScore(), participantInfo.getScore());
    }

    @Test
    void testGetRoom_userNotInRoom_joinPageWithSecret() {
        var sessionId = UUID.randomUUID().toString();

        var clientSession = new ClientSession();
        clientSession.setUserId(123);
        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);

        var user = new User();
        when(userService.getUser(clientSession.getUserId()))
                .thenReturn(user);

        var roomId = 42L;
        when(roomService.getRoom(user, roomId))
                .thenReturn(null);
        var role = new Role();
        role.setId(43);
        role.setName("test-name");
        when(roomService.getRoles(roomId))
                .thenReturn(Collections.singletonList(role));
        when(roomService.hasSecret(roomId))
                .thenReturn(true);

        var model = mock(Model.class);
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        var response = uiController.getRoom(request, String.valueOf(roomId), model);
        assertEquals("join", response);

        verify(model)
                .addAttribute("roomId", String.valueOf(roomId));
        verify(model)
                .addAttribute("roles", Collections.singletonList(role));
        verify(model)
                .addAttribute("hasSecret", true);
    }

    @Test
    void testGetRoom_userNotInRoom_joinPageWithRoles() {
        var sessionId = UUID.randomUUID().toString();

        var clientSession = new ClientSession();
        clientSession.setUserId(123);
        when(sessionService.getSession(sessionId))
                .thenReturn(clientSession);

        var user = new User();
        when(userService.getUser(clientSession.getUserId()))
                .thenReturn(user);

        var roomId = 42L;
        when(roomService.getRoom(user, roomId))
                .thenReturn(null);
        var role = new Role();
        role.setId(43);
        role.setName("test-name");
        when(roomService.getRoles(roomId))
                .thenReturn(Arrays.asList(role, role));
        when(roomService.hasSecret(roomId))
                .thenReturn(false);

        var model = mock(Model.class);
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(CommonConstant.HEADER_SESSION_ID))
                .thenReturn(sessionId);

        var response = uiController.getRoom(request, String.valueOf(roomId), model);
        assertEquals("join", response);

        verify(model)
                .addAttribute("roomId", String.valueOf(roomId));
        verify(model)
                .addAttribute("roles", Arrays.asList(role, role));
        verify(model)
                .addAttribute("hasSecret", false);
    }
}