package kh.farrukh.progee_api.security.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * This class is a Spring configuration class that configures a BCryptPasswordEncoder bean.
 */
@Configuration
public class PasswordEncoder {

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
