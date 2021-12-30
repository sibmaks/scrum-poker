package com.github.sibmaks.sp.api.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Common application constants
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonConstant {
    public static final String HEADER_SESSION_ID = "X-Session-Id";
    public static final String REDIRECT_TO_ROOT = "redirect:/";
}
