package com.github.sibmaks.sp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
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
