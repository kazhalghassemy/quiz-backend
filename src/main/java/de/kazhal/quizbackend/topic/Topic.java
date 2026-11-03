package de.kazhal.quizbackend.topic;

import de.kazhal.quizbackend.question.Question;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)//orphanRemoval = true	aus der Liste entfernt = aus der DB gelöscht
    @OrderBy("id")
    private List<Question> questions = new ArrayList<>();

    protected Topic() {
        // für JPA
    }

    public Topic(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setTopic(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setTopic(null);
    }

    public int maxScore() {
        return questions.stream().mapToInt(Question::getPoints).sum();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Question> getQuestions() { return Collections.unmodifiableList(questions); }
}