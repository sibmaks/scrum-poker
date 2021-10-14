package com.github.sibmaks.sp.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Validation error dto
 *
 * @author sibmaks
 * Created at 26-12-2021
 */
@Data
@AllArgsConstructor
public class ValidationError implements Serializable {
    private final String field;
    private final String message;
}
