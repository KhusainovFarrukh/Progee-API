package kh.farrukh.progee_api.security.filters;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.exception.custom_exceptions.token_exceptions.*;
import kh.farrukh.progee_api.security.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static kh.farrukh.progee_api.utils.constants.ApiEndpoints.*;

/**
 * If the request is not for the login or refresh token endpoints, then decode the JWT and set the authentication in the
 * security context
 */
@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver resolver;

    public JWTAuthorizationFilter(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        this.resolver = resolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // This is the shouldNotFilter logic of the filter. If the request is for the home/register/login/refresh-token endpoints or
        // simple user request for getting list of languages/frameworks/reviews, then don't check JWT token.

        // if..
        // home request
        return request.getRequestURI().equals(ENDPOINT_HOME) ||
                
                // register request
                request.getRequestURI().equals(ENDPOINT_REGISTRATION) ||

                // login request
                request.getRequestURI().equals(ENDPOINT_LOGIN) ||

                // refresh token request
                request.getRequestURI().equals(ENDPOINT_REFRESH_TOKEN) ||

                // upload or download image request
                (request.getRequestURI().contains(ENDPOINT_IMAGE) && (request.getMethod().equals(HttpMethod.GET.name()) || request.getMethod().equals(HttpMethod.POST.name()))) ||

                // get languages/frameworks request without filter by state
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getRequestURI().contains(ENDPOINT_LANGUAGE) && (request.getParameter("state") == null));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) {
        try {
            DecodedJWT decodedJWT = JWTUtils.decodeJWT(request.getHeader(HttpHeaders.AUTHORIZATION), false);
            UsernamePasswordAuthenticationToken authenticationToken =
                    JWTUtils.getAuthenticationFromDecodedJWT(decodedJWT);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (AlgorithmMismatchException exception) {
            resolver.resolveException(request, response, null, new WrongTypeTokenException());
        } catch (SignatureVerificationException exception) {
            resolver.resolveException(request, response, null, new InvalidSignatureTokenException());
        } catch (TokenExpiredException exception) {
            resolver.resolveException(request, response, null, new ExpiredTokenException());
        } catch (InvalidClaimException exception) {
            resolver.resolveException(request, response, null, new InvalidRoleTokenException());
        } catch (Exception exception) {
            resolver.resolveException(request, response, null, new UnknownTokenException());
        }
    }
}
