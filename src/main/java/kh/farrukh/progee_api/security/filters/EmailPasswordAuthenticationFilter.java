package kh.farrukh.progee_api.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.endpoints.auth.payloads.AuthResponseDTO;
import kh.farrukh.progee_api.endpoints.auth.payloads.LoginRequestDTO;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.AppUserRepository;
import kh.farrukh.progee_api.exceptions.custom_exceptions.EmailPasswordWrongException;
import kh.farrukh.progee_api.security.jwt.TokenProvider;
import kh.farrukh.progee_api.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * It extends the default UsernamePasswordAuthenticationFilter and overrides the attemptAuthentication and
 * successfulAuthentication methods
 */
@RequiredArgsConstructor
public class EmailPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final AppUserRepository appUserRepository;
    private final HandlerExceptionResolver resolver;
    private final ObjectMapper objectMapper;

    /**
     * This function is called when the user submits the login form. It takes the username and password from the form,
     * creates a UsernamePasswordAuthenticationToken, and passes it to the AuthenticationManager
     *
     * @param request  The request object that contains the username and password.
     * @param response The response object that will be used to send the token to the client.
     * @return An Authentication object.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginRequestDTO loginRequestDTO;

        try {
            loginRequestDTO = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        return authenticationManager.authenticate(authenticationToken);
    }

    /**
     * If the user is authenticated, generate a JWT token and send it in the response.
     *
     * @param request        The request object
     * @param response       The response object that will be sent to the client.
     * @param chain          The FilterChain object that is used to invoke the next filter in the chain.
     * @param authentication The Authentication object that was created by the AuthenticationManager.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        AppUser appUser = appUserRepository.findByEmail(user.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User not found in the database")
        );
        AuthResponseDTO authResponseDTO = tokenProvider.generateTokens(appUser);
        SecurityUtils.sendTokenInResponse(authResponseDTO, response, objectMapper);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        LoginRequestDTO loginRequestDTO = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);
        EmailPasswordWrongException.Type type;
        if (appUserRepository.existsByEmail(loginRequestDTO.getEmail())) {
            type = EmailPasswordWrongException.Type.PASSWORD;
        } else {
            type = EmailPasswordWrongException.Type.EMAIL;
        }
        resolver.resolveException(request, response, null, new EmailPasswordWrongException(type));
    }
}
