package com.example.twistle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig{

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/register", "/register-error",
                                "/play",
                                "/level2", "/level3", "/level4", "/level5",
                                "/level6", "/level7", "/level8",
                                "/word-menu", "/word-guess/**", "/word/**",
                                "/unlockedLevels", "/getSavedState/**",
                                "/actuator/health",
                                "/style.css", "/*.css", "/*.js",
                                "/audio/**", "/fonts/**",
                                "/favicon.ico", "/index.html"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .build();
    }
}
