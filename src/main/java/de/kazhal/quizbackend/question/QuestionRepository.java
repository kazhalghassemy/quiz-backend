package de.kazhal.quizbackend.question;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @EntityGraph(attributePaths = "options")
    List<Question> findByTopicIdOrderByIdAsc(Long topicId);
    @EntityGraph(attributePaths = "options")
    List<Question> findByTopicIdAndDifficulty(Long topicId, Difficulty difficulty);

    // Bedingung ohne Parameter (True), Sortierung
    List<Question> findByTopicIdAndActiveTrueOrderByIdAsc(Long topicId);

    long countByTopicId(Long topicId);

    // LIKE-Suche, unabhängig von Groß-/Kleinschreibung
    List<Question> findByTextContainingIgnoreCase(String part);

    // Fragen samt Optionen in EINER Abfrage
    @Query("""
            select distinct q from Question q
            left join fetch q.options
            where q.topic.id = :topicId and q.active = true
            order by q.id
            """)
    List<Question> findForQuiz(@Param("topicId") Long topicId);
}