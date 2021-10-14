package com.github.sibmaks.sp.repository;

import com.github.sibmaks.sp.domain.RoomSecret;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Room secret repository interface
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
public interface RoomSecretRepository extends JpaRepository<RoomSecret, Long> {
}
