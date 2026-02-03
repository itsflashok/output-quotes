package me.dimanchous.quotesapp.feature.submission.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubmissionRequest(

        @NotNull
        String content,

        @NotNull
        UUID saidBy,

        @NotNull
        LocalDateTime saidAt,

        @NotNull
        Boolean authorHidden,

        @NotNull
        Boolean submitterHidden
) {
}
