package me.dimanchous.quotesapp.feature.authentication.dto;

import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(

        @NotNull
        String username,

        @NotNull
        String password
) {
}
