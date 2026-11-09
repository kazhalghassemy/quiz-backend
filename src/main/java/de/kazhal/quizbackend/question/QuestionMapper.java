package de.kazhal.quizbackend.question;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface QuestionMapper {
    @Mapping(target = "topicId", source = "topic.id")
    QuestionAdminResponse toAdminResponse(Question question);
    AnswerOptionResponse toResponse(AnswerOption option);
    AcceptedAnswerResponse toResponse(AcceptedAnswer answer);
}