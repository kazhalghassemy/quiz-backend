package de.kazhal.quizbackend.question;

public record AnswerOptionResponse(Long id, String text, boolean correct) {
    public static AnswerOptionResponse from(AnswerOption option) {
        return new AnswerOptionResponse(option.getId(), option.getText(), option.isCorrect());
    }
}