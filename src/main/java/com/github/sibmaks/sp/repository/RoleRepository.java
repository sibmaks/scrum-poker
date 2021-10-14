package com.github.sibmaks.sp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import com.github.sibmaks.sp.domain.Role;

import java.util.List;

/**
 * Role repository interface
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
public interface RoleRepository extends Repository<Role, Integer> {
    /**
     * Method for getting list of roles by roles identifiers
     *
     * @param roles roles identifiers
     * @return list of roles
     */
    List<Role> findAllByIdIn(List<Integer> roles);

    /**
     * Method for getting list of roles allowed for specific room
     *
     * @param roomId room identifier
     * @return list of roles
     */
    @Query("select distinct rr.roomRoleId.role from RoomRole rr where rr.roomRoleId.room.id = ?1")
    List<Role> findAllForRoom(long roomId);

    /**
     * Method for getting all available roles
     *
     * @return list of roles
     */
    List<Role> findAllByOrderById();
}
