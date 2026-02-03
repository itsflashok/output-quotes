package me.dimanchous.quotesapp.feature.author;

import me.dimanchous.quotesapp.component.Utilities;
import me.dimanchous.quotesapp.feature.author.dto.AuthorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final Utilities utilities;

    public AuthorController(AuthorService authorService, Utilities utilities) {
        this.authorService = authorService;
        this.utilities = utilities;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageRequest = PageRequest.of(page, 10);
        Page<AuthorResponse> authors = authorService.findAll(pageRequest);
        return utilities.buildSuccess(new PagedModel<>(authors));
    }
}
