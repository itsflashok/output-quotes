package me.dimanchous.quotesapp.feature.publication.dto;

import me.dimanchous.quotesapp.feature.authentication.dto.ApplicationUserDTO;
import me.dimanchous.quotesapp.feature.author.dto.AuthorDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record PublicationDTO (
        UUID id,
        String content,
        AuthorDTO saidBy,
        LocalDateTime saidAt,
        ApplicationUserDTO submittedBy,
        LocalDateTime submittedAt,
        Integer likeCount,
        Integer dislikeCount
) {
}
