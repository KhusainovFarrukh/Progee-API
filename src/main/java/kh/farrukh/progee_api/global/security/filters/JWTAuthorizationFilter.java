package kh.farrukh.progee_api.global.security.filters;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.progee_api.role.RoleRepository;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.token_exceptions.*;
import kh.farrukh.progee_api.global.security.jwt.TokenProvider;
import kh.farrukh.progee_api.global.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static kh.farrukh.progee_api.app_user.AppUserConstants.ENDPOINT_USER;
import static kh.farrukh.progee_api.auth.AuthConstants.ENDPOINT_REFRESH_TOKEN;
import static kh.farrukh.progee_api.auth.AuthConstants.ENDPOINT_REGISTRATION;
import static kh.farrukh.progee_api.framework.FrameworkConstants.ENDPOINT_FRAMEWORK;
import static kh.farrukh.progee_api.global.security.utils.AuthenticationFilterConfigurer.ENDPOINT_LOGIN;
import static kh.farrukh.progee_api.home.HomeConstants.ENDPOINT_HOME;
import static kh.farrukh.progee_api.image.ImageConstants.ENDPOINT_IMAGE;
import static kh.farrukh.progee_api.language.LanguageConstants.ENDPOINT_LANGUAGE;
import static kh.farrukh.progee_api.review.ReviewConstants.ENDPOINT_REVIEW;

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

    /**
     * If the request is for the home/register/login/refresh-token endpoints or simple user request for getting list of
     * languages/frameworks/reviews, then don't check JWT token
     *
     * @param request The request object.
     * @return The method returns true if the request is for the home/register/login/refresh-token endpoints or simple user
     * request for getting list of languages/frameworks/reviews.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        // home request
        return request.getRequestURI().equals(ENDPOINT_HOME) ||

                // register request
                request.getRequestURI().equals(ENDPOINT_REGISTRATION) ||

                // login request
                request.getRequestURI().equals(ENDPOINT_LOGIN) ||

                // refresh token request
                request.getRequestURI().equals(ENDPOINT_REFRESH_TOKEN) ||

                // get users request
                (request.getRequestURI().contains(ENDPOINT_USER) && request.getMethod().equals(HttpMethod.GET.name())) ||

                // get reviews request
                (request.getRequestURI().contains(ENDPOINT_REVIEW) && request.getMethod().equals(HttpMethod.GET.name())) ||

                // upload or download image request
                (request.getRequestURI().contains(ENDPOINT_IMAGE) && (request.getMethod().equals(HttpMethod.GET.name()) || request.getMethod().equals(HttpMethod.POST.name()))) ||

                // get languages request without filter by state from non-logged user
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getRequestURI().contains(ENDPOINT_LANGUAGE) && (request.getParameter("state") == null) && request.getHeader(HttpHeaders.AUTHORIZATION) == null) ||

                // get frameworks request without filter by state from non-logged user
                (request.getMethod().equals(HttpMethod.GET.name()) && request.getRequestURI().contains(ENDPOINT_FRAMEWORK) && (request.getParameter("state") == null) && request.getHeader(HttpHeaders.AUTHORIZATION) == null);
    }

    /**
     * It validates the token, sets the authentication token, and then passes the request to the next filter
     *
     * @param request The request object
     * @param response The response object that will be used to send the error response.
     * @param filterChain The filter chain that the request will be passed through.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
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
