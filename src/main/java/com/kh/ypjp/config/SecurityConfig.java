package com.kh.ypjp.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kh.ypjp.security.filter.JWTAuthenticationFilter;
import com.kh.ypjp.security.model.handler.OAuth2FailureHandler;
import com.kh.ypjp.security.model.handler.OAuth2SuccessHandler;
import com.kh.ypjp.security.model.provider.JWTProvider;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2FailureHandler OAuth2FailureHandler;
    private final OAuth2SuccessHandler OAuth2SuccessHandler;
    private final JWTProvider jwtProvider;

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter(jwtProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JWTAuthenticationFilter jwtFilter) throws Exception {

        http
            // CORS 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // CSRF 비활성화 (SPA라서)
            .csrf(csrf -> csrf.disable())
            // 인증 실패 및 권한 실패 처리
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
                })
                .accessDeniedHandler((req, res, ex) -> {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN");
                })
            )
            // 세션 관리 - 무상태(stateless) 설정
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 요청별 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/oauth2/**", "/login**", "/error").permitAll()
                .requestMatchers("/api/inglist/**").authenticated()
                .anyRequest().authenticated()
            )
            // OAuth2 로그인 핸들러
            .oauth2Login(oauth2 -> oauth2
                .successHandler(OAuth2SuccessHandler)
                .failureHandler(OAuth2FailureHandler)
            );

        // 필터 순서 지정
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // CORS 설정 정보 빈
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Location", "Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}