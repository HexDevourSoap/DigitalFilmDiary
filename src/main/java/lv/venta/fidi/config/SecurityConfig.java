package lv.venta.fidi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lv.venta.fidi.service.impl.AppUserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService loadMyUserDetailsManager() {
        return new AppUserDetailsServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider loadDaoAuthProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(loadMyUserDetailsManager());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain configureUrlsSecurity(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(loadDaoAuthProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").authenticated()
                .anyRequest().permitAll()
            )
            .formLogin(auth -> auth.permitAll())
            .csrf(auth -> auth.disable());

        return http.build();
    }
}