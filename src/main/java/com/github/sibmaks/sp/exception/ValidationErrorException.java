package com.github.sibmaks.sp.exception;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import com.github.sibmaks.sp.api.entity.ValidationError;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Invalid input data is entered service exception.
 * ValidationError result code used
 *
 * @see ApiResultCode
 *
 * @author sibmaks
 * Created at 14-10-2021
 */
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorException extends ServiceException {
    @Getter
    private final List<ValidationError> validationErrors;

    /**
     *  Constructor for creating exception with single wrong field
     *
     * @param field filed name
     * @param message message for client
     */
    public ValidationErrorException(String field, String message) {
        super(ApiResultCode.VALIDATION_ERROR);
        this.validationErrors = Collections.singletonList(new ValidationError(field, message));
    }
}
