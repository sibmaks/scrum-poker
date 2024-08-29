package com.github.sibmaks.sp.repository;

import com.github.sibmaks.sp.domain.ClientSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Client session repository interface
 *
 * @author sibmaks
 * Created at 25-12-2021
 */
public interface ClientSessionRepository extends JpaRepository<ClientSession, String> {
    /**
     * Method for removing all invalid (expired) sessions
     *
     * @return number of deleted sessions
     */
    @Transactional
    @Modifying
    @Query("delete from ClientSession where validTo <= CURRENT_TIMESTAMP")
    int deleteExpired();
}
