package me.dimanchous.quotesapp.feature.author;

import jakarta.persistence.*;
import me.dimanchous.quotesapp.feature.quote.Quote;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "authors")
public class Author {

    public Author() {}

    public Author(
            String firstName,
            String lastName
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.quotes = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false, unique = true)
    private String firstName;

    @Column(name = "last_name", nullable = false, unique = true)
    private String lastName;

    public List<Quote> getQuotes() {
        return new ArrayList<>(quotes);
    }

    @OneToMany(mappedBy = "saidBy", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Quote> quotes;

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
