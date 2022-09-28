package kh.farrukh.progee_api.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    /**
     * Register the JavaTimeModule with the ObjectMapper so that it can serialize and deserialize Java 8 Date & Time API
     * objects.
     *
     * @param builder The Jackson2ObjectMapperBuilder instance to use for the creation of the ObjectMapper.
     * @return An ObjectMapper bean.
     */
    @Bean
    @Primary
    ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    /**
     * Register the PasswordEncoder so that it can be used in Spring Security.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
