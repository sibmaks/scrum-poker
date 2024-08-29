package com.github.sibmaks.sp.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
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
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantId implements Serializable {
    @ManyToOne
    private User user;
    @ManyToOne
    private Room room;
}
