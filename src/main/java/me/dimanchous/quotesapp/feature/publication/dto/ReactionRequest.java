package me.dimanchous.quotesapp.feature.publication.dto;

import jakarta.validation.constraints.NotNull;
import me.dimanchous.quotesapp.feature.reaction.Reaction;

public record ReactionRequest(
        @NotNull
        Reaction.ReactionType reactionType
) {
}
