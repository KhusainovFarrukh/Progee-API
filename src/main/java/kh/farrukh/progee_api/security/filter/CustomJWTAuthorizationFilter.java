package kh.farrukh.progee_api.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.security.utils.JWTUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_LOGIN;
import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_REFRESH_TOKEN;

public class CustomJWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals(ENDPOINT_LOGIN) || request.getServletPath().equals(ENDPOINT_REFRESH_TOKEN)) {
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
