package de.kazhal.quizbackend.common.error;

/** Fachlich unzulässige Anfrage -> 400 */
public abstract class BadRequestException extends QuizException {
    protected BadRequestException(String code, String message) {
        super(code, message);
    }
}