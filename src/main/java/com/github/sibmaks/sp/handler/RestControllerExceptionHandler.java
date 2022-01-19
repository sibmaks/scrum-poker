package com.github.sibmaks.sp.handler;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import com.github.sibmaks.sp.api.entity.ValidationError;
import com.github.sibmaks.sp.api.response.StandardResponse;
import com.github.sibmaks.sp.api.response.ValidationErrorResponse;
import com.github.sibmaks.sp.exception.ServiceException;
import com.github.sibmaks.sp.exception.ValidationErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception handler for rest controllers.
 * Should return {@link StandardResponse} in Json format.
 *
 * @author sibmaks
 * Created at 28-09-2021
 */
@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class RestControllerExceptionHandler {
    /**
     * Method for handling generic exception
     *
     * @param e exception object
     * @return standard response with UnexpectedError
     */
    @ResponseStatus(value= HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public StandardResponse onException(Exception e) {
        log.error(e.getMessage(), e);
        return new StandardResponse(ApiResultCode.UNEXPECTED_ERROR.code);
    }

    /**
     * Method for handling request validation exception
     *
     * @param e exception object
     * @return {@link ValidationErrorResponse} with information about field issues
     */
    @ResponseStatus(value= HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn(e.getMessage(), e);
        List<ValidationError> validationErrors = e.getBindingResult().getFieldErrors().stream()
                .map(it -> new ValidationError(it.getField(), it.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ValidationErrorResponse(validationErrors);
    }

    /**
     * Method for handling request validation exception
     *
     * @param e exception object
     * @return {@link ValidationErrorResponse} with information about field issues
     */
    @ResponseStatus(value= HttpStatus.OK)
    @ExceptionHandler(ValidationErrorException.class)
    @ResponseBody
    public ValidationErrorResponse onValidException(ValidationErrorException e) {
        log.warn(e.getMessage(), e);
        return new ValidationErrorResponse(e.getValidationErrors());
    }

    /**
     * Method for handling service exception
     *
     * @param e exception object
     * @return {@link StandardResponse} with result code from exception
     */
    @ResponseStatus(value= HttpStatus.OK)
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public StandardResponse onServiceException(ServiceException e) {
        log.warn(e.getMessage(), e);
        return new StandardResponse(e.getApiResultCode().code);
    }
}
