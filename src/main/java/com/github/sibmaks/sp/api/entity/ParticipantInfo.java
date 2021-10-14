package com.github.sibmaks.sp.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Participant info dto
 *
 * @author sibmaks
 * Created at 13-10-2021
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantInfo implements Serializable {
    private long id;
    private String name;
    private boolean voted;
    private String score;
}
