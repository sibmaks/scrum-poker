package com.github.sibmaks.sp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * @author sibmaks
 * Created at 12-10-2021
 */
@Data
@Entity
@Table(name = "participant")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant implements Serializable {
    @EmbeddedId
    private ParticipantId participantId;
    @ManyToOne
    private Role role;
    @Column(name = "score")
    private String score;
}
