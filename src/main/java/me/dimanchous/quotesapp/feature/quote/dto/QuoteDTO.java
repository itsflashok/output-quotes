package me.dimanchous.quotesapp.feature.quote.dto;

import me.dimanchous.quotesapp.feature.author.dto.AuthorDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuoteDTO(
            UUID id,
            String content,
            AuthorDTO saidBy,
            LocalDateTime saidAt
) {
}
