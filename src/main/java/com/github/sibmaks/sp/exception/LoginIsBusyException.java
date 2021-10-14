package com.github.sibmaks.sp.exception;

import com.github.sibmaks.sp.api.constant.ApiResultCode;

/**
 * Login is busy service exception.
 * LoginIsBusy result code used
 *
 * @see ApiResultCode
 *
 * @author sibmaks
 * Created at 26-12-2021
 */
public class LoginIsBusyException extends ServiceException {
    /**
     * Default exception constructor
     */
    public LoginIsBusyException() {
        super(ApiResultCode.LOGIN_IS_BUSY);
    }
}
