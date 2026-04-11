package lv.venta.fidi.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class LocaleFilterConfig {

    @Bean
    public FilterRegistrationBean<LocalePathFilter> localePathFilterRegistration() {
        FilterRegistrationBean<LocalePathFilter> reg = new FilterRegistrationBean<>(new LocalePathFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }
}
