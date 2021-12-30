package com.github.sibmaks.sp.api.response;

import com.github.sibmaks.sp.api.constant.ApiResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Standard application response, should always contain result code.
 * By default, for successful response "Ok" code will be returned.
 * @see ApiResultCode
 *
 * @author sibmaks
 * Created at 14-10-2021
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class StandardResponse implements Serializable {
    private final String resultCode;

    public StandardResponse() {
        this.resultCode = ApiResultCode.OK.code;
    }
}
