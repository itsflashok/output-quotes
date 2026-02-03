package me.dimanchous.quotesapp.feature.submission.dto;

import me.dimanchous.quotesapp.feature.authentication.dto.ApplicationUserDTO;
import me.dimanchous.quotesapp.feature.author.dto.AuthorDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubmissionDTO (
        UUID id,
        String content,
        AuthorDTO saidBy,
        LocalDateTime saidAt,
        ApplicationUserDTO submittedBy,
        LocalDateTime submittedAt,
        Integer voteCount,
        Integer voteGoal
) {
    public SubmissionDTO withAnonymousAuthor() {
        return new SubmissionDTO(
                this.id,
                this.content,
                null,
                null,
                this.submittedBy,
                this.submittedAt,
                this.voteCount,
                this.voteGoal
        );
    }

    public SubmissionDTO withAnonymousSubmitter() {
        return new SubmissionDTO(
                this.id,
                this.content,
                this.saidBy,
                this.saidAt,
                null,
                null,
                this.voteCount,
                this.voteGoal
        );
    }
}
