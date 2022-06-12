package kh.farrukh.progee_api.security.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.security.utils.JWTUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.*;

/**
 * If the request is not for the login or refresh token endpoints, then decode the JWT and set the authentication in the
 * security context
 */
public class CustomJWTAuthorizationFilter extends OncePerRequestFilter {

    // TODO: 6/12/22 needs comment
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals(ENDPOINT_LOGIN) ||
                request.getServletPath().equals(ENDPOINT_REFRESH_TOKEN) ||
                (request.getMethod().equals(HttpMethod.GET.name()) &&
                        request.getServletPath().contains(ENDPOINT_LANGUAGE) &&
                        request.getParameter("state") == null)) {
            filterChain.doFilter(request, response);
        } else {
            try {
                DecodedJWT decodedJWT = JWTUtils.decodeJWT(request);
                if (decodedJWT != null) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            JWTUtils.getAuthenticationFromDecodedJWT(decodedJWT);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } else {
                    filterChain.doFilter(request, response);
                }
            } catch (Exception exception) {
                response.sendError(HttpStatus.FORBIDDEN.value());
            }
        }
    }
}
