package kh.farrukh.progee_api.utils.constant;

/**
 * It contains all the endpoints of the application
 */
public class ApiEndpoints {

    public static final String ENDPOINT_HOME = "";
    public static final String ENDPOINT_REGISTRATION = "api/v1/registration";
    public static final String ENDPOINT_LOGIN = "/api/v1/login";
    public static final String ENDPOINT_REFRESH_TOKEN = "/api/v1/token/refresh";

    public static final String ENDPOINT_LANGUAGE = "/api/v1/languages";
    public static final String SECURITY_ENDPOINT_LANGUAGE_STATE = ENDPOINT_LANGUAGE + "/**/state";
    public static final String ENDPOINT_FRAMEWORK = "api/v1/languages/{languageId}/frameworks";
    public static final String SECURITY_ENDPOINT_FRAMEWORK = ENDPOINT_LANGUAGE + "/**/frameworks";
    public static final String SECURITY_ENDPOINT_FRAMEWORK_STATE = SECURITY_ENDPOINT_FRAMEWORK + "/**/state";
    public static final String ENDPOINT_REVIEW = "/api/v1/languages/{languageId}/reviews";
    public static final String SECURITY_ENDPOINT_REVIEW = ENDPOINT_LANGUAGE + "/**/reviews";
    public static final String ENDPOINT_USER = "/api/v1/users";
    public static final String ENDPOINT_IMAGE = "/api/v1/images";
    public static final String SECURITY_ENDPOINT_DOWNLOAD = ENDPOINT_IMAGE + "/**/download";

    public static String withChildEndpoints(String endpoint) {
        return endpoint + "/**";
    }
}
