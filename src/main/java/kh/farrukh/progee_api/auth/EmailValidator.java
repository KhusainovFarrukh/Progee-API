package kh.farrukh.progee_api.auth;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * It's a predicate that returns true if the given string is a valid email address
 */
@Service
public class EmailValidator implements Predicate<String> {

    private final String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private final Pattern pattern = Pattern.compile(regex);

    @Override
    public boolean test(String email) {
        if (email == null) return false;
        return pattern.matcher(email).matches();
    }
}
