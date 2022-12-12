package com.exercice.upstreampay.controller.validator;

public interface Validator<T> {
    /**
     * Validate.
     *
     * @param target the target
     */
    void validate(T target);
}