package com.github.sibmaks.sp.handler;

import com.github.sibmaks.sp.exception.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author drobyshev-ma
 * Created at 19-01-2022
 */
class UIControllerExceptionHandlerTest {
    private UIControllerExceptionHandler handler;

    @BeforeEach
    public void setUp() {
        handler = new UIControllerExceptionHandler();
    }

    @Test
    void testHandleUnauthorizedException() {
        UnauthorizedException exception = new UnauthorizedException();
        Assertions.assertEquals("redirect:/", handler.onUnauthorizedException(exception));
    }
}