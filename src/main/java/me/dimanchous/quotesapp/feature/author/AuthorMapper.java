package me.dimanchous.quotesapp.feature.author;


import me.dimanchous.quotesapp.feature.author.dto.AuthorDTO;
import me.dimanchous.quotesapp.feature.author.dto.AuthorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuthorMapper {
    AuthorDTO toDTO(Author author);
    AuthorResponse toResponse(AuthorDTO dto, Integer likes, Integer dislikes);
}
