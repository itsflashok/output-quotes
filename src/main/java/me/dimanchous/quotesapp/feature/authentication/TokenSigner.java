package me.dimanchous.quotesapp.feature.authentication;

import org.springframework.stereotype.Component;

@Component
public class TokenSigner {
    private final String HMAC_ALGORITHM = "HmacSHA256";
}
