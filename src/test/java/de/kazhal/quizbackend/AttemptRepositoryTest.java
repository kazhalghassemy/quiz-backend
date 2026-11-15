package de.kazhal.quizbackend;


import de.kazhal.quizbackend.attempt.Attempt;
import de.kazhal.quizbackend.attempt.AttemptAnswer;
import de.kazhal.quizbackend.attempt.AttemptRepository;
import de.kazhal.quizbackend.attempt.AttemptSummaryResponse;
import de.kazhal.quizbackend.question.AnswerOption;
import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.QuestionRepository;
import de.kazhal.quizbackend.question.evaluation.EvaluationResult;
import de.kazhal.quizbackend.question.evaluation.SubmittedAnswer;
import de.kazhal.quizbackend.topic.Topic;
import de.kazhal.quizbackend.topic.TopicRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
// @DataJpaTest und @AutoConfigureTestDatabase: Import mit Alt+Enter
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class AttemptRepositoryTest {
    @Autowired
    private AttemptRepository attemptRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private EntityManager entityManager;
    @Test
    @DisplayName("Versuch mit Antworten und Mehrfachauswahl wird vollständig gespeichert und geladen")
    void saveAndLoadAttempt() {
        Topic topic = topicRepository.findById(2L).orElseThrow();
        Question freeText = questionRepository.findById(4L).orElseThrow();
        Question multi = questionRepository.findById(5L).orElseThrow();
        Attempt attempt = new Attempt(topic, 30, Instant.parse("2026-11-06T10:00:00Z"));
        attempt.recordAnswer(freeText, SubmittedAnswer.ofText("select"),
                new EvaluationResult(true, 10, 10, "Erklärung"), Instant.parse("2026-11-06T10:00:10Z"));
        attempt.recordAnswer(multi, SubmittedAnswer.ofOptions(30L, 31L),
                new EvaluationResult(false, 10, 15, "Erklärung"), Instant.parse("2026-11-06T10:00:20Z"));
        Long id = attemptRepository.save(attempt).getId();
        entityManager.flush();   // SQL wirklich ausführen
        entityManager.clear();   // Persistenzkontext leeren: gleich wird neu aus PostgreSQL gelesen
        Attempt loaded = attemptRepository.findWithAnswersById(id).orElseThrow();
        assertThat(loaded.getTotalScore()).isEqualTo(20);
        assertThat(loaded.getTopic().getName()).isEqualTo("Datenbanken");
        assertThat(loaded.getAnswers()).hasSize(2);
        AttemptAnswer multiAnswer = loaded.getAnswers().stream()
                .filter(a -> a.getQuestion().getId().equals(5L))
                .findFirst().orElseThrow();
        assertThat(multiAnswer.getSelectedOptions())
                .extracting(AnswerOption::getText)
                .containsExactlyInAnyOrder("INSERT", "UPDATE");
        assertThat(multiAnswer.getQuestionTextSnapshot()).startsWith("Welche Befehle");
    }
    @Test
    @DisplayName("Projektion liefert Status und Prozent, neueste zuerst")
    void summaries() {
        Topic topic = topicRepository.findById(1L).orElseThrow();
        Attempt older = new Attempt(topic, 35, Instant.parse("2026-11-01T10:00:00Z"));
        Attempt newer = new Attempt(topic, 35, Instant.parse("2026-11-02T10:00:00Z"));
        newer.finish(Instant.parse("2026-11-02T10:05:00Z"));
        attemptRepository.save(older);
        attemptRepository.save(newer);
        entityManager.flush();
        List<AttemptSummaryResponse> summaries = attemptRepository.findSummariesByTopicId(1L);
        assertThat(summaries).hasSize(2);
        assertThat(summaries.get(0).finished()).isTrue();
// newer
        assertThat(summaries.get(1).finished()).isFalse();   // older
        assertThat(summaries.get(0).percent()).isZero();
    }
}