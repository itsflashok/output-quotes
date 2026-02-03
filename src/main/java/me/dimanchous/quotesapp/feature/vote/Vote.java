package me.dimanchous.quotesapp.feature.vote;

import jakarta.persistence.*;
import me.dimanchous.quotesapp.feature.user.ApplicationUser;
import me.dimanchous.quotesapp.feature.submission.Submission;

@Entity
@Table(name = "votes")
public class Vote {

    public Vote() {}

    public Vote(Submission submission, ApplicationUser user) {
        this.submission = submission;
        this.user = user;
        this.id = new VoteId(submission.getId(), user.getId());
    }

    @EmbeddedId
    private VoteId id;

    @ManyToOne
    @MapsId("submissionId")
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser user;

    public Submission getSubmission() {
        return submission;
    }

    public ApplicationUser getUser() {
        return user;
    }
}
