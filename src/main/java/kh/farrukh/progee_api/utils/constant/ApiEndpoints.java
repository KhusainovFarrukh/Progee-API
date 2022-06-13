package kh.farrukh.progee_api.utils.constant;

public class ApiEndpoints {

    public static final String ENDPOINT_HOME = "/api/v1";
    public static final String ENDPOINT_REGISTRATION = "api/v1/registration";
    public static final String ENDPOINT_LOGIN = "/api/v1/login";
    public static final String ENDPOINT_REFRESH_TOKEN = "/api/v1/token/refresh";

    public static final String ENDPOINT_LANGUAGE = "/api/v1/languages";
    public static final String SECURITY_ENDPOINT_LANGUAGE_STATE = ENDPOINT_LANGUAGE + "/**/state";
    public static final String ENDPOINT_FRAMEWORK_BY_LANGUAGE = "api/v1/languages/{languageId}/frameworks";
    public static final String SECURITY_ENDPOINT_FRAMEWORK_BY_LANGUAGE = ENDPOINT_LANGUAGE + "/**/frameworks";
    public static final String SECURITY_ENDPOINT_FRAMEWORK_BY_LANGUAGE_STATE = SECURITY_ENDPOINT_FRAMEWORK_BY_LANGUAGE + "/**/state";
    public static final String ENDPOINT_REVIEW_BY_LANGUAGE = "/api/v1/languages/{languageId}/reviews";
    public static final String SECURITY_ENDPOINT_REVIEW_BY_LANGUAGE = ENDPOINT_LANGUAGE + "/**/reviews";
    public static final String ENDPOINT_USER = "/api/v1/users";
    public static final String ENDPOINT_IMAGE = "/api/v1/images";

    public static String withChildEndpoints(String endpoint) {
        return endpoint + "/**";
    }
}
