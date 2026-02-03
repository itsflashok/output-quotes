package me.dimanchous.quotesapp.feature.quote;

import me.dimanchous.quotesapp.feature.quote.dto.QuoteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface QuoteMapper {
    @Mapping(source = "saidBy", target = "saidBy")
    QuoteDTO toDTO(Quote quote);
}
