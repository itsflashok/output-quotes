package me.dimanchous.quotesapp.feature.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import me.dimanchous.quotesapp.component.Utilities;
import me.dimanchous.quotesapp.feature.authentication.dto.LoginRequest;
import me.dimanchous.quotesapp.feature.authentication.dto.LoginResponse;
import me.dimanchous.quotesapp.feature.authentication.dto.RegistrationRequest;
import me.dimanchous.quotesapp.feature.authentication.exception.UsernameAlreadyTakenException;
import me.dimanchous.quotesapp.feature.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final Utilities utilities;

    public AuthenticationController(UserRepository userRepository, AuthenticationService authenticationService, Utilities utilities) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.utilities = utilities;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticationState(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return utilities.buildFailure(HttpStatus.UNAUTHORIZED, List.of(
                new Utilities.Error("UNAUTHENTICATED", "User is not authenticated", "No authenticated user found for the request")
        ));
        return utilities.buildSuccess(new LoginResponse(authenticationService.getUserRoles(userDetails)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        List<String> userRoles;
        try {
            userRoles = authenticationService.loginUser(request, httpRequest);
        } catch (AuthenticationException e) {
            return utilities.buildFailure(HttpStatus.UNAUTHORIZED, List.of(
                    new Utilities.Error("INVALID_CREDENTIALS", "Invalid username or password", "The provided username or password is incorrect")
            ));
        }
        return utilities.buildSuccess(new LoginResponse(userRoles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) {
        try {
            authenticationService.registerUser(request);
        } catch (UsernameAlreadyTakenException e) {
            return utilities.buildFailure(HttpStatus.CONFLICT, List.of(
                    new Utilities.Error("USERNAME_ALREADY_TAKEN", "The username is already taken", "The username '%s' is already in use".formatted(request.username()))
            ));
        }
        return utilities.buildSuccess(HttpStatus.CREATED);
    }
}
