package kh.farrukh.progee_api.global.security.utils.request_wrapper;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static kh.farrukh.progee_api.global.security.utils.AuthenticationFilterConfigurer.ENDPOINT_LOGIN;

@Component
public class LoginRequestWrapperFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals(ENDPOINT_LOGIN);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
                new CachedBodyHttpServletRequest(request);
        filterChain.doFilter(cachedBodyHttpServletRequest, response);
    }
}
