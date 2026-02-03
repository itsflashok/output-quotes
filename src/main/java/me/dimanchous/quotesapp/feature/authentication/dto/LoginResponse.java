package me.dimanchous.quotesapp.feature.authentication.dto;

import java.util.List;

public record LoginResponse(
        List<String> roles
) {
}
