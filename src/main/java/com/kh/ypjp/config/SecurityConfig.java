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
//임의추가 -> url접근때문에 
import org.springframework.http.HttpMethod;

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
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/login/**", "/oauth2/**", "/error").permitAll()
                .requestMatchers("/common/**").permitAll()
                .requestMatchers("/chat/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/community/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/mealplan/**").authenticated()
                .requestMatchers(HttpMethod.GET, 
				        "/api/community/recipe/**", 
				        "/api/options/**", 
				        "/api/ingredients/search"
				    ).permitAll()
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