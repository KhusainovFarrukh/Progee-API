package kh.farrukh.progee_api.security.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static kh.farrukh.progee_api.utils.constant.JWTKeys.*;

/**
 * Utils for generating, decoding and sending tokens
 */
public class JWTUtils {
    // Used to format the date in the response.
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private static final DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

    // Creating an algorithm object which will be used to sign the token.
    private static final Algorithm algorithm = Algorithm.HMAC256(System.getenv("MY_JWT_SECRET").getBytes());
    // Value of the expiration time for the access token and refresh token.
    private static final int accessValidMillis = 30 * 60 * 1000;
    private static final int refreshValidMillis = 3 * 24 * 60 * 60 * 1000;

    /**
     * It creates two tokens, one for access and one for refresh, and returns them in a map
     *
     * @param user    The user object that is passed in from the controller.
     * @param request The request object is used to get the URL of the request.
     * @return A map of tokens and their expiration dates.
     */
    public static Map<String, Object> generateTokens(UserDetails user, HttpServletRequest request) {
        long currentMillis = System.currentTimeMillis();
        Date accessExpireDate = new Date(currentMillis + accessValidMillis);
        Date refreshExpireDate = new Date(currentMillis + refreshValidMillis);

        // Getting the roles of the user and converting them to a list of strings.
        String role = user.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()).stream()
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException(
                                "User don't have any role. Check user in database or add role to user from admin profile"
                        )
                );

        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(accessExpireDate)
                .withIssuer(request.getRequestURL().toString())
                .withClaim(KEY_ROLE, role)
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(refreshExpireDate)
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        Map<String, Object> data = new HashMap<>();
        data.put(KEY_ROLE, role);
        data.put(KEY_ACCESS_TOKEN, accessToken);
        data.put(KEY_REFRESH_TOKEN, refreshToken);
        data.put(KEY_ACCESS_TOKEN_EXPIRES, formatter.format(accessExpireDate));
        data.put(KEY_REFRESH_TOKEN_EXPIRES, formatter.format(refreshExpireDate));

        return data;
    }

    /**
     * If the request has an Authorization header with a Bearer token, then verify the token and return the decoded JWT
     *
     * @param request The request object
     * @return A DecodedJWT object
     */
    public static DecodedJWT decodeJWT(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            return jwtVerifier.verify(token);
        } else {
            return null;
        }
    }

    /**
     * It takes a decoded JWT and returns a UsernamePasswordAuthenticationToken with the username and roles from the JWT
     *
     * @param decodedJWT The decoded JWT.
     * @return A UsernamePasswordAuthenticationToken object
     */
    public static UsernamePasswordAuthenticationToken getAuthenticationFromDecodedJWT(DecodedJWT decodedJWT) {
        String username = decodedJWT.getSubject();
        GrantedAuthority authority = new SimpleGrantedAuthority(
                decodedJWT.getClaim(KEY_ROLE).asString()
        );

        return new UsernamePasswordAuthenticationToken(
                username, null, Collections.singletonList(authority)
        );
    }

    /**
     * It takes a map of data and an HttpServletResponse object, and writes the data to the response as JSON
     *
     * @param data     This is the data that you want to send back to the client.
     * @param response The HttpServletResponse object.
     */
    public static void sendTokenInResponse(Map<String, Object> data, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), data);
    }

    /**
     * It sets the access token and refresh token in the response header
     *
     * @param data     The map that contains the access token and refresh token.
     * @param response The response object of the request.
     */
    public static void sendTokenInHeader(Map<String, String> data, HttpServletResponse response) {
        response.setHeader(KEY_ACCESS_TOKEN, data.get(KEY_ACCESS_TOKEN));
        response.setHeader(KEY_REFRESH_TOKEN, data.get(KEY_REFRESH_TOKEN));
    }
}
