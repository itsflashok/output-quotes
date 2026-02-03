package me.dimanchous.quotesapp.feature.authentication.dto;

import java.util.UUID;

public record ApplicationUserDTO(
        UUID id,
        String username
) {
}
