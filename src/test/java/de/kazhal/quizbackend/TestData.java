package de.kazhal.quizbackend;

import org.springframework.test.util.ReflectionTestUtils;

import de.kazhal.quizbackend.common.QuizProperties;
import de.kazhal.quizbackend.question.AnswerOption;
import de.kazhal.quizbackend.question.Difficulty;
import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.QuestionType;

import java.time.Duration;
public final class TestData {
    private TestData() {
    }
    public static QuizProperties quizProperties(QuizProperties.ScoringMode scoring) {
        return quizProperties(scoring, 100);
    }
    public static QuizProperties quizProperties(QuizProperties.ScoringMode scoring, int maxPoints) {
        return new QuizProperties(Duration.ofSeconds(30), maxPoints, new
                QuizProperties.MultipleChoice(scoring));
    }
    /** Single Choice, 10 Punkte. Optionen: 10 final (richtig), 11 static, 12 private */
    public static Question singleChoice() {
        Question q = new Question("Welches Schlüsselwort verhindert, dass eine Klasse erweitert wird?",
                QuestionType.SINGLE_CHOICE, Difficulty.EASY, 10, 30,
                "Eine als final deklarierte Klasse kann nicht abgeleitet werden.");
        q.addOption("final", true);
        q.addOption("static", false);
        q.addOption("private", false);
        return withIds(q, 1L, 10L);
    }
    /** Multiple Choice, 15 Punkte. Optionen: 20 int (richtig), 21 String, 22 boolean (richtig), 23
     Integer */
    public static Question multipleChoice() {
        Question q = new Question("Welche dieser Typen sind primitive Datentypen?",
                QuestionType.MULTIPLE_CHOICE, Difficulty.EASY, 15, 45,
                "int und boolean sind primitiv, String und Integer sind Klassen.");
        q.addOption("int", true);
        q.addOption("String", false);
        q.addOption("boolean", true);
        q.addOption("Integer", false);
        return withIds(q, 2L, 20L);
    }
    /** Wahr/Falsch, 5 Punkte. Optionen: 30 Wahr (richtig), 31 Falsch */
    public static Question trueFalse() {
        Question q = new Question("Ein Interface kann seit Java 8 default-Methoden enthalten.",
                QuestionType.TRUE_FALSE, Difficulty.EASY, 5, 20,
                "Seit Java 8 sind default- und static-Methoden in Interfaces erlaubt.");
        q.addOption("Wahr", true);
        q.addOption("Falsch", false);
        return withIds(q, 3L, 30L);
    }
    /** Freitext, 10 Punkte, akzeptiert "SELECT" ohne Beachtung der Groß- und Kleinschreibung */
    public static Question freeTextIgnoreCase() {
        Question q = new Question("Mit welcher SQL-Anweisung liest man Daten aus einer Tabelle?",
                QuestionType.FREE_TEXT, Difficulty.EASY, 10, null,
                "SELECT liest Daten.");
        q.addAcceptedAnswer("SELECT", false);
        return withIds(q, 4L, 0L);
    }
    /** Freitext, 10 Punkte, akzeptiert nur "extends" in genau dieser Schreibweise */
    public static Question freeTextCaseSensitive() {
        Question q = new Question("Mit welchem Schlüsselwort erbt eine Klasse von einer anderen?",
                QuestionType.FREE_TEXT, Difficulty.MEDIUM, 10, null,
                "Mit extends erbt eine Klasse.");
        q.addAcceptedAnswer("extends", true);
        return withIds(q, 5L, 0L);
    }
    private static Question withIds(Question q, long questionId, long firstOptionId) {
        ReflectionTestUtils.setField(q, "id", questionId);
        long optionId = firstOptionId;
        for (AnswerOption option : q.getOptions()) {
            ReflectionTestUtils.setField(option, "id", optionId++);
        }
        return q;
    }
}