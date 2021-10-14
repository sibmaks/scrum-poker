package com.github.sibmaks.sp.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Vote room request
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VoteRoomRequest extends StandardRequest {
    @Min(1)
    private long roomId;
    @NotEmpty
    @Size(min = 1, max = 3)
    private String score;
}
