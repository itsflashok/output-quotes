package me.dimanchous.quotesapp.feature.publication.dto;

import me.dimanchous.quotesapp.feature.reaction.Reaction;

public record SelfReactionDTO(
        Boolean reacted,
        Reaction.ReactionType reactionType
) {
}
