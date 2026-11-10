package de.kazhal.quizbackend.question;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidQuestionValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidQuestion {
    String message() default "Ungültige Frage";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}