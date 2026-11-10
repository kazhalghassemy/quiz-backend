package de.kazhal.quizbackend.question;



import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
@ValidQuestion
public record QuestionRequest(
        @NotBlank(message = "Fragetext darf nicht leer sein")
        @Size(max = 1000, message = "Fragetext darf höchstens 1000 Zeichen haben")
        String text,
        @NotNull(message = "Fragetyp fehlt")
        QuestionType type,
        @NotNull(message = "Schwierigkeitsgrad fehlt")
        Difficulty difficulty,
        @Positive(message = "Punkte müssen größer als 0 sein")
        int points,
        @Positive(message = "Zeitlimit muss größer als 0 sein")
        Integer timeLimitSeconds,
        @Size(max = 2000, message = "Erklärung darf höchstens 2000 Zeichen haben")
        String explanation,
        @Size(max = 10, message = "Höchstens 10 Antwortoptionen erlaubt")
        List<@Valid AnswerOptionRequest> options,
        @Size(max = 10, message = "Höchstens 10 akzeptierte Antworten erlaubt")
        List<@Valid AcceptedAnswerRequest> acceptedAnswers) {
    public QuestionRequest {
        options = options == null ? List.of() : options;
        acceptedAnswers = acceptedAnswers == null ? List.of() : acceptedAnswers;
    }
}