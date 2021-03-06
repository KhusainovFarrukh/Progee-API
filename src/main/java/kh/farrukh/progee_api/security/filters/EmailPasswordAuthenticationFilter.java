package kh.farrukh.progee_api.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.endpoints.auth.AuthResponse;
import kh.farrukh.progee_api.security.utils.JWTUtils;
import kh.farrukh.progee_api.endpoints.auth.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
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
        LoginRequest loginRequest;

        try {
            loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
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
        AuthResponse authResponse = JWTUtils.generateTokens(user);
        JWTUtils.sendTokenInResponse(authResponse, response);
    }
}
