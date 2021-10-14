package com.github.sibmaks.sp.exception;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Abstract service exception
 *
 * @author sibmaks
 * Created at 14-10-2021
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class ServiceException extends RuntimeException {
    private final ApiResultCode apiResultCode;
}
