package me.dimanchous.quotesapp.feature.submission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    Page<Submission> findAll(Pageable pageable);

    @Query(
            "SELECT s FROM Submission s WHERE s NOT IN (SELECT p.submission FROM Publication p)"
    )
    Page<Submission> findAllUnapproved(Pageable pageable);
}
