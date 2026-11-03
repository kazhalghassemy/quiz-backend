package de.kazhal.quizbackend.topic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}

/**
 * Kurz gesagt
 *
 * Das TopicRepository ist die einzige Stelle, die mit der Tabelle topic in der Datenbank spricht.
 * Wer ein Thema lesen, speichern oder löschen will, geht über das Repository.
 * Das Besondere:
 * Du schreibst nur ein Interface ohne Code, und Spring baut die Implementierung samt SQL beim Start selbst.
 *
 * HTTP-Anfrage
 *    ↓
 * TopicController   → nimmt HTTP entgegen, baut die Antwort
 *    ↓
 * TopicService      → Regeln ("Name schon vergeben?", "Thema hat Versuche?")
 *    ↓
 * TopicRepository   → Datenbankzugriff (SQL)
 *    ↓
 * PostgreSQL, Tabelle topic
 *
 * Jede Schicht hat eine Aufgabe. Der Controller kennt kein SQL, das Repository kennt kein HTTP.
 *
 * extends JpaRepository<Topic, Long>
 *
 * Die zwei Typen in den spitzen Klammern bedeuten:
 *
 * Topic: Welche Entity verwaltet dieses Repository?
 * Long: Welchen Typ hat ihr @Id-Feld? (private Long id;)
 *
 * Allein durch dieses extends erbst du fertige Methoden, ohne eine Zeile zu schreiben:
 *
 * Methode	            SQL dahinter	                        Benutzt bei dir
 * findAll()	        select * from topic	                    TopicService.getAllTopics()
 * findById(id)	        select … where id = ?	                TopicService.getTopic()
 * existsById(id)	    select count(*) … where id = ?	        TopicService.getQuizQuestions()
 * save(topic)	        insert oder update	                    ab heute in createTopic()
 * delete(topic)	    delete from topic where id = ?	        ab heute in deleteTopic()
 * findAll(pageable)	select … order by … limit … offset …	ab heute für die Seitenansicht
 *
 *
 * findByNameIgnoreCase(String name): eine abgeleitete Abfrage
 *
 * Diese Methode steht nicht in JpaRepository, du hast sie selbst hinzugefügt.
 * Spring liest den Methodennamen und baut daraus das SQL:
 *
 * find   By   Name   IgnoreCase
 *  │      │     │        │
 *  │      │     │        └─ Groß-/Kleinschreibung egal
 *  │      │     └─ Feld "name" der Entity Topic
 *  │      └─ jetzt kommt die Bedingung (WHERE)
 *  └─ suchen/lesen
 *
 * → ungefähr select * from topic where upper(name) = upper(?)
 */