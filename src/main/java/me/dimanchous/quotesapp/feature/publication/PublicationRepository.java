package me.dimanchous.quotesapp.feature.publication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PublicationRepository extends JpaRepository<Publication, UUID> {
    Optional<Publication> findBySubmissionId(UUID submissionId);

}
