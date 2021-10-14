package com.github.sibmaks.sp.api.constant;

/**
 * Enum of Rest result codes
 *
 * @author sibmaks
 * Created at 14-10-2021
 */
public enum ApiResultCode {
    OK("Ok"),
    UNEXPECTED_ERROR("UnexpectedError"),
    UNAUTHORIZED("Unauthorized"),
    NOT_ALLOWED("NotAllowed"),
    NOT_FOUND("NotFound"),
    VALIDATION_ERROR("ValidationError"),
    WRONG_SECRET_CODE("WrongSecretCode"),
    LOGIN_IS_BUSY("LoginIsBusy");

    /**
     * Rest result code
     */
    public final String code;

    ApiResultCode(String code) {
        this.code = code;
    }
}
