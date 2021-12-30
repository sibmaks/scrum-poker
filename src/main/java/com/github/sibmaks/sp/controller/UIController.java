package com.github.sibmaks.sp.controller;

import com.github.sibmaks.sp.api.constant.CommonConstant;
import com.github.sibmaks.sp.api.response.GetRoomResponse;
import com.github.sibmaks.sp.domain.*;
import com.github.sibmaks.sp.dto.RoomInfoDto;
import com.github.sibmaks.sp.service.RoomService;
import com.github.sibmaks.sp.service.SessionService;
import com.github.sibmaks.sp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.sibmaks.sp.api.constant.CommonConstant.REDIRECT_TO_ROOT;

/**
 * HTTP controller for getting moving around the app pages
 *
 * @author sibmaks
 * Created at 14-10-2021
 */
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UIController {
    private final UserService userService;
    private final RoomService roomService;
    private final SessionService sessionService;

    /**
     * Index page, redirect on rooms page if client is authorized.
     * Otherwise, authorization page will be returned.
     *
     * @param request http servlet request
     * @return redirect link or index page
     */
    @GetMapping("/")
    public String index(HttpServletRequest request) {
        String sessionId = getSessionId(request);
        if(sessionService.isAuthorized(sessionId)) {
            return "redirect:/rooms";
        }
        return "index";
    }

    /**
     * Registration page, redirect to index page if session is exists.
     * Otherwise, client will see registration page.
     *
     * @param request http servlet request
     * @return redirect link or registration page
     */
    @GetMapping("/reg")
    public String showRegistrationForm(HttpServletRequest request) {
        String sessionId = getSessionId(request);
        if(sessionService.isAuthorized(sessionId)) {
            return REDIRECT_TO_ROOT;
        }
        return "registration";
    }

    /**
     * Account page, redirect to index page if session is not exists.
     * Otherwise, client will see account change page.
     *
     * @param request http servlet request
     * @param model spring model info
     * @return account page
     */
    @GetMapping("/account")
    public String showAccountForm(HttpServletRequest request, Model model) {
        String sessionId = getSessionId(request);
        ClientSession session = sessionService.getSession(sessionId);
        User user = userService.getUser(session.getUserId());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        return "account";
    }

    /**
     * Rooms list page, redirect to index page if session is not exists.
     * Otherwise, client will see page with rooms list.
     *
     * @param request http servlet request
     * @param model spring model info
     * @return rooms page
     */
    @GetMapping("/rooms")
    public String getRooms(HttpServletRequest request, Model model) {
        String sessionId = getSessionId(request);
        ClientSession session = sessionService.getSession(sessionId);
        List<RoomInfoDto> rooms = roomService.getRooms(session.getUserId()).stream()
                .map(it -> new RoomInfoDto(it, roomService.getParticipantCount(it)))
                .collect(Collectors.toList());
        model.addAttribute("rooms", rooms);
        return "rooms";
    }

    /**
     * Room creation page, redirect to index page if session is not exists.
     * Otherwise, client will see room creation page.
     *
     * @param request http servlet request
     * @param model spring model info
     * @return room creation page
     */
    @GetMapping("/room/create")
    public String createRoom(HttpServletRequest request, Model model) {
        String sessionId = getSessionId(request);
        if(sessionService.isAuthorized(sessionId)) {
            List<Role> roles = roomService.getRoles();
            model.addAttribute("roles", roles);
            return "create";
        }
        return REDIRECT_TO_ROOT;
    }

    /**
     * Room info page, redirect to index page if session is not exists.
     * Otherwise, client will see page with room info.
     * If user not belong to room then join page will be returned.
     * User can be automatically join to room if
     *
     * @param request http servlet request
     * @param roomIdParam room identifier
     * @param model spring model info
     * @return room info page or join page
     */
    @GetMapping("/room/{roomId}")
    public String getRoom(HttpServletRequest request, @PathVariable("roomId") String roomIdParam, Model model) {
        String sessionId = getSessionId(request);
        long roomId;
        try {
            roomId = Long.parseLong(roomIdParam);
        } catch (Exception e) {
            return REDIRECT_TO_ROOT;
        }
        ClientSession session = sessionService.getSession(sessionId);
        User user = userService.getUser(session.getUserId());
        Room room = roomService.getRoom(user, roomId);
        if (room == null) {
            List<Role> roles = roomService.getRoles(roomId);
            if (roles == null || roles.isEmpty()) {
                return REDIRECT_TO_ROOT;
            } else {
                boolean hasSecret = roomService.hasSecret(roomId);
                if(roles.size() == 1 && !hasSecret) {
                    Role role = roles.get(0);
                    room = roomService.joinRoom(user, roomId, role.getId(), null);
                } else {
                    model.addAttribute("roomId", roomIdParam);
                    model.addAttribute("roles", roles);
                    model.addAttribute("hasSecret", hasSecret);
                    return "join";
                }
            }
        }
        String secret = roomService.getSecret(user, room);
        List<Participant> participants = roomService.getParticipants(room);
        GetRoomResponse response = new GetRoomResponse(user, room, participants);
        model.addAttribute("roomInfo", response.getRoomInfo());
        model.addAttribute("secretCode", secret);
        return "room";
    }

    /**
     * Get session identifier from http request.
     * Looking for session id in headers and cookies
     *
     * @param request http servlet request
     * @return session identifier
     */
    private static String getSessionId(HttpServletRequest request) {
        String header = request.getHeader(CommonConstant.HEADER_SESSION_ID);
        if(header == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if(CommonConstant.HEADER_SESSION_ID.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return header;
    }
}
