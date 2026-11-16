package de.kazhal.quizbackend.user;

import de.kazhal.quizbackend.common.error.ConflictException;

public class EmailAlreadyRegisteredException extends ConflictException {
    public EmailAlreadyRegisteredException(String email) {
        super("EMAIL_TAKEN", "Die E-Mail-Adresse " + email + " ist bereits registriert");
    }
}
