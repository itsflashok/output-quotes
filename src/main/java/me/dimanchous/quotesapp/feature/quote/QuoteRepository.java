package me.dimanchous.quotesapp.feature.quote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {
    Page<Quote> findAll(Pageable pageable);
    Page<Quote> findBySaidBy_Id(UUID authorId, Pageable pageable);

}
