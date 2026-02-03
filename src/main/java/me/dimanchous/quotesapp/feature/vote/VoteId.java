package me.dimanchous.quotesapp.feature.vote;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class VoteId implements Serializable {

    public VoteId() {}

    public VoteId(UUID userId, UUID submissionId) {
        this.userId = userId;
        this.submissionId = submissionId;
    }

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "submission_id")
    private UUID submissionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteId that = (VoteId) o;

        if (!userId.equals(that.userId)) return false;
        return submissionId.equals(that.submissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, submissionId);
    }

    public UUID getSubmissionId() {
        return submissionId;
    }

    public UUID getUserId() {
        return userId;
    }
}
