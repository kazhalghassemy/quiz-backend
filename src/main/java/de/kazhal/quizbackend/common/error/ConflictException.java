package de.kazhal.quizbackend.common.error;

/** Widerspricht dem aktuellen Zustand -> 409 */
public abstract class ConflictException extends QuizException {
    protected ConflictException(String code, String message) {
        super(code, message);
    }
}