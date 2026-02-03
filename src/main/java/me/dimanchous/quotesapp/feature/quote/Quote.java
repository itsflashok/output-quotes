package me.dimanchous.quotesapp.feature.quote;

import jakarta.persistence.*;
import me.dimanchous.quotesapp.feature.author.Author;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "quotes")
public class Quote {

    public Quote() {}

    public Quote(
            String content,
            Author saidBy,
            LocalDateTime saidAt
    ) {
        this.content = content;
        this.saidBy = saidBy;
        this.saidAt = saidAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "said_by", nullable = false)
    private Author saidBy;

    @Column(name = "said_at", nullable = false)
    private LocalDateTime saidAt;


    public Author getSaidBy() {
        return saidBy;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSaidAt() {
        return saidAt;
    }

}
