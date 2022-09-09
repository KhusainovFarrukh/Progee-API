package kh.farrukh.progee_api.security.filters;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.endpoints.role.RoleRepository;
import kh.farrukh.progee_api.exceptions.custom_exceptions.token_exceptions.*;
import kh.farrukh.progee_api.security.jwt.TokenProvider;
import kh.farrukh.progee_api.security.utils.SecurityUtils;
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

import static kh.farrukh.progee_api.endpoints.auth.AuthController.ENDPOINT_REFRESH_TOKEN;
import static kh.farrukh.progee_api.endpoints.auth.AuthController.ENDPOINT_REGISTRATION;
import static kh.farrukh.progee_api.endpoints.framework.FrameworkController.ENDPOINT_FRAMEWORK;
import static kh.farrukh.progee_api.endpoints.home.HomeController.ENDPOINT_HOME;
import static kh.farrukh.progee_api.endpoints.image.ImageController.ENDPOINT_IMAGE;
import static kh.farrukh.progee_api.endpoints.language.LanguageController.ENDPOINT_LANGUAGE;
import static kh.farrukh.progee_api.security.utils.AuthenticationFilterConfigurer.ENDPOINT_LOGIN;

/**
 * If the request is not for the login or refresh token endpoints, then decode the JWT and set the authentication in the
 * security context
 */
@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final RoleRepository roleRepository;
    private final HandlerExceptionResolver resolver;

    public JWTAuthorizationFilter(
            TokenProvider tokenProvider,
            RoleRepository roleRepository,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        this.tokenProvider = tokenProvider;
        this.roleRepository = roleRepository;
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

                // get languages request without filter by state from non-logged user
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getRequestURI().contains(ENDPOINT_LANGUAGE) && (request.getParameter("state") == null) && request.getHeader(HttpHeaders.AUTHORIZATION) == null) ||

                // get frameworks request without filter by state from non-logged user
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getRequestURI().contains(ENDPOINT_FRAMEWORK) && (request.getParameter("state") == null) && request.getHeader(HttpHeaders.AUTHORIZATION) == null);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) {
        try {
            DecodedJWT decodedJWT = tokenProvider.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION), false);
            UsernamePasswordAuthenticationToken authenticationToken =
                    SecurityUtils.getAuthenticationFromDecodedJWT(decodedJWT, roleRepository);
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
        } catch (MissingTokenException exception) {
            resolver.resolveException(request, response, null, new MissingTokenException());
        } catch (Exception exception) {
            exception.printStackTrace();
            resolver.resolveException(request, response, null, new UnknownTokenException());
        }
    }
}
