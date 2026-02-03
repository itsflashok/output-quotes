package me.dimanchous.quotesapp.component;

import jakarta.servlet.http.HttpServletResponse;
import me.dimanchous.quotesapp.feature.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Utilities {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public Utilities(ObjectMapper objectMapper, UserRepository userRepository) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }


    public ResponseEntity<Map<String, Object>> buildFailure(HttpStatus status, List<Error> errors) {

        Objects.requireNonNull(errors);
        Objects.requireNonNull(status);

        if (!status.is4xxClientError() && !status.is5xxServerError()) throw new IllegalArgumentException("Cannot construct a failure request for a non-error status %s!".formatted(status.value()));

        Map<String, Object> responseMap = new ConcurrentHashMap<>();

        responseMap.put("errors", errors);
        responseMap.put("status", status.value());

        return ResponseEntity.status(status).body(responseMap);
    }


    public ResponseEntity<Map<String, Object>> buildSuccess(Object data) {
        return buildSuccess(HttpStatus.OK, data);
    }

    public ResponseEntity<Map<String, Object>> buildSuccess(HttpStatus status) {
        return buildSuccess(status, Map.of());
    }

    public ResponseEntity<Map<String, Object>> buildSuccess() {
        return buildSuccess(HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> buildSuccess(HttpStatus status, Object data) {

        Objects.requireNonNull(status);
        Objects.requireNonNull(data);

        if (status.is4xxClientError() || status.is5xxServerError()) throw new IllegalArgumentException("Cannot construct a successful request for an error status %s!".formatted(status.value()));


        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("data", data);

        return ResponseEntity.status(status).body(responseMap);
    }

    public List<String> getUserRolesAsStringList(UserDetails userDetails) {
        if (userDetails == null) return List.of();
        String rolePrefix = "ROLE_";
        return userDetails.getAuthorities().stream().map(authority -> {
            String authorityName = authority.getAuthority();
            if (authorityName == null) return "";
            if (authorityName.startsWith(rolePrefix)) {
                return authorityName.substring(rolePrefix.length());
            } else {
                return authorityName;
            }
        }).toList();
    }

    public record Error(
            String code,
            String title,
            String message
    ) {}


    public void writeResponseEntityToResponse(HttpServletResponse response, ResponseEntity<?> responseEntity) throws IOException {

        response.setStatus(responseEntity.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String responseBodyString;
        try {
            responseBodyString = objectMapper.writeValueAsString(responseEntity.getBody());
        } catch (JacksonException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Map<String, Object> responseBody = buildFailure(HttpStatus.INTERNAL_SERVER_ERROR, List.of(new Error("INTERNAL_SERVER_ERROR", "Internal server error", "An internal server error has occurred."))).getBody();
            responseBodyString = objectMapper.writeValueAsString(responseBody);
        }
        response.getWriter().write(responseBodyString);
    }
}

