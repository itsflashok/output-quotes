package me.dimanchous.quotesapp.feature.vote;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, VoteId> {
    Integer countBySubmissionId(UUID submissionId);
}
