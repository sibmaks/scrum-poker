package com.github.sibmaks.sp.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author sibmaks
 * Created at 12-10-2021
 */
@Data
@Entity
@Table(name = "room_role")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRole implements Serializable {
    @EmbeddedId
    private RoomRoleId roomRoleId;
}
