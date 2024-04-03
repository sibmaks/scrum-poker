package com.github.sibmaks.sp.handler;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import com.github.sibmaks.sp.api.entity.ValidationError;
import com.github.sibmaks.sp.api.response.StandardResponse;
import com.github.sibmaks.sp.api.response.ValidationErrorResponse;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.ValidationErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author drobyshev-ma
 * Created at 19-01-2022
 */
class RestControllerExceptionHandlerTest {
    private RestControllerExceptionHandler handler;

    @BeforeEach
    public void setUp() {
        handler = new RestControllerExceptionHandler();
    }

    @Test
    void testOnException() {
        Exception e = new Exception();
        StandardResponse standardResponse = handler.onException(e);
        assertEquals(standardResponse.getResultCode(), ApiResultCode.UNEXPECTED_ERROR.code);
    }

    @Test
    void testOnMethodArgumentNotValidException() throws NoSuchMethodException {
        class StubValidator {
            public void method(String value) {

            }
        }

        MethodParameter methodParameter = new MethodParameter(
                StubValidator.class.getDeclaredMethod("method", String.class),
                0
        );
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).
                thenReturn("fieldName");
        when(fieldError.getDefaultMessage()).
                thenReturn("Error message");
        when(bindingResult.getFieldErrors()).
                thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException e = new MethodArgumentNotValidException(methodParameter, bindingResult);
        ValidationErrorResponse response = handler.onMethodArgumentNotValidException(e);
        assertEquals(response.getResultCode(), ApiResultCode.VALIDATION_ERROR.code);

        List<ValidationError> validationErrors = response.getValidationErrors();
        assertEquals(1, validationErrors.size());

        ValidationError validationError = validationErrors.get(0);
        assertEquals("fieldName", validationError.getField());
        assertEquals("Error message", validationError.getMessage());
    }

    @Test
    void testOnValidationErrorException() {
        ValidationErrorException e = new ValidationErrorException("fieldName", "error message");
        ValidationErrorResponse response = handler.onValidException(e);
        assertEquals(response.getResultCode(), ApiResultCode.VALIDATION_ERROR.code);

        List<ValidationError> validationErrors = response.getValidationErrors();
        assertEquals(1, validationErrors.size());

        ValidationError validationError = validationErrors.get(0);
        assertEquals("fieldName", validationError.getField());
        assertEquals("error message", validationError.getMessage());
    }

    @Test
    void testOnServiceException() {
        NotFoundException e = new NotFoundException();
        StandardResponse standardResponse = handler.onServiceException(e);
        assertEquals(standardResponse.getResultCode(), e.getApiResultCode().code);
    }
}