package de.kazhal.quizbackend;


import de.kazhal.quizbackend.question.Difficulty;
import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.QuestionRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
// @DataJpaTest und @AutoConfigureTestDatabase: Import mit Alt+Enter, das Package hängt von der Boot-Version ab
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class QuestionRepositoryTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private EntityManager entityManager;

    private Statistics statistics() {
        return entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
    }

    @Test
    @DisplayName("Die Startdaten aus Flyway sind vorhanden")
    void seedData() {
        assertThat(questionRepository.countByTopicId(1L)).isEqualTo(3);
        assertThat(questionRepository.countByTopicId(2L)).isEqualTo(3);
    }

    @Test
    @DisplayName("findForQuiz lädt Fragen samt Optionen mit genau einer SQL-Abfrage")
    void findForQuizWithoutNPlusOne() {
        entityManager.clear();
        Statistics stats = statistics();
        stats.clear();
        List<Question> questions = questionRepository.findForQuiz(1L);
        int optionCount = questions.stream().mapToInt(q -> q.getOptions().size()).sum();
        assertThat(questions).extracting(Question::getId).containsExactly(1L, 2L, 3L);
        assertThat(optionCount).isEqualTo(7);   // 3 + 4 + 0 (Frage 3 ist Freitext)
        assertThat(stats.getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deaktivierte Fragen erscheinen nicht im Quiz")
    void inactiveQuestionsAreSkipped() {
        Question question = questionRepository.findById(2L).orElseThrow();
        question.deactivate();
        entityManager.flush();
        entityManager.clear();
        assertThat(questionRepository.findForQuiz(1L))
                .extracting(Question::getId)
                .containsExactly(1L, 3L);
    }

    @Test
    @DisplayName("Abgeleitete Abfrage nach Thema und Schwierigkeit")
    void findByDifficulty() {
        assertThat(questionRepository.findByTopicIdAndDifficulty(1L, Difficulty.EASY))
                .extracting(Question::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }
}