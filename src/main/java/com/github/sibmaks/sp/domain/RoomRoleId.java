package com.github.sibmaks.sp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author sibmaks
 * Created at 12-10-2021
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RoomRoleId implements Serializable {
    @ManyToOne
    private Room room;
    @ManyToOne
    private Role role;
}
