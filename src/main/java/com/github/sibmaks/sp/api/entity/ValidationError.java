package com.github.sibmaks.sp.api.entity;

import java.io.Serializable;

/**
 * Validation error dto
 *
 * @author sibmaks
 * Created at 26-12-2021
 */
public record ValidationError(String field, String message) implements Serializable {
}
