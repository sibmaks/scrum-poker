package com.github.sibmaks.sp.dto;

import com.github.sibmaks.sp.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Room info dto for UI
 *
 * @author sibmaks
 * Created at 14-10-2021
 */
@Data
@AllArgsConstructor
public class RoomInfoDto {
    private final long id;
    private final String name;
    private final boolean voting;
    private final long participants;

    /**
     * Constructor for creating dto by domain entity and participants count
     * @param room domain room
     * @param participants participants count
     */
    public RoomInfoDto(Room room, long participants) {
        this.id = room.getId();
        this.name = room.getName();
        this.voting = room.isVoting();
        this.participants = participants;
    }
}
