package com.github.sibmaks.sp.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Update user request
 *
 * @author sibmaks
 * Created at 26-12-2021
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateUserRequest extends StandardRequest {
    @NotEmpty
    @Size(min = 1, max = 128)
    private String firstName;
    @NotEmpty
    @Size(min = 1, max = 128)
    private String lastName;
}
