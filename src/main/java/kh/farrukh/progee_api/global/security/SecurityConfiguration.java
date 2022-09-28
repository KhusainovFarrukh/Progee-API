package kh.farrukh.progee_api.global.security;

import kh.farrukh.progee_api.global.security.filters.JWTAuthorizationFilter;
import kh.farrukh.progee_api.global.security.handlers.JWTAccessDeniedHandler;
import kh.farrukh.progee_api.global.security.utils.AuthenticationFilterConfigurer;
import kh.farrukh.progee_api.global.security.utils.request_wrapper.LoginRequestWrapperFilter;
import kh.farrukh.progee_api.role.Permission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static kh.farrukh.progee_api.app_user.AppUserConstants.ENDPOINT_USER;
import static kh.farrukh.progee_api.auth.AuthConstants.ENDPOINT_REFRESH_TOKEN;
import static kh.farrukh.progee_api.auth.AuthConstants.ENDPOINT_REGISTRATION;
import static kh.farrukh.progee_api.framework.FrameworkConstants.ENDPOINT_FRAMEWORK;
import static kh.farrukh.progee_api.global.security.utils.AuthenticationFilterConfigurer.ENDPOINT_LOGIN;
import static kh.farrukh.progee_api.home.HomeConstants.ENDPOINT_HOME;
import static kh.farrukh.progee_api.image.ImageConstants.ENDPOINT_IMAGE;
import static kh.farrukh.progee_api.language.LanguageConstants.ENDPOINT_LANGUAGE;
import static kh.farrukh.progee_api.review.ReviewConstants.ENDPOINT_REVIEW;
import static kh.farrukh.progee_api.role.RoleConstants.ENDPOINT_ROLE;

