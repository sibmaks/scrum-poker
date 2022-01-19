package com.github.sibmaks.sp.controller;

import com.github.sibmaks.sp.api.constant.CommonConstant;
import com.github.sibmaks.sp.api.entity.GroupInfo;
import com.github.sibmaks.sp.api.entity.ParticipantInfo;
import com.github.sibmaks.sp.api.entity.RoomInfo;
import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.*;
import com.github.sibmaks.sp.dto.RoomInfoDto;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.UnauthorizedException;
import com.github.sibmaks.sp.service.RoomService;
import com.github.sibmaks.sp.service.SessionService;
import com.github.sibmaks.sp.service.UserService;
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
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
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
        String sessionId = UUID.randomUUID().toString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        Mockito.when(sessionService.isAuthorized(sessionId)).thenReturn(true);

        String response = uiController.index(request);
        Assertions.assertEquals("redirect:/rooms", response);
    }

    @Test
    void testIndex_authorized_cookie() {
        String sessionId = UUID.randomUUID().toString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getCookies()).thenReturn(new Cookie[] {
                new Cookie("test", "val"),
                new Cookie(CommonConstant.HEADER_SESSION_ID, sessionId)
        });

        Mockito.when(sessionService.isAuthorized(sessionId)).thenReturn(true);

        String response = uiController.index(request);
        Assertions.assertEquals("redirect:/rooms", response);
    }

    @Test
    void testIndex_unauthorized() {
        String sessionId = UUID.randomUUID().toString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        Mockito.when(sessionService.isAuthorized(sessionId)).thenReturn(false);

        String response = uiController.index(request);
        Assertions.assertEquals("index", response);
    }

    @Test
    void testShowRegistrationForm_authorized() {
        String sessionId = UUID.randomUUID().toString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        Mockito.when(sessionService.isAuthorized(sessionId)).thenReturn(true);

        String response = uiController.showRegistrationForm(request);
        Assertions.assertEquals("redirect:/", response);
    }

    @Test
    void testShowRegistrationForm_unauthorized() {
        String sessionId = UUID.randomUUID().toString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        Mockito.when(sessionService.isAuthorized(sessionId)).thenReturn(false);

        String response = uiController.showRegistrationForm(request);
        Assertions.assertEquals("registration", response);
    }

    @Test
    void testShowRegistrationForm_unauthorized_empty_cookies() {
        String sessionId = UUID.randomUUID().toString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getCookies()).thenReturn(new Cookie[] {});

        Mockito.when(sessionService.isAuthorized(sessionId)).thenReturn(false);

        String response = uiController.showRegistrationForm(request);
        Assertions.assertEquals("registration", response);
    }

    @Test
    void testShowRegistrationForm_unauthorized_no_cookie_or_header() {
        String sessionId = UUID.randomUUID().toString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(sessionService.isAuthorized(sessionId)).thenReturn(false);

        String response = uiController.showRegistrationForm(request);
        Assertions.assertEquals("registration", response);
    }

    @Test
    void testShowAccountForm() {
        String sessionId = UUID.randomUUID().toString();

        ClientSession clientSession = new ClientSession();
        clientSession.setUserId(123);

        User user = new User();
        user.setFirstName("first");
        user.setLastName("last");

        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);
        Mockito.when(userService.getUser(clientSession.getUserId())).thenReturn(user);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        String response = uiController.showAccountForm(request, model);
        Assertions.assertEquals("account", response);

        Mockito.verify(model, Mockito.times(1)).addAttribute("firstName", user.getFirstName());
        Mockito.verify(model, Mockito.times(1)).addAttribute("lastName", user.getLastName());
    }

    @Test
    void testShowAccountForm_unauthorized() {
        String sessionId = UUID.randomUUID().toString();

        ClientSession clientSession = new ClientSession();
        clientSession.setUserId(123);

        User user = new User();
        user.setFirstName("first");
        user.setLastName("last");

        Mockito.when(sessionService.getSession(sessionId)).thenThrow(new NotFoundException());

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        Assertions.assertThrows(UnauthorizedException.class, () -> uiController.showAccountForm(request, model));
    }

    @Test
    void testGetRooms() {
        String sessionId = UUID.randomUUID().toString();

        ClientSession clientSession = new ClientSession();
        clientSession.setUserId(123);
        User user = new User();
        user.setId(clientSession.getUserId());

        long participants = 42;
        Room room = new Room();
        room.setId(10);
        room.setVoting(true);
        room.setName("test name");

        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);
        Mockito.when(userService.getUser(user.getId())).thenReturn(user);
        Mockito.when(roomService.getRooms(clientSession.getUserId())).thenReturn(Collections.singletonList(room));
        Mockito.when(roomService.getParticipantCount(room)).thenReturn(participants);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        String response = uiController.getRooms(request, model);
        Assertions.assertEquals("rooms", response);

        ArgumentCaptor<List<RoomInfoDto>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(model, Mockito.times(1)).addAttribute(Mockito.eq("rooms"), captor.capture());

        List<RoomInfoDto> roomInfoDtos = captor.getValue();
        Assertions.assertEquals(1, roomInfoDtos.size());

        RoomInfoDto roomInfoDto = roomInfoDtos.get(0);
        Assertions.assertEquals(room.getId(), roomInfoDto.getId());
        Assertions.assertEquals(room.getName(), roomInfoDto.getName());
        Assertions.assertEquals(room.isVoting(), roomInfoDto.isVoting());
        Assertions.assertEquals(participants, roomInfoDto.getParticipants());
    }

    @Test
    void testCreateRoom_authorized() {
        String sessionId = UUID.randomUUID().toString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        List<Role> roles = Collections.singletonList(new Role());
        Mockito.when(roomService.getRoles()).thenReturn(roles);

        Mockito.when(sessionService.isAuthorized(sessionId)).thenReturn(true);
        Model model = Mockito.mock(Model.class);

        String response = uiController.createRoom(request, model);
        Assertions.assertEquals("create", response);

        Mockito.verify(model, Mockito.times(1)).addAttribute("roles", roles);
    }

    @Test
    void testCreateRoom_unauthorized() {
        String sessionId = UUID.randomUUID().toString();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        Mockito.when(sessionService.isAuthorized(sessionId)).thenReturn(false);
        Model model = Mockito.mock(Model.class);

        String response = uiController.createRoom(request, model);
        Assertions.assertEquals("redirect:/", response);
    }

    @Test
    void testGetRoom_wrongRoomId() {
        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String response = uiController.getRoom(request, "string", model);
        Assertions.assertEquals("redirect:/", response);
    }

    @Test
    void testGetRoom_userInRoom_voteInProgress() {
        String sessionId = UUID.randomUUID().toString();

        ClientSession clientSession = new ClientSession();
        clientSession.setUserId(123);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);

        User user = new User();
        Mockito.when(userService.getUser(clientSession.getUserId())).thenReturn(user);

        Room room = new Room();
        room.setId(10);
        room.setVoting(true);
        room.setName("test name");
        room.setAuthor(user);
        Mockito.when(roomService.getRoom(user, room.getId())).thenReturn(room);

        Participant participant = new Participant();
        participant.setScore("score");
        participant.setParticipantId(new ParticipantId(user, room));
        Role role = new Role();
        role.setName("role-name");
        participant.setRole(role);
        Mockito.when(roomService.getParticipants(room)).thenReturn(Collections.singletonList(participant));

        String secretCode = UUID.randomUUID().toString();
        Mockito.when(roomService.getSecret(user, room)).thenReturn(secretCode);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        String response = uiController.getRoom(request, String.valueOf(room.getId()), model);
        Assertions.assertEquals("room", response);

        ArgumentCaptor<RoomInfo> captor = ArgumentCaptor.forClass(RoomInfo.class);
        Mockito.verify(model, Mockito.times(1)).addAttribute(Mockito.eq("roomInfo"), captor.capture());
        Mockito.verify(model, Mockito.times(1)).addAttribute("secretCode", secretCode);

        RoomInfo roomInfoDto = captor.getValue();
        Assertions.assertEquals(room.getId(), roomInfoDto.getId());
        Assertions.assertEquals(room.getName(), roomInfoDto.getName());
        Assertions.assertEquals(room.isVoting(), roomInfoDto.isVoting());
        Assertions.assertEquals(1, roomInfoDto.getGroupInfos().size());

        GroupInfo groupInfo = roomInfoDto.getGroupInfos().get(0);
        Assertions.assertEquals(role.getName(), groupInfo.getName());

        ParticipantInfo participantInfo = groupInfo.getParticipantInfos().get(0);
        Assertions.assertEquals(user.getId(), participantInfo.getId());
        Assertions.assertEquals(user.getFirstName() + " " + user.getLastName(), participantInfo.getName());
        Assertions.assertNull(participantInfo.getScore());
    }

    @Test
    void testGetRoom_userInRoom_noActiveVotes() {
        String sessionId = UUID.randomUUID().toString();

        ClientSession clientSession = new ClientSession();
        clientSession.setUserId(123);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);

        User user = new User();
        Mockito.when(userService.getUser(clientSession.getUserId())).thenReturn(user);

        Room room = new Room();
        room.setId(10);
        room.setVoting(false);
        room.setName("test name");
        room.setAuthor(user);
        Mockito.when(roomService.getRoom(user, room.getId())).thenReturn(room);

        Participant participant = new Participant();
        participant.setScore("score");
        participant.setParticipantId(new ParticipantId(user, room));
        Role role = new Role();
        role.setName("role-name");
        participant.setRole(role);
        Mockito.when(roomService.getParticipants(room)).thenReturn(Collections.singletonList(participant));

        String secretCode = UUID.randomUUID().toString();
        Mockito.when(roomService.getSecret(user, room)).thenReturn(secretCode);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        String response = uiController.getRoom(request, String.valueOf(room.getId()), model);
        Assertions.assertEquals("room", response);

        ArgumentCaptor<RoomInfo> captor = ArgumentCaptor.forClass(RoomInfo.class);
        Mockito.verify(model, Mockito.times(1)).addAttribute(Mockito.eq("roomInfo"), captor.capture());
        Mockito.verify(model, Mockito.times(1)).addAttribute("secretCode", secretCode);

        RoomInfo roomInfoDto = captor.getValue();
        Assertions.assertEquals(room.getId(), roomInfoDto.getId());
        Assertions.assertEquals(room.getName(), roomInfoDto.getName());
        Assertions.assertEquals(room.isVoting(), roomInfoDto.isVoting());
        Assertions.assertEquals(1, roomInfoDto.getGroupInfos().size());

        GroupInfo groupInfo = roomInfoDto.getGroupInfos().get(0);
        Assertions.assertEquals(role.getName(), groupInfo.getName());

        ParticipantInfo participantInfo = groupInfo.getParticipantInfos().get(0);
        Assertions.assertEquals(user.getId(), participantInfo.getId());
        Assertions.assertEquals(user.getFirstName() + " " + user.getLastName(), participantInfo.getName());
        Assertions.assertEquals(participant.getScore(), participantInfo.getScore());
    }

    @Test
    void testGetRoom_userNotInRoom_rolesNull() {
        String sessionId = UUID.randomUUID().toString();

        ClientSession clientSession = new ClientSession();
        clientSession.setUserId(123);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);

        User user = new User();
        Mockito.when(userService.getUser(clientSession.getUserId())).thenReturn(user);

        int roomId = 42;
        Mockito.when(roomService.getRoom(user, roomId)).thenReturn(null);
        Mockito.when(roomService.getRoles(roomId)).thenReturn(null);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        String response = uiController.getRoom(request, String.valueOf(roomId), model);
        Assertions.assertEquals("redirect:/", response);
    }

    @Test
    void testGetRoom_userNotInRoom_rolesEmpty() {
        String sessionId = UUID.randomUUID().toString();

        ClientSession clientSession = new ClientSession();
        clientSession.setUserId(123);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);

        User user = new User();
        Mockito.when(userService.getUser(clientSession.getUserId())).thenReturn(user);

        int roomId = 42;
        Mockito.when(roomService.getRoom(user, roomId)).thenReturn(null);
        Mockito.when(roomService.getRoles(roomId)).thenReturn(Collections.emptyList());

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        String response = uiController.getRoom(request, String.valueOf(roomId), model);
        Assertions.assertEquals("redirect:/", response);
    }

    @Test
    void testGetRoom_userNotInRoom_autoJoin() {
        String sessionId = UUID.randomUUID().toString();

        ClientSession clientSession = new ClientSession();
        clientSession.setUserId(123);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);

        User user = new User();
        Mockito.when(userService.getUser(clientSession.getUserId())).thenReturn(user);

        int roomId = 42;
        Mockito.when(roomService.getRoom(user, roomId)).thenReturn(null);
        Role role = new Role();
        role.setId(43);
        role.setName("test-name");
        Mockito.when(roomService.getRoles(roomId)).thenReturn(Collections.singletonList(role));
        Mockito.when(roomService.hasSecret(roomId)).thenReturn(false);

        Room room = new Room();
        room.setId(roomId);
        room.setVoting(false);
        room.setName("test name");
        room.setAuthor(user);
        Mockito.when(roomService.joinRoom(user, roomId, role.getId(), null)).thenReturn(room);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        Participant participant = new Participant();
        participant.setScore("score");
        participant.setParticipantId(new ParticipantId(user, room));
        participant.setRole(role);
        Mockito.when(roomService.getParticipants(room)).thenReturn(Collections.singletonList(participant));

        String secretCode = UUID.randomUUID().toString();
        Mockito.when(roomService.getSecret(user, room)).thenReturn(secretCode);

        String response = uiController.getRoom(request, String.valueOf(roomId), model);
        Assertions.assertEquals("room", response);

        ArgumentCaptor<RoomInfo> captor = ArgumentCaptor.forClass(RoomInfo.class);
        Mockito.verify(model, Mockito.times(1)).addAttribute(Mockito.eq("roomInfo"), captor.capture());
        Mockito.verify(model, Mockito.times(1)).addAttribute("secretCode", secretCode);

        RoomInfo roomInfoDto = captor.getValue();
        Assertions.assertEquals(room.getId(), roomInfoDto.getId());
        Assertions.assertEquals(room.getName(), roomInfoDto.getName());
        Assertions.assertEquals(room.isVoting(), roomInfoDto.isVoting());
        Assertions.assertEquals(1, roomInfoDto.getGroupInfos().size());

        GroupInfo groupInfo = roomInfoDto.getGroupInfos().get(0);
        Assertions.assertEquals(role.getName(), groupInfo.getName());

        ParticipantInfo participantInfo = groupInfo.getParticipantInfos().get(0);
        Assertions.assertEquals(user.getId(), participantInfo.getId());
        Assertions.assertEquals(user.getFirstName() + " " + user.getLastName(), participantInfo.getName());
        Assertions.assertEquals(participant.getScore(), participantInfo.getScore());
    }

    @Test
    void testGetRoom_userNotInRoom_joinPageWithSecret() {
        String sessionId = UUID.randomUUID().toString();

        ClientSession clientSession = new ClientSession();
        clientSession.setUserId(123);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);

        User user = new User();
        Mockito.when(userService.getUser(clientSession.getUserId())).thenReturn(user);

        long roomId = 42;
        Mockito.when(roomService.getRoom(user, roomId)).thenReturn(null);
        Role role = new Role();
        role.setId(43);
        role.setName("test-name");
        Mockito.when(roomService.getRoles(roomId)).thenReturn(Collections.singletonList(role));
        Mockito.when(roomService.hasSecret(roomId)).thenReturn(true);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        String response = uiController.getRoom(request, String.valueOf(roomId), model);
        Assertions.assertEquals("join", response);

        Mockito.verify(model, Mockito.times(1)).addAttribute("roomId", String.valueOf(roomId));
        Mockito.verify(model, Mockito.times(1)).addAttribute("roles", Collections.singletonList(role));
        Mockito.verify(model, Mockito.times(1)).addAttribute("hasSecret", true);
    }

    @Test
    void testGetRoom_userNotInRoom_joinPageWithRoles() {
        String sessionId = UUID.randomUUID().toString();

        ClientSession clientSession = new ClientSession();
        clientSession.setUserId(123);
        Mockito.when(sessionService.getSession(sessionId)).thenReturn(clientSession);

        User user = new User();
        Mockito.when(userService.getUser(clientSession.getUserId())).thenReturn(user);

        long roomId = 42;
        Mockito.when(roomService.getRoom(user, roomId)).thenReturn(null);
        Role role = new Role();
        role.setId(43);
        role.setName("test-name");
        Mockito.when(roomService.getRoles(roomId)).thenReturn(Arrays.asList(role, role));
        Mockito.when(roomService.hasSecret(roomId)).thenReturn(false);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(CommonConstant.HEADER_SESSION_ID)).thenReturn(sessionId);

        String response = uiController.getRoom(request, String.valueOf(roomId), model);
        Assertions.assertEquals("join", response);

        Mockito.verify(model, Mockito.times(1)).addAttribute("roomId", String.valueOf(roomId));
        Mockito.verify(model, Mockito.times(1)).addAttribute("roles", Arrays.asList(role, role));
        Mockito.verify(model, Mockito.times(1)).addAttribute("hasSecret", false);
    }
}