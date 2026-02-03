package me.dimanchous.quotesapp.feature.publication;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import me.dimanchous.quotesapp.feature.publication.exception.PublicationNotFoundException;
import me.dimanchous.quotesapp.feature.publication.exception.ReactionNotFoundException;
import me.dimanchous.quotesapp.feature.user.ApplicationUser;
import me.dimanchous.quotesapp.feature.user.ApplicationUserService;
import me.dimanchous.quotesapp.feature.user.UserRepository;
import me.dimanchous.quotesapp.feature.publication.dto.PublicationDTO;
import me.dimanchous.quotesapp.feature.publication.dto.PublicationResponse;
import me.dimanchous.quotesapp.feature.publication.dto.SelfReactionDTO;
import me.dimanchous.quotesapp.feature.publication.exception.ReactionAlreadyExistsException;
import me.dimanchous.quotesapp.feature.reaction.Reaction;
import me.dimanchous.quotesapp.feature.reaction.ReactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final PublicationMapper publicationMapper;
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final ApplicationUserService applicationUserService;

    @PersistenceContext
    private EntityManager entityManager;


    public PublicationService(PublicationRepository publicationRepository, PublicationMapper publicationMapper, ReactionRepository reactionRepository, UserRepository userRepository, ApplicationUserService applicationUserService) {
        this.publicationRepository = publicationRepository;
        this.publicationMapper = publicationMapper;
        this.reactionRepository = reactionRepository;
        this.userRepository = userRepository;
        this.applicationUserService = applicationUserService;
    }

    @Transactional
    public PublicationResponse removeReactionFromPublication(UserDetails userDetails, UUID publicationId) throws PublicationNotFoundException, ReactionNotFoundException {
        Optional<Publication> publicationOptional = publicationRepository.findById(publicationId);
        if (publicationOptional.isEmpty()) throw new PublicationNotFoundException();
        Publication publication = publicationOptional.get();
        ApplicationUser user = applicationUserService.getAuthenticatedUserFromDetails(userDetails);
        Optional<Reaction> existingReactionOptional = reactionRepository.findById_PublicationIdAndId_UserId(publicationId, user.getId());
        if (existingReactionOptional.isEmpty()) throw new ReactionNotFoundException();
        Reaction reaction = existingReactionOptional.get();
        reactionRepository.delete(reaction);
        entityManager.flush();
        entityManager.refresh(publication);
        return getPublicationResponse(publication, user);
    }

    @Transactional
    public PublicationResponse reactToPublication(UserDetails userDetails, UUID publicationId, Reaction.ReactionType reactionType) throws PublicationNotFoundException, ReactionAlreadyExistsException {
        Optional<Publication> publicationOptional = publicationRepository.findById(publicationId);
        if (publicationOptional.isEmpty()) throw new PublicationNotFoundException();
        Publication publication = publicationOptional.get();

        ApplicationUser user = applicationUserService.getAuthenticatedUserFromDetails(userDetails);

        Optional<Reaction> existingReactionOptional = reactionRepository.findById_PublicationIdAndId_UserId(publicationId, user.getId());
        if (existingReactionOptional.isPresent()) {
            Reaction reaction = existingReactionOptional.get();
            if (reactionType != reaction.getReactionType()) {
                reaction.setReactionType(reactionType);
                reactionRepository.save(reaction);
                entityManager.flush();
                entityManager.refresh(publication);
                return getPublicationResponse(publication, user);
            } else {
                throw new ReactionAlreadyExistsException();
            }
        };

        Reaction newReaction = new Reaction(
            publication,
            user,
            reactionType
        );

        reactionRepository.save(newReaction);
        entityManager.flush();
        entityManager.refresh(publication);
        return getPublicationResponse(publication, user);
    }

    private PublicationResponse getPublicationResponse(Publication publication) {
        return getPublicationResponse(publication, null);
    }

    private PublicationResponse getPublicationResponse(Publication publication, ApplicationUser user) {
        PublicationDTO dto = publicationMapper.toDto(publication);
        SelfReactionDTO selfReactionData;

        if (user == null) {
            return publicationMapper.toResponse(
                    dto,
                    new SelfReactionDTO(false, null)
            );
        }

        Boolean selfReaction = reactionRepository.existsById_UserIdAndId_PublicationId(user.getId(), publication.getId());
        if (selfReaction) {
            Optional<Reaction> reactionOptional = reactionRepository.findById_PublicationIdAndId_UserId(publication.getId(), user.getId());
            if (reactionOptional.isEmpty()) throw new RuntimeException("Reaction not found despite existence check");
            Reaction reaction = reactionOptional.get();
            selfReactionData = new SelfReactionDTO(
                true,
                reaction.getReactionType()
            );
        } else {
            selfReactionData = new SelfReactionDTO(
                false,
                null
            );
        }
        return publicationMapper.toResponse(dto, selfReactionData);
    }

    public Page<PublicationResponse> findAll(Pageable pageable, UserDetails userDetails) {
        return publicationRepository.findAll(pageable).map(publication -> {
            if (userDetails == null) return getPublicationResponse(publication);
            ApplicationUser user = applicationUserService.getAuthenticatedUserFromDetails(userDetails);
            return getPublicationResponse(publication, user);
        });
    }
}

