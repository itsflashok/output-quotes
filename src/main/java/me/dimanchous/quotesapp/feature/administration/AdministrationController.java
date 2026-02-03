package me.dimanchous.quotesapp.feature.administration;

import jakarta.validation.Valid;
import me.dimanchous.quotesapp.component.Utilities;
import me.dimanchous.quotesapp.feature.administration.dto.CreateAuthorRequest;
import me.dimanchous.quotesapp.feature.author.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/administration")
public class AdministrationController {

    private final AuthorService authorService;
    private final Utilities utilities;

    public AdministrationController(AuthorService authorService, Utilities utilities) {
        this.authorService = authorService;
        this.utilities = utilities;
    }

    @PostMapping("/author")
    public ResponseEntity<?> createAuthor(@Valid @RequestBody CreateAuthorRequest request) {
        return utilities.buildSuccess(authorService.createAuthor(request));
    }
}
