package com.github.sibmaks.sp.controller;

import com.github.sibmaks.sp.api.constant.CommonConstant;
import com.github.sibmaks.sp.api.request.*;
import com.github.sibmaks.sp.api.response.*;
import com.github.sibmaks.sp.domain.ClientSession;
import com.github.sibmaks.sp.domain.Participant;
import com.github.sibmaks.sp.domain.Room;
import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.ValidationErrorException;
import com.github.sibmaks.sp.service.RoomService;
import com.github.sibmaks.sp.service.SessionService;
import com.github.sibmaks.sp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rest controller for operation with rooms
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
@RestController
@RequestMapping("/api/room/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoomController {
    private final SessionService sessionService;
    private final UserService userService;
    private final RoomService roomService;

    /**
     * Api endpoint for room creation.
     * In case if session not exists or unauthorized then Unauthorized result code will be returned.
     *
     * @param sessionId session identifier
     * @param request API request DTO {@link CreateRoomRequest}
     * @see CreateRoomResponse
     * @return response with new room identifier or response with error description
     */
    @PostMapping(value = "createRoom", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StandardResponse createRoom(@RequestHeader(CommonConstant.HEADER_SESSION_ID) String sessionId,
                                       @RequestBody @Validated CreateRoomRequest request) {
        ClientSession session = sessionService.getSession(sessionId);
        User user = userService.getUser(session.getUserId());
        String secretCode = request.getSecretCode();
        if(secretCode != null && (secretCode.length() < 4 || secretCode.length() > 128)) {
            throw new ValidationErrorException("secretCode", "size must be between 4 and 128");
        }
        Room room = roomService.createRoom(user, request.getName(), secretCode, request.getRoles(), request.getDays(),
                request.getRoleId());
        return new CreateRoomResponse(room);
    }

    /**
     * Api endpoint for join user to specific room.
     * In case if session not exists or unauthorized then Unauthorized result code will be returned.
     *
     * @param sessionId session identifier
     * @param request API request DTO {@link JoinRoomRequest}
     * @see JoinRoomResponse
     * @return response with identifier of room or response with error description
     */
    @PostMapping(value = "join", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StandardResponse join(@RequestHeader(CommonConstant.HEADER_SESSION_ID) String sessionId,
                                       @RequestBody @Validated JoinRoomRequest request) {
        ClientSession session = sessionService.getSession(sessionId);
        User user = userService.getUser(session.getUserId());
        Room room = roomService.joinRoom(user, request.getRoomId(), request.getRoleId(), request.getSecretCode());
        return new JoinRoomResponse(room);
    }

    /**
     * Api endpoint for leaving user from specific room.
     * After successful execution empty response will be returned.
     *
     * In case if session not exists or unauthorized then Unauthorized result code will be returned.
     *
     * @param sessionId session identifier
     * @param request API request DTO {@link LeaveRoomRequest}
     * @see StandardResponse
     * @return empty response or response with error description
     */
    @PostMapping(value = "leave", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StandardResponse leave(@RequestHeader(CommonConstant.HEADER_SESSION_ID) String sessionId,
                                       @RequestBody @Validated LeaveRoomRequest request) {
        ClientSession session = sessionService.getSession(sessionId);
        User user = userService.getUser(session.getUserId());
        roomService.leaveRoom(user, request.getRoomId());
        return new StandardResponse();
    }

    /**
     * Api endpoint for user voting in specific room.
     * User should belong to the room, NotAllowed result code will be returned otherwise.
     * After successful execution empty response will be returned.
     *
     * In case if session not exists or unauthorized then Unauthorized result code will be returned.
     *
     * @param sessionId session identifier
     * @param request API request DTO {@link VoteRoomRequest}
     * @see StandardResponse
     * @return empty response or response with error description
     */
    @PostMapping(value = "vote", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StandardResponse vote(@RequestHeader(CommonConstant.HEADER_SESSION_ID) String sessionId,
                                       @RequestBody @Validated VoteRoomRequest request) {
        ClientSession session = sessionService.getSession(sessionId);
        User user = userService.getUser(session.getUserId());
        roomService.vote(user, request.getRoomId(), request.getScore());
        return new StandardResponse();
    }

    /**
     * Api endpoint for changing vote status of room.
     * User should be an author of room, NotAllowed result code will be returned otherwise.
     * After successful execution room info will be returned.
     *
     * In case if session not exists or unauthorized then Unauthorized result code will be returned.
     *
     * @param sessionId session identifier
     * @param request API request DTO {@link SetVotingRoomRequest}
     * @see GetRoomResponse
     * @return response with room info or response with error description
     */
    @PostMapping(value = "setVoting", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StandardResponse setVoting(@RequestHeader(CommonConstant.HEADER_SESSION_ID) String sessionId,
                                       @RequestBody @Validated SetVotingRoomRequest request) {
        ClientSession session = sessionService.getSession(sessionId);
        User user = userService.getUser(session.getUserId());
        Room room = roomService.setVoting(user, request.getRoomId(), request.isVoting());
        List<Participant> participants = roomService.getParticipants(room);
        return new GetRoomResponse(user, room, participants);
    }

    /**
     * Api endpoint for get info about room.
     * User should belong to the room, NotFound result code will be returned otherwise.
     * After successful execution room info will be returned.
     *
     * In case if session not exists or unauthorized then Unauthorized result code will be returned.
     *
     * @param sessionId session identifier
     * @param request API request DTO {@link GetRoomRequest}
     * @see GetRoomResponse
     * @return response with room info or response with error description
     */
    @PostMapping(value = "getRoom", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StandardResponse getRoom(@RequestHeader(CommonConstant.HEADER_SESSION_ID) String sessionId,
                                    @RequestBody @Validated GetRoomRequest request) {
        ClientSession session = sessionService.getSession(sessionId);
        User user = userService.getUser(session.getUserId());
        Room room = roomService.getRoom(user, request.getRoomId());
        if(room == null) {
            throw new NotFoundException();
        }
        List<Participant> participants = roomService.getParticipants(room);
        return new GetRoomResponse(user, room, participants);
    }
}
