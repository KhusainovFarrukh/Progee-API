package kh.farrukh.progee_api.security;

import kh.farrukh.progee_api.endpoints.user.UserRole;
import kh.farrukh.progee_api.security.filters.CustomJWTAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static kh.farrukh.progee_api.security.utils.CustomDslForAuthManager.customDsl;
import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.*;

/**
 * It configures the security of the application using Spring Security via JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * A function that is used to configure the security filter chain.
     *
     * @param http the HttpSecurity object
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disabling the CSRF and making the session stateless.
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Allowing all the users to access the login and refresh token endpoints.
        http.authorizeRequests().antMatchers(
                withChildEndpoints(ENDPOINT_REGISTRATION),
                withChildEndpoints(ENDPOINT_LOGIN),
                withChildEndpoints(ENDPOINT_REFRESH_TOKEN)
        ).permitAll();

        // Setting security for the other endpoints in the application.
        setEveryoneReadableEndpoint(withChildEndpoints(ENDPOINT_IMAGE), http);
        setEveryoneReadableEndpoint(withChildEndpoints(ENDPOINT_LANGUAGE), http);
        setEveryoneReadableEndpoint(withChildEndpoints(ENDPOINT_FRAMEWORK), http);
        setUserEditableEndpoint(withChildEndpoints(ENDPOINT_REVIEW), http);
        setOnlySuperAdminEndpoint(withChildEndpoints(ENDPOINT_USER), http);

        // Adding the custom DSL for the authentication manager and the custom JWT authorization filter.
        http.apply(customDsl());
        http.addFilterBefore(new CustomJWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * This function sets the endpoint to be readable by everyone, but only writable by the super admin and admin.
     *
     * @param endpoint The endpoint you want to set the permissions for.
     * @param http The HttpSecurity object that is used to configure the security of the application.
     */
    private void setEveryoneReadableEndpoint(String endpoint, HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, endpoint).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.PUT, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
    }

    /**
     * This function sets the endpoint to be accessible by all users for GET requests, and only accessible by users with
     * the SUPER_ADMIN, ADMIN, or USER authority for POST, PUT, and DELETE requests.
     *
     * @param endpoint The endpoint you want to set the permissions for.
     * @param http The HttpSecurity object that is used to configure the security of the application.
     */
    private void setUserEditableEndpoint(String endpoint, HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, endpoint).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name(), UserRole.USER.name());
        http.authorizeRequests().antMatchers(HttpMethod.PUT, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name(), UserRole.USER.name());
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
    }

    /**
     * This function sets the endpoint to be accessible only by the super admin and admin.
     *
     * @param endpoint The endpoint you want to restrict access to.
     * @param http The HttpSecurity object that is used to configure the security of the application.
     */
    private void setOnlySuperAdminEndpoint(String endpoint, HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.POST, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.PUT, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name());
    }
}
