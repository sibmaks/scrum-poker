package com.github.sibmaks.sp.repository;

import com.github.sibmaks.sp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * User repository interface
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find user by login
     *
     * @param login user login
     * @return user domain or null
     */
    @Query("select u from User u where lower(u.login) = lower(:login)")
    User findByLogin(@Param("login") String login);

    /**
     *  Check is client exists in DB with passed login or not
     *
     * @param login user login
     * @return true if user exists or false otherwise
     */
    @Query("select count(u) > 0 from User u where lower(u.login) = lower(:login)")
    boolean existsByLogin(String login);
}
