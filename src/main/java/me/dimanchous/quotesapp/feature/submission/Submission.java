package me.dimanchous.quotesapp.feature.submission;

import jakarta.persistence.*;
import me.dimanchous.quotesapp.feature.user.ApplicationUser;
import me.dimanchous.quotesapp.feature.quote.Quote;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "submissions")
public class Submission {

    public Submission() {}

    public Submission(
            ApplicationUser submittedBy,
            LocalDateTime submittedAt,
            Quote quote,
            Boolean authorHidden,
            Boolean submitterHidden
    ) {
        this.submittedBy = submittedBy;
        this.submittedAt = submittedAt;
        this.quote = quote;
        this.submitterHidden = submitterHidden;
        this.authorHidden = authorHidden;
    }

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "submitted_by", nullable = false)
    private ApplicationUser submittedBy;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @OneToOne
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Formula("(SELECT COUNT(*) FROM votes v WHERE v.submission_id = id)")
    private Integer voteCount;

    @Column(name = "vote_goal")
    private Integer voteGoal;

    @Column(name = "submitter_hidden", nullable = false)
    private Boolean submitterHidden;

    @Column(name = "author_hidden", nullable = false)
    private Boolean authorHidden;

    public void setVoteGoal(int voteGoal) {
        this.voteGoal = voteGoal;
    }

    public Boolean isSubmitterHidden() {
        return submitterHidden;
    }

    public Boolean isAuthorHidden() {
        return authorHidden;
    }

    public int getVoteGoal() {
        return voteGoal;
    }

    public ApplicationUser getSubmittedBy() {
        return submittedBy;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Quote getQuote() {
        return quote;
    }

    public UUID getId() {
        return id;
    }

    public Integer getVoteCount() {
        return voteCount;
    }
}
