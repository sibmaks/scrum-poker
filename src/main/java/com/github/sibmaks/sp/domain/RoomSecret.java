package com.github.sibmaks.sp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * @author sibmaks
 * Created at 25-12-2021
 */
@Data
@Entity
@Table(name = "room_secret")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSecret implements Serializable {
    @Id
    @Column(name = "room_id", nullable = false)
    private long roomId;
    @Column(name = "secret_code", nullable = false)
    private String secretCode;
}
