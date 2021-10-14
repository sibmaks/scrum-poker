package com.github.sibmaks.sp.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Group info dto
 *
 * @author sibmaks
 * Created at 13-10-2021
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupInfo implements Serializable {
    private String name;
    private List<ParticipantInfo> participantInfos;
}
