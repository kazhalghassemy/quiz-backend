package de.kazhal.quizbackend;

import de.kazhal.quizbackend.attempt.AttemptRepository;
import de.kazhal.quizbackend.question.QuestionRepository;
import de.kazhal.quizbackend.topic.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AttemptRepository attemptRepository;
    private TopicService topicService;

    @BeforeEach
    void setUp() {
        topicService = new TopicService(topicRepository, questionRepository, attemptRepository, new
                TopicMapperImpl());
    }

    private static Topic topicWithId(long id, String name) {
        Topic topic = new Topic(name, "Beschreibung");
        ReflectionTestUtils.setField(topic, "id", id);
        return topic;
    }

    @Test
    @DisplayName("Legt ein neues Thema an")
    void createTopic() {
        when(topicRepository.existsByNameIgnoreCase("Spring Boot")).thenReturn(false);
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation ->
                invocation.getArgument(0));
        TopicResponse response = topicService.createTopic(new TopicRequest("Spring Boot", "Beans und JPA"));
        ArgumentCaptor<Topic> saved = ArgumentCaptor.forClass(Topic.class);
        verify(topicRepository).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo("Spring Boot");
        assertThat(saved.getValue().getDescription()).isEqualTo("Beans und JPA");
        assertThat(response.name()).isEqualTo("Spring Boot");
    }

    @Test
    @DisplayName("Lehnt einen vergebenen Namen ab und speichert nichts")
    void createTopicNameTaken() {
        when(topicRepository.existsByNameIgnoreCase("Java-Grundlagen")).thenReturn(true);
        assertThatThrownBy(() -> topicService.createTopic(new TopicRequest("Java-Grundlagen",
                null)))
                .isInstanceOf(TopicNameAlreadyExistsException.class);
        verify(topicRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ändert das Thema über Dirty Checking, ohne save()")
    void updateTopic() {
        Topic topic = topicWithId(1L, "Alt");
        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(topicRepository.existsByNameIgnoreCase("Neu")).thenReturn(false);
        TopicResponse response = topicService.updateTopic(1L, new TopicRequest("Neu", "Neue Beschreibung"));
        assertThat(topic.getName()).isEqualTo("Neu");
        assertThat(response.id()).isEqualTo(1L);
        verify(topicRepository, never()).save(any());
    }

    @Test
    @DisplayName("Thema mit Versuchen wird nicht gelöscht")
    void deleteTopicInUse() {
        when(topicRepository.findById(1L)).thenReturn(Optional.of(topicWithId(1L, "Java Grundlagen")));
        when(attemptRepository.existsByTopicId(1L)).thenReturn(true);
        assertThatThrownBy(() -> topicService.deleteTopic(1L))
                .isInstanceOf(TopicInUseException.class);
        verify(topicRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Unbekanntes Thema wirft TopicNotFoundException")
    void topicNotFound() {
        when(topicRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> topicService.findTopic(999L))
                .isInstanceOf(TopicNotFoundException.class)
                .hasMessage("Thema mit ID 999 nicht gefunden");
    }
}
