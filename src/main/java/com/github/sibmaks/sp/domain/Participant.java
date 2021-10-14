package com.github.sibmaks.sp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
public class Participant {
    @EmbeddedId
    private ParticipantId participantId;
    @ManyToOne
    private Role role;
    @Column(name = "score")
    private String score;
}
