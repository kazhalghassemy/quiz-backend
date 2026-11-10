package de.kazhal.quizbackend.common.error;

/** Basis aller fachlichen Fehler der Quiz-App. */
public abstract class QuizException extends RuntimeException {
    private final String code;
    protected QuizException(String code, String message) {
        super(message);
        this.code = code;
    }
    public String getCode() {
        return code;
    }
}