/**
 * It configures the security of the application using Spring Security via JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    public static final String ENDPOINT_REVIEW_VOTE = ENDPOINT_REVIEW + "/**/vote";
    public static final String ENDPOINT_LANGUAGE_STATE = ENDPOINT_LANGUAGE + "/**/state";
    public static final String ENDPOINT_FRAMEWORK_STATE = ENDPOINT_FRAMEWORK + "/**/state";
    public static final String ENDPOINT_USER_ROLE = ENDPOINT_USER + "/**/role";

    /**
     * A function that is used to configure the security filter chain.
     *
     * @param http the HttpSecurity object
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JWTAuthorizationFilter authorizationFilter,
            LoginRequestWrapperFilter loginRequestWrapperFilter,
            AuthenticationFilterConfigurer authenticationFilterConfigurer,
            JWTAccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        // Disabling the CSRF and making the session stateless.
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Adding the custom DSL for the authentication manager and the custom JWT authorization filter.
        http.apply(authenticationFilterConfigurer)
                .and()
                .addFilterBefore(loginRequestWrapperFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                //home endpoint
                .antMatchers(HttpMethod.GET, ENDPOINT_HOME).permitAll()
                //image endpoints
                .antMatchers(HttpMethod.GET, withChildEndpoints(ENDPOINT_IMAGE)).permitAll()
                .antMatchers(HttpMethod.POST, withChildEndpoints(ENDPOINT_IMAGE)).permitAll()
                .antMatchers(HttpMethod.PUT, withChildEndpoints(ENDPOINT_IMAGE)).hasAuthority(Permission.CAN_UPDATE_IMAGE.name())
                .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_IMAGE)).hasAuthority(Permission.CAN_UPDATE_IMAGE.name())
                .antMatchers(HttpMethod.DELETE, withChildEndpoints(ENDPOINT_IMAGE)).hasAuthority(Permission.CAN_DELETE_IMAGE.name())
                //review endpoints
                .antMatchers(HttpMethod.GET, withChildEndpoints(ENDPOINT_REVIEW)).permitAll()
                .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_REVIEW_VOTE)).hasAuthority(Permission.CAN_VOTE_REVIEW.name())
                .antMatchers(HttpMethod.POST, withChildEndpoints(ENDPOINT_REVIEW)).hasAuthority(Permission.CAN_CREATE_REVIEW.name())
                .antMatchers(HttpMethod.PUT, withChildEndpoints(ENDPOINT_REVIEW)).hasAnyAuthority(Permission.CAN_UPDATE_OWN_REVIEW.name(), Permission.CAN_UPDATE_OTHERS_REVIEW.name())
                .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_REVIEW)).hasAnyAuthority(Permission.CAN_UPDATE_OWN_REVIEW.name(), Permission.CAN_UPDATE_OTHERS_REVIEW.name())
                .antMatchers(HttpMethod.DELETE, withChildEndpoints(ENDPOINT_REVIEW)).hasAuthority(Permission.CAN_DELETE_OWN_REVIEW.name())
                //framework endpoints
                .antMatchers(HttpMethod.GET, withChildEndpoints(ENDPOINT_FRAMEWORK)).permitAll()
                .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_FRAMEWORK_STATE)).hasAuthority(Permission.CAN_SET_FRAMEWORK_STATE.name())
                .antMatchers(HttpMethod.POST, withChildEndpoints(ENDPOINT_FRAMEWORK)).hasAuthority(Permission.CAN_CREATE_FRAMEWORK.name())
                .antMatchers(HttpMethod.PUT, withChildEndpoints(ENDPOINT_FRAMEWORK)).hasAnyAuthority(Permission.CAN_UPDATE_OWN_FRAMEWORK.name(), Permission.CAN_UPDATE_OTHERS_FRAMEWORK.name())
                .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_FRAMEWORK)).hasAnyAuthority(Permission.CAN_UPDATE_OWN_FRAMEWORK.name(), Permission.CAN_UPDATE_OTHERS_FRAMEWORK.name())
                .antMatchers(HttpMethod.DELETE, withChildEndpoints(ENDPOINT_FRAMEWORK)).hasAuthority(Permission.CAN_DELETE_FRAMEWORK.name())
                //language endpoints
                .antMatchers(HttpMethod.GET, withChildEndpoints(ENDPOINT_LANGUAGE)).permitAll()
                .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_LANGUAGE_STATE)).hasAuthority(Permission.CAN_SET_LANGUAGE_STATE.name())
                .antMatchers(HttpMethod.POST, withChildEndpoints(ENDPOINT_LANGUAGE)).hasAuthority(Permission.CAN_CREATE_LANGUAGE.name())
                .antMatchers(HttpMethod.PUT, withChildEndpoints(ENDPOINT_LANGUAGE)).hasAnyAuthority(Permission.CAN_UPDATE_OWN_LANGUAGE.name(), Permission.CAN_UPDATE_OTHERS_FRAMEWORK.name())
                .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_LANGUAGE)).hasAnyAuthority(Permission.CAN_UPDATE_OWN_LANGUAGE.name(), Permission.CAN_UPDATE_OTHERS_FRAMEWORK.name())
                .antMatchers(HttpMethod.DELETE, withChildEndpoints(ENDPOINT_LANGUAGE)).hasAuthority(Permission.CAN_DELETE_LANGUAGE.name())
                //user endpoints
                .antMatchers(HttpMethod.GET, withChildEndpoints(ENDPOINT_USER)).permitAll()
                .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_USER_ROLE)).hasAuthority(Permission.CAN_SET_USER_ROLE.name())
                .antMatchers(HttpMethod.PUT, withChildEndpoints(ENDPOINT_USER)).hasAnyAuthority(Permission.CAN_UPDATE_OWN_USER.name(), Permission.CAN_UPDATE_OTHER_USER.name())
                .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_USER)).hasAnyAuthority(Permission.CAN_UPDATE_OWN_USER.name(), Permission.CAN_UPDATE_OTHER_USER.name())
                .antMatchers(HttpMethod.DELETE, withChildEndpoints(ENDPOINT_USER)).hasAuthority(Permission.CAN_DELETE_USER.name())
                //role endpoints
                .antMatchers(HttpMethod.GET, withChildEndpoints(ENDPOINT_ROLE)).hasAuthority(Permission.CAN_VIEW_ROLE.name())
                .antMatchers(HttpMethod.POST, withChildEndpoints(ENDPOINT_ROLE)).hasAuthority(Permission.CAN_CREATE_ROLE.name())
                .antMatchers(HttpMethod.PUT, withChildEndpoints(ENDPOINT_ROLE)).hasAuthority(Permission.CAN_UPDATE_ROLE.name())
                .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_ROLE)).hasAuthority(Permission.CAN_UPDATE_ROLE.name())
                .antMatchers(HttpMethod.DELETE, withChildEndpoints(ENDPOINT_ROLE)).hasAuthority(Permission.CAN_DELETE_ROLE.name())
                //auth endpoints
                .antMatchers(
                        withChildEndpoints(ENDPOINT_REGISTRATION),
                        withChildEndpoints(ENDPOINT_LOGIN),
                        withChildEndpoints(ENDPOINT_REFRESH_TOKEN)
                ).permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);

        return http.build();
    }

    /**
     * It takes an endpoint and returns the endpoint with a wildcard appended to it
     *
     * @param endpoint The endpoint to be secured.
     * @return A string with the endpoint and a wildcard.
     */
    public static String withChildEndpoints(String endpoint) {
        return endpoint + "/**";
    }
}
