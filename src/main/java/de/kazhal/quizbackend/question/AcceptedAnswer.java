package de.kazhal.quizbackend.question;

import jakarta.persistence.*;

@Entity
public class AcceptedAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(nullable = false)
    private boolean caseSensitive;

    protected AcceptedAnswer() {
        // für JPA
    }

    AcceptedAnswer(Question question, String text, boolean caseSensitive) {
        this.question = question;
        this.text = text;
        this.caseSensitive = caseSensitive;
    }

    public Long getId() { return id; }
    public Question getQuestion() { return question; }
    public String getText() { return text; }
    public boolean isCaseSensitive() { return caseSensitive; }
}