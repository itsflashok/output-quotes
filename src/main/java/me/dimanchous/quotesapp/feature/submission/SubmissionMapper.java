package me.dimanchous.quotesapp.feature.submission;


import me.dimanchous.quotesapp.feature.submission.dto.SubmissionDTO;
import me.dimanchous.quotesapp.feature.submission.dto.SubmissionPublishingStatus;
import me.dimanchous.quotesapp.feature.submission.dto.SubmissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SubmissionMapper {
    @Mapping(source = "quote.content", target = "content")
    @Mapping(source = "quote.saidBy", target = "saidBy")
    @Mapping(source = "quote.saidAt", target = "saidAt")
    SubmissionDTO toDTO(Submission submission);

    SubmissionResponse toResponse(SubmissionDTO dto, Boolean selfVoted, SubmissionPublishingStatus submissionPublishingStatus);
}
