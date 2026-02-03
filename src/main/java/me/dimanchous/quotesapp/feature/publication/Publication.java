package me.dimanchous.quotesapp.feature.publication;

import jakarta.persistence.*;
import me.dimanchous.quotesapp.feature.quote.Quote;
import me.dimanchous.quotesapp.feature.submission.Submission;
import org.hibernate.annotations.Formula;

import java.util.UUID;

@Entity
@Table(name = "publications")
public class Publication {

    public Publication() {}

    public Publication(Submission submission) {
        this.submission = submission;
    }

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @Formula("(SELECT COUNT(*) FROM reactions r WHERE r.publication_id = id AND r.reaction_type = 'LIKE')")
    private Integer likeCount;

    @Formula("(SELECT COUNT(*) FROM reactions r WHERE r.publication_id = id AND r.reaction_type = 'DISLIKE')")
    private Integer dislikeCount;

    @Formula("(SELECT COALESCE(SUM(CASE WHEN r.reaction_type = 'LIKE' THEN 1 WHEN r.reaction_type = 'DISLIKE' THEN -1 ELSE 0 END), 0) FROM reactions r WHERE r.publication_id = id)")
    private Integer score;


    public Integer getScore() {
        return score;
    }

    public UUID getId() {
        return id;
    }

    public Submission getSubmission() {
        return submission;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public Integer getDislikeCount() {
        return dislikeCount;
    }

}
