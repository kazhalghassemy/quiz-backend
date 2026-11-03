package de.kazhal.quizbackend.common;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(List<T> content, // die Einträge dieser Seite
                              int page, // welche Seite (beginnt bei 0!)
                              int size, // wie viele pro Seite
                              long totalElements,// wie viele Einträge es insgesamt gibt
                              int totalPages // wie viele Seiten es insgesamt gibt
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(),
                page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
/**
 * Kurz gesagt
 * <p>
 * PageResponse ist eine Verpackung für eine Seite voller Ergebnisse. Sie legt fest, wie die Seite als JSON beim Client
 * ankommt, also später bei deinem Angular-Frontend.
 * <p>
 * Das Problem dahinter
 * <p>
 * Wenn du GET /api/topics?page=0&size=1 aufrufst, willst du nicht alle Themen, sondern nur eine Seite.
 * Der Client braucht dann zwei Dinge:
 * <p>
 * die Daten dieser Seite
 * Infos zum Blättern, also auf welcher Seite er ist und wie viele es insgesamt gibt
 */

/**
 * Spring Data liefert dir dafür ein Page<T>-Objekt. Das ist aber eine interne Spring-Klasse.
 * Ihr JSON-Format ist unübersichtlich und kann sich mit jeder Spring-Version ändern,
 * deshalb warnt Spring selbst davor, sie direkt zurückzugeben.
 * Also übernimmst du nur die fünf Werte, die du brauchst, in deinen eigenen Record.
 */
/**
 * T ist ein Platzhalter für einen Typ (Generics). Der Record weiß nicht, was auf der Seite liegt, und muss es auch nicht wissen:
 *
 * PageResponse<TopicResponse> ist eine Seite mit Themen
 * PageResponse<QuestionResponse> wäre eine Seite mit Fragen
 */