package me.dimanchous.quotesapp.feature.quote;

import me.dimanchous.quotesapp.feature.quote.dto.QuoteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteMapper quoteMapper;

    public QuoteService(QuoteRepository quoteRepository, QuoteMapper quoteMapper) {
        this.quoteRepository = quoteRepository;
        this.quoteMapper = quoteMapper;
    }

    public Page<QuoteDTO> findAll(Pageable pageable) {
        return quoteRepository.findAll(pageable).map(quoteMapper::toDTO);
    }
}
