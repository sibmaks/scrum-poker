package com.github.sibmaks.sp.exception;

import com.github.sibmaks.sp.api.constant.ApiResultCode;

/**
 * Action is not allowed for user service exception.
 * NotAllowed result code used
 *
 * @see ApiResultCode
 *
 * @author sibmaks
 * Created at 14-10-2021
 */
public class NotAllowedException extends ServiceException {
    /**
     * Default exception constructor
     */
    public NotAllowedException() {
        super(ApiResultCode.NOT_ALLOWED);
    }
}
