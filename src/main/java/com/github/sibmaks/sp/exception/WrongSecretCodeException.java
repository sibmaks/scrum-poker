package com.github.sibmaks.sp.exception;

import com.github.sibmaks.sp.api.constant.ApiResultCode;

/**
 * Wrong secret code is entered service exception.
 * WrongSecretCode result code used
 *
 * @see ApiResultCode
 *
 * @author sibmaks
 * Created at 25-12-2021
 */
public class WrongSecretCodeException extends ServiceException {
    /**
     * Default exception constructor
     */
    public WrongSecretCodeException() {
        super(ApiResultCode.WRONG_SECRET_CODE);
    }
}
