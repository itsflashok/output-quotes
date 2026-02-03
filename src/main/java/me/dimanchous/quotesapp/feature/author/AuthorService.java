package me.dimanchous.quotesapp.feature.author;

import me.dimanchous.quotesapp.feature.administration.dto.CreateAuthorRequest;
import me.dimanchous.quotesapp.feature.author.dto.AuthorDTO;
import me.dimanchous.quotesapp.feature.author.dto.AuthorResponse;
import me.dimanchous.quotesapp.feature.publication.PublicationRepository;
import me.dimanchous.quotesapp.feature.quote.QuoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorService {


    private final AuthorMapper authorMapper;
    private final AuthorRepository authorRepository;
    private final QuoteRepository quoteRepository;
    private final PublicationRepository publicationRepository;

    public AuthorService(AuthorMapper authorMapper, AuthorRepository authorRepository, QuoteRepository quoteRepository, PublicationRepository publicationRepository) {
        this.authorMapper = authorMapper;
        this.authorRepository = authorRepository;
        this.quoteRepository = quoteRepository;
        this.publicationRepository = publicationRepository;
    }

    public Author createAuthor(CreateAuthorRequest createAuthorRequest) {
        Author newAuthor = new Author(
                createAuthorRequest.firstName(),
                createAuthorRequest.lastName()
        );

        return authorRepository.save(newAuthor);
    }

    public Page<AuthorResponse> findAll(Pageable pageable) {
        Page<Author> authors = authorRepository.findAll(pageable);
        return authors.map(author -> {
            AuthorDTO dto = authorMapper.toDTO(author);
            // TODO HERE
            int likes = 0;
            int dislikes = 0;
            return authorMapper.toResponse(dto, likes, dislikes);
        });
    }
}
