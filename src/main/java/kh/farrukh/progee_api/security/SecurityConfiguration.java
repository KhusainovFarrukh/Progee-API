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
        setOnlyAdminEditableEndpoint(withChildEndpoints(SECURITY_ENDPOINT_FRAMEWORK_BY_LANGUAGE_STATE), http);
        setOnlyAdminEditableEndpoint(withChildEndpoints(SECURITY_ENDPOINT_LANGUAGE_STATE), http);
        setAdminVerifiableEndpoint(withChildEndpoints(SECURITY_ENDPOINT_FRAMEWORK_BY_LANGUAGE), http);
        setAdminVerifiableEndpoint(withChildEndpoints(ENDPOINT_LANGUAGE), http);
        setUserCreatableEndpoint(withChildEndpoints(SECURITY_ENDPOINT_REVIEW_BY_LANGUAGE), http);
        setUserCreatableEndpoint(withChildEndpoints(ENDPOINT_IMAGE), http);
        setOnlySuperAdminEditableEndpoint(withChildEndpoints(ENDPOINT_USER), http);

        // Adding the custom DSL for the authentication manager and the custom JWT authorization filter.
        http.apply(customDsl());
        http.addFilterBefore(new CustomJWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * It sets the endpoint to be accessible by all users.
     *
     * @param endpoint The endpoint you want to set the permissions for.
     * @param http     The HttpSecurity object that is used to configure the security of the application.
     */
    private void setUserCreatableEndpoint(String endpoint, HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, endpoint).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name(), UserRole.USER.name());
        http.authorizeRequests().antMatchers(HttpMethod.PUT, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name(), UserRole.USER.name());
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name(), UserRole.USER.name());
    }

    /**
     * It sets the endpoint to be for admin-verified resource (when user created some resource).
     *
     * @param endpoint The endpoint you want to set the permissions for.
     * @param http     The HttpSecurity object that is used to configure the security of the application.
     */
    private void setAdminVerifiableEndpoint(String endpoint, HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, endpoint).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name(), UserRole.USER.name());
        http.authorizeRequests().antMatchers(HttpMethod.PUT, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
    }

    /**
     * It sets the endpoint to be accessible by only admins.
     *
     * @param endpoint The endpoint you want to set the permissions for.
     * @param http     The HttpSecurity object that is used to configure the security of the application.
     */
    private void setOnlyAdminEditableEndpoint(String endpoint, HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.POST, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.PUT, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
    }

    /**
     * It sets the endpoint to be accessible by only super admins (admins can access only GET requests).
     *
     * @param endpoint The endpoint you want to set the permissions for.
     * @param http     The HttpSecurity object that is used to configure the security of the application.
     */
    private void setOnlySuperAdminEditableEndpoint(String endpoint, HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.POST, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.PUT, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name());
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, endpoint).hasAnyAuthority(UserRole.SUPER_ADMIN.name());
    }
}
