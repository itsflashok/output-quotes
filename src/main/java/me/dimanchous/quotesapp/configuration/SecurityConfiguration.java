package me.dimanchous.quotesapp.configuration;

import me.dimanchous.quotesapp.component.Utilities;
import me.dimanchous.quotesapp.feature.user.ApplicationUser;
import me.dimanchous.quotesapp.feature.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Utilities utilities, CorsConfigurationSource corsConfigurationSource) {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers("/api/authentication/**").permitAll()
                            .requestMatchers("/api/administration/**").hasRole("ADMIN")

                            .requestMatchers(HttpMethod.GET, "/api/submissions").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/submissions").authenticated()
                            .requestMatchers(HttpMethod.POST, "/api/submissions/*/vote").authenticated()

                            .requestMatchers(HttpMethod.GET, "/api/authors").permitAll()

                            .requestMatchers(HttpMethod.GET, "/api/publications").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/publications/*/react").authenticated()
                            .anyRequest().authenticated();
                })
//                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .logout((logout) -> logout.logoutUrl("/api/authentication/logout").logoutSuccessHandler((request, response, authentication) -> {
                    ResponseEntity<?> responseEntity = utilities.buildSuccess(HttpStatus.OK);
                    utilities.writeResponseEntityToResponse(response, responseEntity);
                }))
                .exceptionHandling(exceptions -> {
                    exceptions
                            .authenticationEntryPoint(
                                    (request, response, exception) -> {
                                        ResponseEntity<?> responseEntity = utilities.buildFailure(HttpStatus.UNAUTHORIZED, List.of(
                                                new Utilities.Error("UNAUTHORIZED", "Authentication required", "You must be authenticated to access this resource")
                                        ));
                                        utilities.writeResponseEntityToResponse(response, responseEntity);
                                    }
                            )
                            .accessDeniedHandler(
                                    (request, response, exception) -> {
                                        ResponseEntity<?> responseEntity = utilities.buildFailure(HttpStatus.FORBIDDEN, List.of(
                                                new Utilities.Error("FORBIDDEN", "Access denied", "You do not have permission to access this resource")
                                        ));
                                        utilities.writeResponseEntityToResponse(response, responseEntity);
                                    }
                            );
                });
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174", "https://output.center"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            Optional<ApplicationUser> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) throw new UsernameNotFoundException(username);
            ApplicationUser applicationUser = userOptional.get();

            return new User(
                    applicationUser.getUsername(),
                    applicationUser.getPassword(),
                    applicationUser.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).toList()
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
