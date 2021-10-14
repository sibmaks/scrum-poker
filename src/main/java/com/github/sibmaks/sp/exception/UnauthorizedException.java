package com.github.sibmaks.sp.exception;

import com.github.sibmaks.sp.api.constant.ApiResultCode;

/**
 * User is unauthorized service exception.
 * Unauthorized result code used
 *
 * @see ApiResultCode
 *
 * @author sibmaks
 * Created at 14-10-2021
 */
public class UnauthorizedException extends ServiceException {
    /**
     * Default exception constructor
     */
    public UnauthorizedException() {
        super(ApiResultCode.UNAUTHORIZED);
    }
}
