package me.dimanchous.quotesapp.feature.publication;

import jakarta.validation.Valid;
import me.dimanchous.quotesapp.component.Utilities;
import me.dimanchous.quotesapp.feature.publication.dto.PublicationResponse;
import me.dimanchous.quotesapp.feature.publication.dto.ReactionRequest;
import me.dimanchous.quotesapp.feature.publication.exception.PublicationNotFoundException;
import me.dimanchous.quotesapp.feature.publication.exception.ReactionAlreadyExistsException;
import me.dimanchous.quotesapp.feature.publication.exception.ReactionNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/publications")
public class PublicationController {

    private final Utilities utilities;
    private final PublicationService publicationService;

    public PublicationController(Utilities utilities, PublicationService publicationService) {
        this.publicationService = publicationService;
        this.utilities = utilities;
    }

    @GetMapping
    public ResponseEntity<?> getPublications(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("score").descending());
        return utilities.buildSuccess(new PagedModel<>(publicationService.findAll(pageable, userDetails)));
    }

    @DeleteMapping("/{id}/react")
    public ResponseEntity<?> removeReactionFromPublication(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @PathVariable("id") UUID id
    ) {
        PublicationResponse response;
        try {
            response = publicationService.removeReactionFromPublication(userDetails, id);
        } catch (PublicationNotFoundException e) {
            return utilities.buildFailure(
                    HttpStatus.NOT_FOUND,
                    List.of(new Utilities.Error(
                            "PUBLICATION_NOT_FOUND",
                            "The specified publication was not found",
                            "No publication with ID %s exists".formatted(id)
                    ))
            );
        } catch (ReactionNotFoundException e) {
            return utilities.buildFailure(
                    HttpStatus.BAD_REQUEST,
                    List.of(new Utilities.Error(
                            "REACTION_NOT_FOUND",
                            "You have not reacted to this publication",
                            "User %s has not reacted to publication %s".formatted(userDetails.getUsername(), id)
                    ))
            );
        }
        return utilities.buildSuccess(response);
    }

    @PostMapping("/{id}/react")
    public ResponseEntity<?> reactToPublication(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @PathVariable("id") UUID id,
            @Valid @RequestBody ReactionRequest request
        ) {

        PublicationResponse response;
        try {
            response = publicationService.reactToPublication(userDetails, id, request.reactionType());
        } catch (PublicationNotFoundException e) {
            return utilities.buildFailure(
                    HttpStatus.NOT_FOUND,
                    List.of(new Utilities.Error(
                            "PUBLICATION_NOT_FOUND",
                            "The specified publication was not found",
                            "No publication with ID %s exists".formatted(id)
                    ))
            );
        } catch (ReactionAlreadyExistsException e) {
            return utilities.buildFailure(
                    HttpStatus.BAD_REQUEST,
                    List.of(new Utilities.Error(
                            "REACTION_ALREADY_EXISTS",
                            "You have already reacted to this publication",
                            "User %s has already reacted to publication %s with reaction type %s".formatted(userDetails.getUsername(), id, request.reactionType())
                    ))
            );
        }
        return utilities.buildSuccess(response);
    }
}

