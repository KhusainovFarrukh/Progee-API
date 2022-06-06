package kh.farrukh.progee_api.utils.i18n;

/**
 * It configures the locale change interceptor to look for a parameter named lang in the request and then sets the locale
 * accordingly
 *
 * Delete it to take preferred language from request header (Accept-Language)
 */
//@Configuration
//public class LocalizationConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(localeChangeInterceptor());
//    }
//
//    @Bean
//    public LocaleChangeInterceptor localeChangeInterceptor() {
//        LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
//        localeInterceptor.setParamName("lang");
//        return localeInterceptor;
//    }
//
//    @Bean
//    public LocaleResolver localeResolver() {
//        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
//        localeResolver.setDefaultLocale(Locale.ENGLISH);
//        return localeResolver;
//    }
//}