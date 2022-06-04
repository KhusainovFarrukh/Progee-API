package kh.farrukh.progee_api.security;

import kh.farrukh.progee_api.security.filter.CustomJWTAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static kh.farrukh.progee_api.security.CustomDslForAuthManager.customDsl;
import static kh.farrukh.progee_api.endpoints.role.Role.*;
import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.*;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests().antMatchers(withChildEndpoints(ENDPOINT_LOGIN), withChildEndpoints(ENDPOINT_REFRESH_TOKEN)).permitAll();


        setEveryoneReadableEndpoint(withChildEndpoints(ENDPOINT_LANGUAGE), http);
        setEveryoneReadableEndpoint(withChildEndpoints(ENDPOINT_FRAMEWORK), http);
        setUserEditableEndpoint(withChildEndpoints(ENDPOINT_REVIEW), http);
        setOnlySuperAdminEndpoint(withChildEndpoints(ENDPOINT_USER), http);

        http.authorizeRequests().antMatchers(withChildEndpoints(ENDPOINT_ROLE)).hasAnyAuthority(SUPER_ADMIN);
        http.authorizeRequests().antMatchers(withChildEndpoints(ENDPOINT_ROLE_TO_USER)).hasAnyAuthority(SUPER_ADMIN);

        http.apply(customDsl());
        http.addFilterBefore(new CustomJWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void setEveryoneReadableEndpoint(String endpoint, HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, endpoint).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, endpoint).hasAnyAuthority(SUPER_ADMIN, ADMIN);
        http.authorizeRequests().antMatchers(HttpMethod.PUT, endpoint).hasAnyAuthority(SUPER_ADMIN, ADMIN);
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, endpoint).hasAnyAuthority(SUPER_ADMIN, ADMIN);
    }

    private void setUserEditableEndpoint(String endpoint, HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, endpoint).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, endpoint).hasAnyAuthority(SUPER_ADMIN, ADMIN, USER);
        http.authorizeRequests().antMatchers(HttpMethod.PUT, endpoint).hasAnyAuthority(SUPER_ADMIN, ADMIN, USER);
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, endpoint).hasAnyAuthority(SUPER_ADMIN, ADMIN);
    }

    private void setOnlySuperAdminEndpoint(String endpoint, HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, endpoint).hasAnyAuthority(SUPER_ADMIN, ADMIN);
        http.authorizeRequests().antMatchers(HttpMethod.POST, endpoint).hasAnyAuthority(SUPER_ADMIN);
        http.authorizeRequests().antMatchers(HttpMethod.PUT, endpoint).hasAnyAuthority(SUPER_ADMIN);
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, endpoint).hasAnyAuthority(SUPER_ADMIN);

    }
}
