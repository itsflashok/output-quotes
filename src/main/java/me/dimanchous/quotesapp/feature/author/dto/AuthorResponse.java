package me.dimanchous.quotesapp.feature.author.dto;

import java.util.UUID;

public record AuthorResponse(
        UUID id,
        String firstName,
        String lastName,
        Integer likes,
        Integer dislikes
) {
}
