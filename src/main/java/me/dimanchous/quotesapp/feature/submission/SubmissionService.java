package me.dimanchous.quotesapp.feature.submission;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import me.dimanchous.quotesapp.feature.publication.Publication;
import me.dimanchous.quotesapp.feature.publication.PublicationRepository;
import me.dimanchous.quotesapp.feature.submission.dto.SubmissionPublishingStatus;
import me.dimanchous.quotesapp.feature.submission.dto.SubmissionRequest;
import me.dimanchous.quotesapp.feature.submission.exception.*;
import me.dimanchous.quotesapp.feature.user.ApplicationUser;
import me.dimanchous.quotesapp.feature.user.ApplicationUserMapper;
import me.dimanchous.quotesapp.feature.user.ApplicationUserService;
import me.dimanchous.quotesapp.feature.user.UserRepository;
import me.dimanchous.quotesapp.feature.author.Author;
import me.dimanchous.quotesapp.feature.author.AuthorRepository;
import me.dimanchous.quotesapp.feature.quote.Quote;
import me.dimanchous.quotesapp.feature.quote.QuoteRepository;
import me.dimanchous.quotesapp.feature.submission.dto.SubmissionDTO;
import me.dimanchous.quotesapp.feature.submission.dto.SubmissionResponse;
import me.dimanchous.quotesapp.feature.vote.Vote;
import me.dimanchous.quotesapp.feature.vote.VoteId;
import me.dimanchous.quotesapp.feature.vote.VoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class SubmissionService {

    private final AuthorRepository authorRepository;
    private final ZoneId timeZoneId;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final QuoteRepository quoteRepository;
    private final ApplicationUserMapper applicationUserMapper;
    private final SubmissionMapper submissionMapper;
    private final VoteRepository voteRepository;
    private final ApplicationUserService applicationUserService;
    private final PublicationRepository publicationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public SubmissionService(AuthorRepository authorRepository, ZoneId timeZoneId, UserRepository userRepository, SubmissionRepository submissionRepository, QuoteRepository quoteRepository, ApplicationUserMapper applicationUserMapper, SubmissionMapper submissionMapper, VoteRepository voteRepository, ApplicationUserService applicationUserService, PublicationRepository publicationRepository) {
        this.authorRepository = authorRepository;
        this.timeZoneId = timeZoneId;
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.quoteRepository = quoteRepository;
        this.applicationUserMapper = applicationUserMapper;
        this.submissionMapper = submissionMapper;
        this.voteRepository = voteRepository;
        this.applicationUserService = applicationUserService;
        this.publicationRepository = publicationRepository;
    }

    @Transactional
    public SubmissionResponse voteForSubmission(UserDetails userDetails, UUID submissionId) throws SubmissionNotFoundException, AlreadyVotedException, SubmissionAlreadyPublishedException {
        ApplicationUser user = applicationUserService.getAuthenticatedUserFromDetails(userDetails);

        Optional<Submission> submissionOptional = submissionRepository.findById(submissionId);
        if (submissionOptional.isEmpty()) throw new SubmissionNotFoundException();
        Submission submission = submissionOptional.get();

        Optional<Vote> existingVoteOptional = voteRepository.findById(new VoteId(user.getId(), submission.getId()));
        if (existingVoteOptional.isPresent()) throw new AlreadyVotedException();

        if (isSubmissionPublished(submission)) throw new SubmissionAlreadyPublishedException();
        Vote newVote = new Vote(
            submission,
            user
        );

        voteRepository.save(newVote);
        entityManager.flush();
        entityManager.refresh(submission);
        publishSubmissionIfVoteGoalReached(submission);
        entityManager.flush();
        entityManager.refresh(submission);
        return getSubmissionResponse(submission, user);
    }

    private SubmissionResponse getSubmissionResponse(Submission submission, ApplicationUser user) {
        SubmissionDTO dto = submissionMapper.toDTO(submission);
        if (submission.isAuthorHidden()) dto = dto.withAnonymousAuthor();
        if (submission.isSubmitterHidden()) dto = dto.withAnonymousSubmitter();
        Boolean hasVoted = user != null && voteRepository.existsById(new VoteId(user.getId(), submission.getId()));
        Boolean published = isSubmissionPublished(submission);

        SubmissionPublishingStatus publishingStatus;

        if (published) {
            Optional<Publication> publicationOptional = publicationRepository.findBySubmissionId(submission.getId());
            if (publicationOptional.isEmpty()) throw new RuntimeException("Publication not found for published submission");
            publishingStatus = new SubmissionPublishingStatus(
                    true,
                    publicationOptional.get().getId()
            );
        } else {
            publishingStatus = new SubmissionPublishingStatus(
                    false,
                    null
            );
        }

        return submissionMapper.toResponse(dto, hasVoted, publishingStatus);
    }

    private SubmissionResponse getSubmissionResponse(Submission submission) {
        return getSubmissionResponse(submission, null);
    }

    public Page<SubmissionResponse> findAll(Pageable pageable, UserDetails userDetails) {

        Function<Submission, SubmissionResponse> mapper =
                userDetails == null ? this::getSubmissionResponse : submission -> {
                    ApplicationUser user = applicationUserService.getAuthenticatedUserFromDetails(userDetails);
                    return getSubmissionResponse(submission, user);
                };
        return submissionRepository.findAllUnapproved(pageable).map(mapper);
    }

    public SubmissionDTO createSubmission(UserDetails userDetails, SubmissionRequest request)
            throws AuthorNotFoundException, InvalidSaidAtException {

        Optional<Author> saidByOptional = authorRepository.findById(request.saidBy());
        if (saidByOptional.isEmpty()) {
            throw new AuthorNotFoundException();
        }

        if (request.saidAt().isAfter(ZonedDateTime.now(timeZoneId).toLocalDateTime())) {
            throw new InvalidSaidAtException();
        }

        Author saidBy = saidByOptional.get();
        Quote quote = new Quote(request.content(), saidBy, request.saidAt());
        quoteRepository.save(quote);

        Optional<ApplicationUser> submitterOptional = userRepository.findByUsername(userDetails.getUsername());
        if (submitterOptional.isEmpty()) {
            throw new RuntimeException("Authenticated user not found");
        }
        ApplicationUser submitter = submitterOptional.get();

        Submission submission = new Submission(
                submitter,
                LocalDateTime.now(timeZoneId),
                quote,
                request.authorHidden(),
                request.submitterHidden()
        );
        submission.setVoteGoal(getSubmissionVoteGoal());

        submissionRepository.save(submission);

        return submissionMapper.toDTO(submission);
    }

    private void publishSubmissionIfVoteGoalReached(Submission submission) {
        if(!(submission.getVoteCount() >= submission.getVoteGoal()) || isSubmissionPublished(submission)) return;

        Publication publication = new Publication(
                submission
        );
        publicationRepository.save(publication);
    }

    private Boolean isSubmissionPublished(Submission submission) {
        Optional<Publication> publicationOptional = publicationRepository.findBySubmissionId(submission.getId());
        return publicationOptional.isPresent();
    }

    public Integer getSubmissionVoteGoal() {
        return 10;
    }
}
