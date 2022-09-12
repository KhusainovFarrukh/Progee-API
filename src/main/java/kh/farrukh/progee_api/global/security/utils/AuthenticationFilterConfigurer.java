package kh.farrukh.progee_api.global.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.security.jwt.TokenProvider;
import kh.farrukh.progee_api.global.security.filters.EmailPasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * This class is a custom DSL that adds a custom authentication filter to the Spring Security filter chain.
 * <p>
 * The class extends AbstractHttpConfigurer, which is a class that provides a DSL for configuring the Spring Security
 * filter chain
 */
@Component
public class AuthenticationFilterConfigurer extends AbstractHttpConfigurer<AuthenticationFilterConfigurer, HttpSecurity> {

    public static final String ENDPOINT_LOGIN = "/api/v1/login";

    private final TokenProvider tokenProvider;
    private final AppUserRepository appUserRepository;
    private final HandlerExceptionResolver resolver;
    private final ObjectMapper objectMapper;

    public AuthenticationFilterConfigurer(
            TokenProvider tokenProvider,
            AppUserRepository appUserRepository,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            ObjectMapper objectMapper
    ) {
        this.tokenProvider = tokenProvider;
        this.appUserRepository = appUserRepository;
        this.resolver = resolver;
        this.objectMapper = objectMapper;
    }

    /**
     * Add a custom filter to the http security chain that will be used to authenticate users.
     *
     * @param http The HttpSecurity object that is used to configure the security of the application.
     */
    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        EmailPasswordAuthenticationFilter authenticationFilter = new EmailPasswordAuthenticationFilter(
                authenticationManager, tokenProvider, appUserRepository, resolver, objectMapper
        );
        authenticationFilter.setFilterProcessesUrl(ENDPOINT_LOGIN);
        http.addFilter(authenticationFilter);
    }
}
