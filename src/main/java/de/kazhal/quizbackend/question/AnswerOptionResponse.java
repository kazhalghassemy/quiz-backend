package de.kazhal.quizbackend.question;

/**
 * AnswerOptionResponse ist keine Entity, sondern nur ein
 * Transportbehälter für die Antwort an den Client.
 * Eine Tabelle braucht sie nicht.
 *
 * Der Unterschied
 * 	            AnswerOption	        AnswerOptionResponse
 * Art	        Entity (@Entity)	    Record (DTO)
 * Tabelle	    ✅ answer_option	    ❌ keine
 * Lebt wo?	    in der Datenbank,       dauerhaft	nur im Arbeitsspeicher, für eine Anfrage
 * Wofür?	    Daten speichern	        Daten als JSON an den Client schicken
 *
 * Nur Klassen mit @Entity bekommen eine Tabelle.
 * Dein Record hat keine Annotation, deshalb interessiert sich Hibernate
 * gar nicht für ihn.
 * @param id
 * @param text
 * @param correct
 */
public record AnswerOptionResponse(Long id, String text, boolean correct) {
    public static AnswerOptionResponse from(AnswerOption option) {
        return new AnswerOptionResponse(option.getId(), option.getText(), option.isCorrect());
    }
}