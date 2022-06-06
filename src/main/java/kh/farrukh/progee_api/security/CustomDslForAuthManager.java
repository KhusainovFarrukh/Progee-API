package kh.farrukh.progee_api.security;

import kh.farrukh.progee_api.security.filters.CustomUsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_LOGIN;

public class CustomDslForAuthManager extends AbstractHttpConfigurer<CustomDslForAuthManager, HttpSecurity> {

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        CustomUsernamePasswordAuthenticationFilter authenticationFilter = new CustomUsernamePasswordAuthenticationFilter(authenticationManager);
        authenticationFilter.setFilterProcessesUrl(ENDPOINT_LOGIN);
        http.addFilter(authenticationFilter);
    }

    public static CustomDslForAuthManager customDsl() {
        return new CustomDslForAuthManager();
    }
}
