package de.kazhal.quizbackend.question.evaluation;

import de.kazhal.quizbackend.question.Question;
import de.kazhal.quizbackend.question.QuestionType;

public interface AnswerEvaluator {
    QuestionType supports();
    EvaluationResult evaluate(Question question, SubmittedAnswer answer);
}
