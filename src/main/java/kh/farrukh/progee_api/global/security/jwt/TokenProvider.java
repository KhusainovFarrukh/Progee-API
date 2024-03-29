package kh.farrukh.progee_api.global.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.token_exceptions.MissingTokenException;
import kh.farrukh.progee_api.role.RoleMappers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

import static kh.farrukh.progee_api.global.security.jwt.JWTKeys.KEY_ROLE_ID;

/**
 * Component for providing and validating tokens
 */
@Getter
@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {

    private Algorithm accessTokenAlgorithm;
    private Algorithm refreshTokenAlgorithm;

    private final JwtConfiguration jwtConfiguration;

    @Override
    public void afterPropertiesSet() {
        accessTokenAlgorithm = Algorithm.HMAC256(jwtConfiguration.getSecret());
        refreshTokenAlgorithm = Algorithm.HMAC384(jwtConfiguration.getSecret());
    }

    /**
     * It creates two tokens, one for access and one for refresh, and returns them in an AuthResponseDTO object
     *
     * @param user The AuthResponseDTO object that contains the tokens' information.
     */
    public AuthResponseDTO generateTokens(AppUser user) {
        ZonedDateTime accessExpireDate = ZonedDateTime.now().plusSeconds(jwtConfiguration.getAccessTokenValidityInSeconds());
        ZonedDateTime refreshExpireDate = ZonedDateTime.now().plusSeconds(jwtConfiguration.getRefreshTokenValidityInSeconds());

        return new AuthResponseDTO(
                RoleMappers.toRoleResponseDTO(user.getRole()),
                createAccessToken(user, accessExpireDate),
                createRefreshToken(user, refreshExpireDate),
                accessExpireDate,
                refreshExpireDate
        );
    }

    /**
     * If the request has an Authorization header with a Bearer token, then verify the token and return the decoded JWT
     *
     * @param authHeader The refresh token in request header
     * @return A DecodedJWT object
     */
    public DecodedJWT validateToken(String authHeader, boolean isRefresh) {
        Algorithm algorithm;
        if (isRefresh) {
            algorithm = refreshTokenAlgorithm;
        } else {
            algorithm = accessTokenAlgorithm;
        }
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            return jwtVerifier.verify(token);
        } else {
            throw new MissingTokenException();
        }
    }

    /**
     * It creates an access token for the user with the given expiration date and the access token algorithm
     *
     * @param user The user for whom the token is being created.
     * @param expireDate The date and time when the token expires.
     * @return A token
     */
    public String createAccessToken(AppUser user, ZonedDateTime expireDate) {
        return createToken(user, expireDate, accessTokenAlgorithm);
    }

    /**
     * It creates a refresh token for the user with the given expiration date and the refresh token algorithm
     *
     * @param user The user for whom the token is being created.
     * @param expireDate The date and time when the token expires.
     * @return A token
     */
    public String createRefreshToken(AppUser user, ZonedDateTime expireDate) {
        return createToken(user, expireDate, refreshTokenAlgorithm);
    }

    /**
     * It creates a JWT token with the username as the subject, the expiration date as the expiration date,
     * and the permission names list as the claim
     *
     * @param user       The user object that contains the username and role.
     * @param expireDate The date when the token will expire.
     * @param algorithm  The algorithm to use for signing the token.
     * @return A JWT token
     */
    private String createToken(AppUser user, ZonedDateTime expireDate, Algorithm algorithm) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(Date.from(expireDate.toInstant()))
                .withClaim(KEY_ROLE_ID, user.getRole().getId())
                .sign(algorithm);
    }
}
