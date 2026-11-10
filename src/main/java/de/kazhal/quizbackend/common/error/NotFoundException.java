package de.kazhal.quizbackend.common.error;

/** Etwas existiert nicht -> 404 */
public abstract class NotFoundException extends QuizException {
    protected NotFoundException(String code, String message) {
        super(code, message);
    }
}