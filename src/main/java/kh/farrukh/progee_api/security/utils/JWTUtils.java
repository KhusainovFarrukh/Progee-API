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
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static kh.farrukh.progee_api.utils.constant.JWTKeys.*;

public class JWTUtils {
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private static final DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

    private static final Algorithm algorithm = Algorithm.HMAC256("temp_secret".getBytes());
    private static final int accessValidMillis = 30 * 60 * 1000;
    private static final int refreshValidMillis = 3 * 24 * 60 * 60 * 1000;

    public static Map<String, Object> generateTokens(User user, HttpServletRequest request) {
        long currentMillis = System.currentTimeMillis();
        Date accessExpireDate = new Date(currentMillis + accessValidMillis);
        Date refreshExpireDate = new Date(currentMillis + refreshValidMillis);

        List<String> roles = user.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(accessExpireDate)
                .withIssuer(request.getRequestURL().toString())
                .withClaim(KEY_ROLES, roles)
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(refreshExpireDate)
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        Map<String, Object> data = new HashMap<>();
        data.put(KEY_ROLES, roles);
        data.put(KEY_ACCESS_TOKEN, accessToken);
        data.put(KEY_REFRESH_TOKEN, refreshToken);
        data.put(KEY_ACCESS_TOKEN_EXPIRES, formatter.format(accessExpireDate));
        data.put(KEY_REFRESH_TOKEN_EXPIRES, formatter.format(refreshExpireDate));

        return data;
    }

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

    public static UsernamePasswordAuthenticationToken getAuthenticationFromDecodedJWT(DecodedJWT decodedJWT) {
        String username = decodedJWT.getSubject();
        Collection<SimpleGrantedAuthority> authorities = decodedJWT.getClaim(KEY_ROLES)
                .asList(String.class)
                .stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    public static void sendTokenInResponse(Map<String, Object> data, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), data);
    }

    public static void sendTokenInHeader(Map<String, String> data, HttpServletResponse response) {
        response.setHeader(KEY_ACCESS_TOKEN, data.get(KEY_ACCESS_TOKEN));
        response.setHeader(KEY_REFRESH_TOKEN, data.get(KEY_REFRESH_TOKEN));
    }
}
