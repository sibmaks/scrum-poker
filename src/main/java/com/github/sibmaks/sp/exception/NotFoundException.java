package com.github.sibmaks.sp.exception;

import com.github.sibmaks.sp.api.constant.ApiResultCode;

/**
 * Requested item not found service exception.
 * NotFound result code used
 *
 * @see ApiResultCode
 *
 * @author sibmaks
 * Created at 14-10-2021
 */
public class NotFoundException extends ServiceException {
    /**
     * Default exception constructor
     */
    public NotFoundException() {
        super(ApiResultCode.NOT_FOUND);
    }
}
