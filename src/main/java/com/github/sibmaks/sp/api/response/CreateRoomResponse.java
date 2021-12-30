package com.github.sibmaks.sp.api.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import com.github.sibmaks.sp.domain.Room;

/**
 * Create room response
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
@EqualsAndHashCode(callSuper = true)
public class CreateRoomResponse extends StandardResponse {
    @Getter
    private final long roomId;

    public CreateRoomResponse(Room room) {
        this.roomId = room.getId();
    }
}
