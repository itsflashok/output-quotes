package me.dimanchous.quotesapp.feature.reaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReactionRepository extends JpaRepository<Reaction, ReactionId> {
    Integer countByReactionTypeAndId_PublicationId(Reaction.ReactionType reactionType, UUID publicationId);
    Optional<Reaction> findById_PublicationIdAndId_UserId(UUID publicationId, UUID userId);
    Boolean existsById_UserIdAndId_PublicationId(UUID userId, UUID publicationId);
}
