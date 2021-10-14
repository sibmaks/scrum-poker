package com.github.sibmaks.sp.api.response;

import lombok.Getter;
import com.github.sibmaks.sp.domain.Room;

/**
 * Create room response
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
public class CreateRoomResponse extends StandardResponse {
    @Getter
    private final long roomId;

    public CreateRoomResponse(Room room) {
        this.roomId = room.getId();
    }
}
