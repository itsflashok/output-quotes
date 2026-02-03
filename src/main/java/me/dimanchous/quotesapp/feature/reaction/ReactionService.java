package me.dimanchous.quotesapp.feature.reaction;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;

    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    public Integer countReactionsByTypeAndPublicationId(Reaction.ReactionType type, UUID publicationId) {
        return reactionRepository.countByReactionTypeAndId_PublicationId(type, publicationId);
    }
}
