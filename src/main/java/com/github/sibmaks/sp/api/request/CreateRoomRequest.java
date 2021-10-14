package com.github.sibmaks.sp.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Create room request
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateRoomRequest extends StandardRequest {
    @NotEmpty
    @Size(min = 4, max = 128)
    private String name;
    private String secretCode;
    @NotEmpty
    private List<Integer> roles;
    @Min(1)
    private int days;
    @Min(1)
    private int roleId;
}
