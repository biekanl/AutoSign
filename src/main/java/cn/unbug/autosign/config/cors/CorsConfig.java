package cn.unbug.autosign.config.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author zhangtao
 */
@Component
public class CorsConfig {

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1
        corsConfiguration.addAllowedOrigin("*");
        // 2
        corsConfiguration.addAllowedHeader("*");
        // 3
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 4
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

}
