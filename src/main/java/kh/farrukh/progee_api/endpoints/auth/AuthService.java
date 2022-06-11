package kh.farrukh.progee_api.endpoints.auth;

import kh.farrukh.progee_api.endpoints.user.AppUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthService {

    AppUser register(RegistrationRequest registrationRequest);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
