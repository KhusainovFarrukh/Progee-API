package kh.farrukh.progee_api.global.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * It's a configuration class that reads the values from the application.yml file and makes them available to the
 * application
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("jwt")
public class JwtConfiguration {

    private String secret;
    private Long accessTokenValidityInSeconds;
    private Long refreshTokenValidityInSeconds;
}
