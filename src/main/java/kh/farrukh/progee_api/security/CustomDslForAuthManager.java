package kh.farrukh.progee_api.security;

import kh.farrukh.progee_api.security.filters.CustomUsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_LOGIN;

/**
 * This class is a custom DSL that adds a custom authentication filter to the Spring Security filter chain.
 * <p>
 * The class extends AbstractHttpConfigurer, which is a class that provides a DSL for configuring the Spring Security
 * filter chain
 */
public class CustomDslForAuthManager extends AbstractHttpConfigurer<CustomDslForAuthManager, HttpSecurity> {

    /**
     * Add a custom filter to the http security chain that will be used to authenticate users.
     *
     * @param http The HttpSecurity object that is used to configure the security of the application.
     */
    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        CustomUsernamePasswordAuthenticationFilter authenticationFilter = new CustomUsernamePasswordAuthenticationFilter(authenticationManager);
        authenticationFilter.setFilterProcessesUrl(ENDPOINT_LOGIN);
        http.addFilter(authenticationFilter);
    }

    /**
     * It returns a new instance of the `CustomDslForAuthManager` class for using in apply() method of HttpSecurity
     *
     * @return A new instance of the CustomDslForAuthManager class.
     */
    public static CustomDslForAuthManager customDsl() {
        return new CustomDslForAuthManager();
    }
}
