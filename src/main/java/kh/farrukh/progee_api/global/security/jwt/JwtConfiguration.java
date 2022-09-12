package kh.farrukh.progee_api.global.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("jwt")
public class JwtConfiguration {

    private String secret;
    private Long accessTokenValidityInSeconds;
    private Long refreshTokenValidityInSeconds;
}
