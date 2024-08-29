package com.github.sibmaks.sp.handler;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.ValidationErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

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
        var e = new Exception();
        var standardResponse = handler.onException(e);
        assertEquals(standardResponse.getResultCode(), ApiResultCode.UNEXPECTED_ERROR.code);
    }

    @Test
    void testOnMethodArgumentNotValidException() throws NoSuchMethodException {
        class StubValidator {
            public void method(String value) {

            }
        }

        var methodParameter = new MethodParameter(
                StubValidator.class.getDeclaredMethod("method", String.class),
                0
        );
        var bindingResult = mock(BindingResult.class);
        var fieldError = mock(FieldError.class);
        when(fieldError.getField()).
                thenReturn("fieldName");
        when(fieldError.getDefaultMessage()).
                thenReturn("Error message");
        when(bindingResult.getFieldErrors()).
                thenReturn(Collections.singletonList(fieldError));

        var e = new MethodArgumentNotValidException(methodParameter, bindingResult);
        var response = handler.onMethodArgumentNotValidException(e);
        assertEquals(response.getResultCode(), ApiResultCode.VALIDATION_ERROR.code);

        var validationErrors = response.getValidationErrors();
        assertEquals(1, validationErrors.size());

        var validationError = validationErrors.getFirst();
        assertEquals("fieldName", validationError.getField());
        assertEquals("Error message", validationError.getMessage());
    }

    @Test
    void testOnValidationErrorException() {
        var e = new ValidationErrorException("fieldName", "error message");
        var response = handler.onValidException(e);
        assertEquals(response.getResultCode(), ApiResultCode.VALIDATION_ERROR.code);

        var validationErrors = response.getValidationErrors();
        assertEquals(1, validationErrors.size());

        var validationError = validationErrors.getFirst();
        assertEquals("fieldName", validationError.getField());
        assertEquals("error message", validationError.getMessage());
    }

    @Test
    void testOnServiceException() {
        var e = new NotFoundException();
        var standardResponse = handler.onServiceException(e);
        assertEquals(standardResponse.getResultCode(), e.getApiResultCode().code);
    }
}