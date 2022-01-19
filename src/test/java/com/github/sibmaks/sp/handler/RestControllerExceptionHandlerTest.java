package com.github.sibmaks.sp.handler;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import com.github.sibmaks.sp.api.response.StandardResponse;
import com.github.sibmaks.sp.api.response.ValidationErrorResponse;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.ValidationErrorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

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
    public void testOnException() {
        Exception e = new Exception();
        StandardResponse standardResponse = handler.onException(e);
        Assertions.assertEquals(standardResponse.getResultCode(), ApiResultCode.UNEXPECTED_ERROR.code);
    }

    @Test
    public void testOnMethodArgumentNotValidException() throws NoSuchMethodException {
        class StubValidator {
            public void method(String value) {

            }
        }

        MethodParameter methodParameter = new MethodParameter(StubValidator.class.getDeclaredMethod("method", String.class), 0);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = Mockito.mock(FieldError.class);
        Mockito.when(fieldError.getField()).thenReturn("fieldName");
        Mockito.when(fieldError.getDefaultMessage()).thenReturn("Error message");
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException e = new MethodArgumentNotValidException(methodParameter, bindingResult);
        ValidationErrorResponse response = handler.onMethodArgumentNotValidException(e);
        Assertions.assertEquals(response.getResultCode(), ApiResultCode.VALIDATION_ERROR.code);
        Assertions.assertEquals(1, response.getValidationErrors().size());

        Assertions.assertEquals("fieldName", response.getValidationErrors().get(0).getField());
        Assertions.assertEquals("Error message", response.getValidationErrors().get(0).getMessage());
    }

    @Test
    public void testOnValidationErrorException() {
        ValidationErrorException e = new ValidationErrorException("fieldName", "error message");
        ValidationErrorResponse response = handler.onValidException(e);
        Assertions.assertEquals(response.getResultCode(), ApiResultCode.VALIDATION_ERROR.code);
        Assertions.assertEquals(1, response.getValidationErrors().size());

        Assertions.assertEquals("fieldName", response.getValidationErrors().get(0).getField());
        Assertions.assertEquals("error message", response.getValidationErrors().get(0).getMessage());
    }

    @Test
    public void testOnServiceException() {
        NotFoundException e = new NotFoundException();
        StandardResponse standardResponse = handler.onServiceException(e);
        Assertions.assertEquals(standardResponse.getResultCode(), e.getApiResultCode().code);
    }
}