package me.dimanchous.quotesapp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class MainConfiguration {

    @Bean
    public ZoneId timeZoneId() {
        return ZoneId.of("Europe/Berlin");
    }
}
