package me.dimanchous.quotesapp.feature.author.dto;

import java.util.UUID;

public record AuthorDTO(
        UUID id,
        String firstName,
        String lastName
) {
}
