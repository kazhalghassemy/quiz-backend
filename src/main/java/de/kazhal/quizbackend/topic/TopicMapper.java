package de.kazhal.quizbackend.topic;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TopicMapper {
    TopicResponse toResponse(Topic topic);
}
