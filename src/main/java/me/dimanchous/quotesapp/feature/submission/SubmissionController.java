package me.dimanchous.quotesapp.feature.submission;

import jakarta.validation.Valid;
import me.dimanchous.quotesapp.component.Utilities;
import me.dimanchous.quotesapp.feature.submission.dto.SubmissionDTO;
import me.dimanchous.quotesapp.feature.submission.dto.SubmissionRequest;
import me.dimanchous.quotesapp.feature.submission.dto.SubmissionResponse;
import me.dimanchous.quotesapp.feature.submission.exception.*;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/submissions")
public class SubmissionController {


    private final Utilities utilities;
    private final SubmissionService submissionService;

    public SubmissionController(Utilities utilities, SubmissionService submissionService) {
        this.utilities = utilities;
        this.submissionService = submissionService;
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<?> voteForSubmission(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable @Valid UUID id
    ) {
        SubmissionResponse submissionResponse;
        try {
            submissionResponse = submissionService.voteForSubmission(userDetails, id);
        } catch (SubmissionNotFoundException e) {
            return utilities.buildFailure(HttpStatus.NOT_FOUND, List.of(
                    new Utilities.Error(
                            "SUBMISSION_NOT_FOUND",
                            "The specified submission was not found",
                            "No submission with ID %s exists".formatted(id)
                    )
            ));
        } catch (AlreadyVotedException e) {
            return utilities.buildFailure(HttpStatus.BAD_REQUEST, List.of(
                    new Utilities.Error(
                            "ALREADY_VOTED",
                            "You have already voted for this submission",
                            "User %s has already voted for submission %s".formatted(userDetails.getUsername(), id)
                    )
            ));
        } catch (SubmissionAlreadyPublishedException e) {
            return utilities.buildFailure(HttpStatus.BAD_REQUEST, List.of(
                    new Utilities.Error(
                            "SUBMISSION_ALREADY_PUBLISHED",
                            "This submission has already been published",
                            "Submission %s has already been published and cannot be voted on".formatted(id)
                    )
            ));
        }
        return utilities.buildSuccess(submissionResponse);
    }

    @GetMapping
    public ResponseEntity<?> getSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("voteCount").descending());
        Page<SubmissionResponse> submissions = submissionService.findAll(pageable, userDetails);
        return utilities.buildSuccess(new PagedModel<>(submissions));
    }

    @PostMapping
    public ResponseEntity<?> createSubmission(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody SubmissionRequest request) {
        SubmissionDTO submissionDTO;
        try {
            submissionDTO = submissionService.createSubmission(userDetails, request);
        } catch (AuthorNotFoundException e) {
            return utilities.buildFailure(
                    HttpStatus.NOT_FOUND,
                    List.of(new Utilities.Error(
                            "AUTHOR_NOT_FOUND",
                            "The specified author was not found",
                            "No author with ID %s exists".formatted(request.saidBy()
                    ))
            ));
        } catch (InvalidSaidAtException e) {
            return utilities.buildFailure(
                    HttpStatus.BAD_REQUEST,
                    List.of(new Utilities.Error(
                            "INVALID_SAID_AT",
                            "The saidAt date is invalid",
                            "The saidAt date must be in the past"
                    ))
            );
        }
        return utilities.buildSuccess();
    }
}
