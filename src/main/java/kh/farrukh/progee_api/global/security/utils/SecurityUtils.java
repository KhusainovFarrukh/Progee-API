package kh.farrukh.progee_api.global.security.utils;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.role.Role;
import kh.farrukh.progee_api.role.RoleRepository;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static kh.farrukh.progee_api.global.security.jwt.JWTKeys.*;

/**
 * Utils for spring security base logic
 */
public class SecurityUtils {

    /**
     * It takes a decoded JWT and returns a UsernamePasswordAuthenticationToken with the username and permissions
     * from the JWT
     *
     * @param decodedJWT The decoded JWT.
     * @return A UsernamePasswordAuthenticationToken object
     */
    public static UsernamePasswordAuthenticationToken getAuthenticationFromDecodedJWT(
            DecodedJWT decodedJWT, RoleRepository roleRepository
    ) {
        String username = decodedJWT.getSubject();
        long roleId = decodedJWT.getClaim(KEY_ROLE_ID).asLong();
        Role role = roleRepository.findById(roleId).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", roleId)
        );

        List<SimpleGrantedAuthority> authorities = role
                .getPermissions()
                .stream().map(permission -> new SimpleGrantedAuthority(permission.name()))
                .toList();

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    /**
     * It takes AuthResponseDTO object and an HttpServletResponse object, and writes the data to the response as JSON
     *
     * @param authResponseDTO This is the AuthResponse that you want to send back to the client.
     * @param response        The HttpServletResponse object.
     */
    public static void sendTokenInResponse(
            AuthResponseDTO authResponseDTO,
            HttpServletResponse response,
            ObjectMapper objectMapper
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> data = objectMapper.convertValue(authResponseDTO,
                new TypeReference<>() {
                });
        objectMapper.writeValue(response.getOutputStream(), data);
    }
}
