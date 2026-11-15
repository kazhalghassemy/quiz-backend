package de.kazhal.quizbackend.question;


import de.kazhal.quizbackend.topic.Topic;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(nullable = false, length = 1000)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionType type;
    /***
     * Das Grundproblem
     *
     * Eine Datenbank kennt keine Java-Enums. Sie kennt nur Zahlen, Texte usw. JPA muss Difficulty.MEDIUM also irgendwie umwandeln, und dafür gibt es zwei Möglichkeiten:
     *
     * Variante	               Was steht in der DB?	                    Spaltentyp
     * EnumType.ORDINAL        (Standard!)	die Position: 0, 1, 2	    Zahl (INTEGER)
     * EnumType.STRING	        der Name: "EASY", "MEDIUM", "HARD"	    Text (VARCHAR)
     *
     * lässt du die Annotation weg, speichert JPA die Position in der Enum-Liste:
     *
     * java
     * enum Difficulty { EASY, MEDIUM, HARD }
     * //                 0      1      2
     * Das funktioniert, bis jemand das Enum ändert: Alle alten Fragen haben plötzlich eine andere Schwierigkeit, ohne Fehlermeldung.
     *
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Difficulty difficulty;

    @Column(nullable = false)
    private int points;

    private Integer timeLimitSeconds;   // null = Standard aus QuizProperties

    @Column(length = 2000)
    private String explanation;

    @Column(nullable = false)
    private boolean active = true;      // Spalte aus V3__question_active.sql

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<AnswerOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)// orphanRemoval = true	aus der Liste entfernt = aus der DB gelöscht
    @OrderBy("id")
    private List<AcceptedAnswer> acceptedAnswers = new ArrayList<>();

    protected Question() {
        // für JPA
    }

    public Question(String text, QuestionType type, Difficulty difficulty, int points,
                    Integer timeLimitSeconds, String explanation) {
        this.text = text;
        this.type = type;
        this.difficulty = difficulty;
        this.points = points;
        this.timeLimitSeconds = timeLimitSeconds;
        this.explanation = explanation;
    }

    public void addOption(String text, boolean correct) {
        options.add(new AnswerOption(this, text, correct));
    }

    public void addAcceptedAnswer(String text, boolean caseSensitive) {
        acceptedAnswers.add(new AcceptedAnswer(this, text, caseSensitive));
    }

    /**
     * Die Methode beantwortet die Frage: Welche Antwortoptionen dieser Frage sind richtig?
     * Zurück kommen nicht die Optionen selbst, sondern nur ihre IDs.
     * @return
     */
    public Set<Long> correctOptionIds() {
        return options.stream()                         // 1. Fließband starten
                .filter(AnswerOption::isCorrect)        // 2. nur richtige durchlassen
                .map(AnswerOption::getId)               // 3. jede Option durch ihre ID ersetzen
                .collect(Collectors.toSet());           // 4. Ergebnis in ein Set sammeln
    }

    /** Nur von Topic.addQuestion/removeQuestion aufrufen, damit beide Seiten stimmen. */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Long getId() { return id; }
    public Topic getTopic() { return topic; }
    public String getText() { return text; }
    public QuestionType getType() { return type; }
    public Difficulty getDifficulty() { return difficulty; }
    public int getPoints() { return points; }
    public Integer getTimeLimitSeconds() { return timeLimitSeconds; }
    public String getExplanation() { return explanation; }
    public boolean isActive() { return active; }
    public List<AnswerOption> getOptions() { return Collections.unmodifiableList(options); }
    public List<AcceptedAnswer> getAcceptedAnswers() { return Collections.unmodifiableList(acceptedAnswers); }
    public void deactivate() {
        this.active = false;
    }
}