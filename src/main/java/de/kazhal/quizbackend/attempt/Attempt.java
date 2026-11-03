package de.kazhal.quizbackend.attempt;


import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.evaluation.EvaluationResult;
import de.kazhal.quizbackend.question.evaluation.SubmittedAnswer;
import de.kazhal.quizbackend.topic.Topic;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id")
    private Topic topic;
    @Column(nullable = false)
    private Instant startedAt;
    private Instant finishedAt;
    @Column(nullable = false)
    private int totalScore;
    @Column(nullable = false)
    private int maxScore;
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private Set<AttemptAnswer> answers = new LinkedHashSet<>();
    protected Attempt() {
        // für JPA
    }
    public Attempt(Topic topic, int maxScore, Instant startedAt) {
        this.topic = topic;
        this.maxScore = maxScore;
        this.startedAt = startedAt;
    }
    public AttemptAnswer recordAnswer(Question question, SubmittedAnswer submitted,
                                      EvaluationResult result, Instant answeredAt) {
        if (isFinished()) {
            throw new IllegalStateException("Versuch " + id + " ist bereits abgeschlossen");
        }
        if (hasAnswered(question.getId())) {
            throw new IllegalStateException("Frage " + question.getId() + " wurde in diesem Versuch schon beantwortet");
        }
        AttemptAnswer answer = new AttemptAnswer(this, question, submitted, result, answeredAt);
        answers.add(answer);
        totalScore += result.pointsAwarded();
        return answer;
    }
    public void finish(Instant now) {
        if (isFinished()) {
            throw new IllegalStateException("Versuch " + id + " ist bereits abgeschlossen");
        }
        this.finishedAt = now;
    }
    public boolean isFinished() {
        return finishedAt != null;
    }
    public boolean hasAnswered(Long questionId) {
        return answers.stream().anyMatch(a -> a.getQuestion().getId().equals(questionId));
    }
    public int percent() {
        return maxScore == 0 ? 0 : totalScore * 100 / maxScore;
    }
    public Long getId() { return id; }
    public Topic getTopic() { return topic; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getFinishedAt() { return finishedAt; }
    public int getTotalScore() { return totalScore; }
    public int getMaxScore() { return maxScore; }
    public Set<AttemptAnswer> getAnswers() { return Collections.unmodifiableSet(answers); }
}