package kh.farrukh.progee_api.security.utils;

import kh.farrukh.progee_api.security.filters.EmailPasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.stereotype.Component;

import static kh.farrukh.progee_api.utils.constants.ApiEndpoints.ENDPOINT_LOGIN;

/**
 * This class is a custom DSL that adds a custom authentication filter to the Spring Security filter chain.
 * <p>
 * The class extends AbstractHttpConfigurer, which is a class that provides a DSL for configuring the Spring Security
 * filter chain
 */
@Component
public class AuthenticationFilterConfigurer extends AbstractHttpConfigurer<AuthenticationFilterConfigurer, HttpSecurity> {

    /**
     * Add a custom filter to the http security chain that will be used to authenticate users.
     *
     * @param http The HttpSecurity object that is used to configure the security of the application.
     */
    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        EmailPasswordAuthenticationFilter authenticationFilter = new EmailPasswordAuthenticationFilter(authenticationManager);
        authenticationFilter.setFilterProcessesUrl(ENDPOINT_LOGIN);
        http.addFilter(authenticationFilter);
    }
}
