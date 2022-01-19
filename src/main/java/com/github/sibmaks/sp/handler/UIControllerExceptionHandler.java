package com.github.sibmaks.sp.handler;

import com.github.sibmaks.sp.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler for ui controllers.
 * Should return link to error page or redirect link.
 *
 * @author sibmaks
 * Created at 28-09-2021
 */
@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class UIControllerExceptionHandler {
    /**
     * Method for handling unauthorized exception
     * Redirect client to root page.
     *
     * @param e unauthorized exception
     * @return redirect link to root page
     */
    @ExceptionHandler(UnauthorizedException.class)
    public String onUnauthorizedException(UnauthorizedException e) {
        log.warn("Unauthorized access", e);
        return "redirect:/";
    }
}
