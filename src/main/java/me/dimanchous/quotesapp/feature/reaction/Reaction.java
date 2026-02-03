package me.dimanchous.quotesapp.feature.reaction;

import jakarta.persistence.*;
import me.dimanchous.quotesapp.feature.user.ApplicationUser;
import me.dimanchous.quotesapp.feature.publication.Publication;

@Entity
@Table(name = "reactions")
public class Reaction {

    public Reaction() {}

    public Reaction(
            Publication publication,
            ApplicationUser user,
            ReactionType reactionType
    ) {
        this.publication = publication;
        this.user = user;
        this.reactionType = reactionType;
        this.id = new ReactionId(publication.getId(), user.getId());
    }
    @EmbeddedId
    private ReactionId id;

    @ManyToOne
    @MapsId("publicationId")
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;


    @Column(name = "reaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReactionType reactionType;

    public ReactionType getReactionType() {
        return reactionType;
    }

    public Publication getPublication() {
        return publication;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }

    public enum ReactionType {
        LIKE,
        DISLIKE
    }
}
