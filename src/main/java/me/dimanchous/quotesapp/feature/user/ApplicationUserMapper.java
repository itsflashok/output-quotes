package me.dimanchous.quotesapp.feature.user;

import me.dimanchous.quotesapp.feature.authentication.dto.ApplicationUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ApplicationUserMapper {
    ApplicationUserDTO toDTO(ApplicationUser user);
}
