package kh.farrukh.progee_api.security.handlers;

import kh.farrukh.progee_api.exception.custom_exceptions.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class EmailPasswordAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //    // TODO: 7/29/22 change to service
//    private final UserRepository userRepository;
    private final HandlerExceptionResolver resolver;

    public EmailPasswordAuthenticationEntryPoint(
//            UserRepository userRepository,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
//        this.userRepository = userRepository;
        this.resolver = resolver;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) {
//        // TODO: 7/29/22 separate message (logic) for 401 in /login and 401 in other endpoints
//        EmailPasswordInvalidException.Type type;
//        // TODO: 7/29/22 always being false because email is null
//        if (userRepository.existsByEmail(request.getParameter("email"))) {
//            type = EmailPasswordInvalidException.Type.PASSWORD;
//        } else {
//            type = EmailPasswordInvalidException.Type.EMAIL;
//        }
//        resolver.resolveException(request, response, null, new EmailPasswordInvalidException(type));

        resolver.resolveException(request, response, null, new InvalidCredentialsException());
    }
}
