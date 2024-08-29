package com.github.sibmaks.sp.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

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
