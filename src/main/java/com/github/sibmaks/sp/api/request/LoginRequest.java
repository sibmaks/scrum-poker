package com.github.sibmaks.sp.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Login request
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoginRequest extends StandardRequest {
    @NotEmpty
    @Email(message = "Login should be valid email", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    @Size(min = 8, max = 128)
    private final String login;
    @NotEmpty
    @Size(min = 8, max = 128)
    private final String password;
}
