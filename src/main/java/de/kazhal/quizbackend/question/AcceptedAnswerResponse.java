package de.kazhal.quizbackend.question;

public record AcceptedAnswerResponse(Long id, String text, boolean caseSensitive) {
    public static AcceptedAnswerResponse from(AcceptedAnswer answer) {
        return new AcceptedAnswerResponse(answer.getId(), answer.getText(),
                answer.isCaseSensitive());
    }
}