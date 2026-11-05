package de.kazhal.quizbackend.question;

public record OptionPlayResponse(Long id, String text) {
    public static OptionPlayResponse from(AnswerOption option) {
        return new OptionPlayResponse(option.getId(), option.getText());
    }
}
