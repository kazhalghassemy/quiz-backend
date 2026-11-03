package de.kazhal.quizbackend.attempt;

import de.kazhal.quizbackend.question.AnswerOption;
import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.evaluation.EvaluationResult;
import de.kazhal.quizbackend.question.evaluation.SubmittedAnswer;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class AttemptAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id")
    private Attempt attempt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private Question question;
    @Column(nullable = false, length = 1000)
    private String questionTextSnapshot;
    @Column(length = 500)
    private String freeTextAnswer;
    @Column(nullable = false)
    private boolean correct;
    @Column(nullable = false)
    private int pointsAwarded;
    @Column(nullable = false)
    private int maxPoints;
    @Column(nullable = false)
    private Instant answeredAt;
    @ManyToMany
    @JoinTable(name = "attempt_answer_selected_option",
            joinColumns = @JoinColumn(name = "attempt_answer_id"),
            inverseJoinColumns = @JoinColumn(name = "answer_option_id"))
    private Set<AnswerOption> selectedOptions = new HashSet<>();
    protected AttemptAnswer() {
        // für JPA
    }
    AttemptAnswer(Attempt attempt, Question question, SubmittedAnswer submitted,
                  EvaluationResult result, Instant answeredAt) {
        this.attempt = attempt;
        this.question = question;
        this.questionTextSnapshot = question.getText();   // Snapshot!
        this.freeTextAnswer = submitted.freeText();
        this.correct = result.correct();
        this.pointsAwarded = result.pointsAwarded();
        this.maxPoints = result.maxPoints();
        this.answeredAt = answeredAt;
        question.getOptions().stream()
                .filter(option -> submitted.selectedOptionIds().contains(option.getId()))
                .forEach(selectedOptions::add);
    }
    public Long getId() { return id; }
    public Attempt getAttempt() { return attempt; }
    public Question getQuestion() { return question; }
    public String getQuestionTextSnapshot() { return questionTextSnapshot; }
    public String getFreeTextAnswer() { return freeTextAnswer; }
    public boolean isCorrect() { return correct; }
    public int getPointsAwarded() { return pointsAwarded; }
    public int getMaxPoints() { return maxPoints; }
    public Instant getAnsweredAt() { return answeredAt; }
    public Set<AnswerOption> getSelectedOptions() { return
            Collections.unmodifiableSet(selectedOptions); }
}