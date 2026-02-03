package me.dimanchous.quotesapp.feature.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import me.dimanchous.quotesapp.component.Utilities;
import me.dimanchous.quotesapp.feature.authentication.dto.LoginRequest;
import me.dimanchous.quotesapp.feature.authentication.dto.RegistrationRequest;
import me.dimanchous.quotesapp.feature.authentication.exception.UsernameAlreadyTakenException;
import me.dimanchous.quotesapp.feature.user.ApplicationUser;
import me.dimanchous.quotesapp.feature.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Utilities utilities;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, Utilities utilities) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.utilities = utilities;
    }

    public List<String> getUserRoles(UserDetails userDetails) {
        return utilities.getUserRolesAsStringList(userDetails);
    }

    public List<String> loginUser(LoginRequest request, HttpServletRequest httpRequest) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails == null ? List.of() : utilities.getUserRolesAsStringList(userDetails);
    }


    public void registerUser(RegistrationRequest request) throws UsernameAlreadyTakenException {
        Optional<ApplicationUser> existingUserOptional = userRepository.findByUsername(request.username());
        if (existingUserOptional.isPresent()) throw new UsernameAlreadyTakenException();

        ApplicationUser newUser = new ApplicationUser(
                request.username(),
                passwordEncoder.encode(request.password())
        );

        userRepository.save(newUser);
    }
}
