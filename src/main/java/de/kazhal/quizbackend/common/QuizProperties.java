package de.kazhal.quizbackend.common;


import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "quiz")
public record QuizProperties(
        @DefaultValue("30s") Duration defaultTimeLimit,
        @DefaultValue("100") @Positive int maxPointsPerQuestion,
        @DefaultValue MultipleChoice multipleChoice) {

    public record MultipleChoice(@DefaultValue("ALL_OR_NOTHING") ScoringMode scoring) {}

    public enum ScoringMode { ALL_OR_NOTHING, PARTIAL }

}

/**
 * Sie ist die Brücke zwischen deiner YAML-Datei und deinem Java-Code. Spring liest beim Start alles unter quiz:
 * und füllt damit diesen Record. Danach kann jede Klasse die Werte typsicher benutzen, statt Texte aus der YAML zu lesen.
 *
 * Zuordnung YAML → Java
 *
 * Deine application.yml:
 *
 * yaml
 * quiz:
 *   default-time-limit: 30s
 *   max-points-per-question: 100
 *   multiple-choice:
 *     scoring: ALL_OR_NOTHING
 *
 * So landet jeder Wert im Record:
 *
 * YAML	                                Java-Feld	                Typ
 * quiz	                                → prefix = "quiz"	        (Startpunkt)
 * default-time-limit: 30s	            defaultTimeLimit	        Duration
 * max-points-per-question: 100	        maxPointsPerQuestion	    int
 * multiple-choice:	                    multipleChoice	            Record MultipleChoice
 * scoring: ALL_OR_NOTHING	            multipleChoice.scoring	    Enum ScoringMode
 *
 * Zwei Dinge erledigt Spring dabei automatisch:
 *
 * Namen umwandeln: In der YAML schreibst du mit Bindestrichen (max-points-per-question, sogenanntes kebab-case),
 * in Java in camelCase (maxPointsPerQuestion). Spring erkennt, dass beides dasselbe meint.
 * Typen umwandeln: Aus dem Text 30s wird eine Duration von 30 Sekunden, aus 100 ein int, aus PARTIAL der Enum-Wert
 * ScoringMode.PARTIAL.
 *
 * Zeile für Zeile
 * java
 * @ConfigurationProperties(prefix = "quiz")
 *
 * → „Fülle mich mit allem, was unter quiz: steht.“
 *
 * java
 * @Validated
 * → „Prüfe die Werte beim Start.“ Zusammen mit @Positive heißt das: Steht in der YAML max-points-per-question: -5,
 * startet die App nicht und meldet einen klaren Fehler.
 * Ein falscher Wert fällt also sofort auf und nicht erst mitten im Quiz.
 *
 * java
 * @DefaultValue("30s") Duration defaultTimeLimit,
 *
 * → Fehlt der Eintrag in der YAML, gilt 30 Sekunden. Duration versteht Angaben wie 30s, 2m oder 1h.
 *
 * java
 * @DefaultValue("100") @Positive int maxPointsPerQuestion,
 *
 * → Fehlt der Eintrag, gilt 100. Der Wert muss größer als 0 sein.
 *
 * java
 * @DefaultValue MultipleChoice multipleChoice
 *
 * → Hier steht @DefaultValue ohne Wert. Das heißt: Fehlt der ganze Block multiple-choice:,
 * erzeugt Spring trotzdem ein MultipleChoice-Objekt mit dessen eigenen Standardwerten.
 * Ohne diese Annotation wäre multipleChoice dann null,
 * und properties.multipleChoice().scoring() würde mit einer NullPointerException abstürzen.
 *
 * public record MultipleChoice(@DefaultValue("ALL_OR_NOTHING") ScoringMode scoring) {}
 *
 * → Ein verschachtelter Record für die verschachtelte YAML-Ebene. Ohne Angabe gilt ALL_OR_NOTHING.
 *
 * java
 * public enum ScoringMode { ALL_OR_NOTHING, PARTIAL }
 *
 * → Die einzigen zwei erlaubten Werte. Ein Tippfehler wie scoring: PARTAL lässt die App beim Start mit einem Fehler abbrechen.
 *
 * Woher Spring die Klasse kennt
 *
 * In deiner QuizBackendApplication steht:
 *
 * java
 * @ConfigurationPropertiesScan
 *
 * Damit sucht Spring alle Klassen mit @ConfigurationProperties und macht sie zu Beans.
 *
 * Wer sie benutzt
 *
 * Die Klasse wird per Konstruktor übergeben (Dependency Injection):
 *
 * java
 * // MultipleChoiceEvaluator
 * if (properties.multipleChoice().scoring() == QuizProperties.ScoringMode.ALL_OR_NOTHING) { ... }
 *
 * // EvaluationService
 * if (question.getPoints() > properties.maxPointsPerQuestion()) { ... }
 *
 * // InfoController
 * quizProperties.defaultTimeLimit().toSeconds()
 */