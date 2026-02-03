package me.dimanchous.quotesapp.feature.submission.dto;

import me.dimanchous.quotesapp.feature.authentication.dto.ApplicationUserDTO;
import me.dimanchous.quotesapp.feature.author.dto.AuthorDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubmissionResponse(
        UUID id,
        String content,
        AuthorDTO saidBy,
        LocalDateTime saidAt,
        ApplicationUserDTO submittedBy,
        LocalDateTime submittedAt,
        Integer voteCount,
        Integer voteGoal,
        Boolean selfVoted,
        SubmissionPublishingStatus submissionPublishingStatus
) {
}
