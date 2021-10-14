package com.github.sibmaks.sp.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Change user password request
 *
 * @author sibmaks
 * Created at 26-12-2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChangePasswordRequest extends StandardRequest {
    @NotEmpty
    @Size(min = 8, max = 128)
    private String password;
}
