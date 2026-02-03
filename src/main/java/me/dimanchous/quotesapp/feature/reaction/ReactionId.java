package me.dimanchous.quotesapp.feature.reaction;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;
import java.io.Serializable;

@Embeddable
public class ReactionId implements Serializable {

    public ReactionId() {}

    public ReactionId(
            UUID publicationId,
            UUID userId
    ) {
        this.publicationId = publicationId;
        this.userId = userId;
    }

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "publication_id")
    private UUID publicationId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReactionId that = (ReactionId) o;

        if (!userId.equals(that.userId)) return false;
        return publicationId.equals(that.publicationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, publicationId);
    }

    public UUID getPublicationId() {
        return publicationId;
    }

    public UUID getUserId() {
        return userId;
    }
}
