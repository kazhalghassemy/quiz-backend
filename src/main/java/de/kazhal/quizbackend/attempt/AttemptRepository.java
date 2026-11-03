package de.kazhal.quizbackend.attempt;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    @EntityGraph(attributePaths = {"topic", "answers", "answers.selectedOptions"})
    Optional<Attempt> findWithAnswersById(Long id);

    List<Attempt> findByTopicIdOrderByStartedAtDesc(Long topicId);
}