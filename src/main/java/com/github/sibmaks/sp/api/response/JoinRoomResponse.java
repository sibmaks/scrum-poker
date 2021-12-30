package com.github.sibmaks.sp.api.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import com.github.sibmaks.sp.domain.Room;

/**
 * Join room response
 *
 * @author sibmaks
 * Created at 13-10-2021
 */
@EqualsAndHashCode(callSuper = true)
public class JoinRoomResponse extends StandardResponse {
    @Getter
    private final long roomId;

    /**
     * Create dto by domain room
     * @param room domain room
     */
    public JoinRoomResponse(Room room) {
        this.roomId = room.getId();
    }
}
