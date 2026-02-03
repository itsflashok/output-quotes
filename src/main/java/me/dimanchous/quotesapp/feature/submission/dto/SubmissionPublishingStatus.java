package me.dimanchous.quotesapp.feature.submission.dto;

import java.util.UUID;

public record SubmissionPublishingStatus(
        Boolean published,
        UUID publicationId
) {
}
