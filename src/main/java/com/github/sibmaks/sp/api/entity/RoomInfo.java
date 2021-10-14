package com.github.sibmaks.sp.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Room info dto
 *
 * @author sibmaks
 * Created at 13-10-2021
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfo implements Serializable {
    private long id;
    private String name;
    private boolean voting;
    private boolean author;
    private String score;
    private List<GroupInfo> groupInfos;
}
