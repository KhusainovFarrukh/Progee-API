package kh.farrukh.progee_api.security.utils;

import kh.farrukh.progee_api.security.filters.EmailPasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_LOGIN;

/**
 * This class is a custom DSL that adds a custom authentication filter to the Spring Security filter chain.
 *
 * The class extends AbstractHttpConfigurer, which is a class that provides a DSL for configuring the Spring Security
 * filter chain
 */
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

    /**
     * It returns a new instance of the `AuthenticationFilterConfigurer` class for using in apply() method of HttpSecurity
     *
     * @return A new instance of the AuthenticationFilterConfigurer class.
     */
    public static AuthenticationFilterConfigurer configureAuthenticationFilter() {
        return new AuthenticationFilterConfigurer();
    }
}
