package com.github.sibmaks.sp.repository;

import com.github.sibmaks.sp.domain.Room;
import com.github.sibmaks.sp.domain.RoomRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Room roles repository interface
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
public interface RoomRoleRepository extends JpaRepository<RoomRole, Integer> {
    /**
     * Method for getting list of room's roles.
     *
     * @param room room domain
     * @return list of room roles
     * @see RoomRole
     */
    List<RoomRole> findAllByRoomRoleIdRoom(Room room);
}
