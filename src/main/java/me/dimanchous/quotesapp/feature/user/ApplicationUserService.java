package me.dimanchous.quotesapp.feature.user;

import me.dimanchous.quotesapp.feature.user.exception.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationUserService {

    private final UserRepository userRepository;

    public ApplicationUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ApplicationUser getAuthenticatedUserFromDetails(UserDetails userDetails) {
        try {
            return getUserFromDetails(userDetails);
        } catch (UserNotFoundException e) {
            throw new RuntimeException("Authenticated user not found");
        }
    }

    public ApplicationUser getUserFromDetails(UserDetails userDetails) throws UserNotFoundException {
        Optional<ApplicationUser> userOptional = userRepository.findByUsername(userDetails.getUsername());
        if (userOptional.isEmpty()) throw new UserNotFoundException();
        return userOptional.get();
    }
}
