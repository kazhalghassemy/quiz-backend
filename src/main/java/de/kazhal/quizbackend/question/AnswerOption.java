package de.kazhal.quizbackend.question;

import jakarta.persistence.*;

/**
 * CREATE TABLE answer_option (
 *     id           BIGINT AUTO_INCREMENT PRIMARY KEY,   -- @Id + @GeneratedValue
 *     question_id  BIGINT       NOT NULL,               -- @ManyToOne + @JoinColumn
 *     text         VARCHAR(500) NOT NULL,               -- @Column(nullable=false, length=500)
 *     correct      BOOLEAN      NOT NULL,               -- @Column(nullable=false)
 *     FOREIGN KEY (question_id) REFERENCES question(id)
 * );
 */
@Entity
public class AnswerOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //IDENTITY bedeutet konkret: Die DB-Spalte ist eine Auto-Increment-Spalte
    private Long id;
    /*
     * Standard bei @ManyToOne wäre EAGER: Jedes Mal, wenn du eine Option lädst,
     * würde Hibernate sofort auch die Frage mitladen, ob du sie brauchst oder nicht.
     *
     * Mit LAZY steht in question zunächst nur ein Proxy, eine Art Platzhalter, der nur die ID kennt.
     * Erst wenn du z. B. option.getQuestion().getText() aufrufst, wird die Frage aus der DB geholt
    */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)    //optional = false -- Heißt: Eine Option ohne Frage gibt es nicht.
                                                            // Das Feld darf nie null sein. Hibernate weiß dadurch,
                                                            // dass die Spalte NOT NULL ist, und kann bei manchen Abfragen einen effizienteren INNER JOIN statt LEFT JOIN verwenden.
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(nullable = false)
    private boolean correct;

    protected AnswerOption() {
        // für JPA
    }

    AnswerOption(Question question, String text, boolean correct) {
        this.question = question;
        this.text = text;
        this.correct = correct;
    }

    public Long getId() { return id; }
    public Question getQuestion() { return question; }
    public String getText() { return text; }
    public boolean isCorrect() { return correct; }
}