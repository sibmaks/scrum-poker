package com.github.sibmaks.sp.api.response;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import com.github.sibmaks.sp.api.entity.ValidationError;
import lombok.Getter;

import java.util.List;

/**
 * Validation error response.
 * Contains information about field validation error
 *
 * @author sibmaks
 * Created at 26-12-2021
 */
public class ValidationErrorResponse extends StandardResponse {
    @Getter
    private final List<ValidationError> validationErrors;

    /**
     * Create dto with list of validation errors list
     * Result code is ValidationError
     *
     * @see ApiResultCode
     * @param validationErrors validation errors list
     */
    public ValidationErrorResponse(List<ValidationError> validationErrors) {
        super(ApiResultCode.VALIDATION_ERROR.code);
        this.validationErrors = validationErrors;
    }
}
