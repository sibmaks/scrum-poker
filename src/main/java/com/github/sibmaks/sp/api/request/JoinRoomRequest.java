package com.github.sibmaks.sp.api.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * Join room request
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JoinRoomRequest extends StandardRequest {
    @Min(1)
    private long roomId;
    @Min(1)
    private int roleId;
    private String secretCode;
}
