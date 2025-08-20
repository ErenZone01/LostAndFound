package com.service_annonce.post.models;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class SingleWordListValidator implements ConstraintValidator<SingleWords, List<String>> {
    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null) return true; // ou false selon besoin
        return value.stream().allMatch(word -> word != null && !word.contains(" "));
    }
}