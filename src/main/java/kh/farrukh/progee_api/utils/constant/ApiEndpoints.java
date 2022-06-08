package kh.farrukh.progee_api.utils.constant;

public class ApiEndpoints {

    public static final String ENDPOINT_HOME = "/api/v1";
    public static final String ENDPOINT_REGISTRATION = "api/v1/registration";
    public static final String ENDPOINT_LOGIN = "/api/v1/login";
    public static final String ENDPOINT_REFRESH_TOKEN = "/api/v1/token/refresh";

    public static final String ENDPOINT_LANGUAGE = "/api/v1/languages";
    public static final String ENDPOINT_FRAMEWORK = "api/v1/languages/{languageId}/frameworks";
    public static final String ENDPOINT_REVIEW = "/api/v1/languages/{languageId}/reviews";
    public static final String ENDPOINT_USER = "/api/v1/users";
    public static final String ENDPOINT_IMAGE = "/api/v1/images";

    public static String withChildEndpoints(String endpoint) {
        return endpoint + "/**";
    }
}
