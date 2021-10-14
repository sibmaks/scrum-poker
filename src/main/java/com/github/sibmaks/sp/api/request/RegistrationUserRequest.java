package com.github.sibmaks.sp.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Registration user request
 *
 * @author sibmaks
 * Created at 25-12-2021
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegistrationUserRequest extends StandardRequest {
    @NotEmpty
    @Email(message = "Login should be valid email", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    @Size(min = 8, max = 128)
    private String login;
    @NotEmpty
    @Size(min = 8, max = 128)
    private String password;
    @NotEmpty
    @Size(min = 1, max = 128)
    private String firstName;
    @NotEmpty
    @Size(min = 1, max = 128)
    private String lastName;
}
