package me.dimanchous.quotesapp.feature.publication;


import me.dimanchous.quotesapp.feature.publication.dto.PublicationDTO;
import me.dimanchous.quotesapp.feature.publication.dto.PublicationResponse;
import me.dimanchous.quotesapp.feature.publication.dto.SelfReactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PublicationMapper {
    @Mapping(target = "content", source = "submission.quote.content")
    @Mapping(target = "saidBy", source = "submission.quote.saidBy")
    @Mapping(target = "saidAt", source = "submission.quote.saidAt")
    @Mapping(target = "submittedBy", source = "submission.submittedBy")
    @Mapping(target = "submittedAt", source = "submission.submittedAt")
    PublicationDTO toDto(Publication publication);

    PublicationResponse toResponse(PublicationDTO dto, SelfReactionDTO selfReactionData);
}
