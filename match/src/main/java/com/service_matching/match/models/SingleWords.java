package com.service_matching.match.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SingleWordListValidator.class)
public @interface SingleWords {
    String message() default "Chaque mot doit être un seul mot sans espaces";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


