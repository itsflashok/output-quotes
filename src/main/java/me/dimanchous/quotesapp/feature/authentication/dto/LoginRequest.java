package me.dimanchous.quotesapp.feature.authentication.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(

        @NotNull
        String username,

        @NotNull
        String password
) {
}
