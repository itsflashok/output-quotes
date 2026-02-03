package me.dimanchous.quotesapp.feature.administration.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAuthorRequest(

        @NotNull
        String firstName,

        @NotNull
        String lastName
) {
}
