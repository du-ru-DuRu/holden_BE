package com.project.aistudy.config;

import com.project.aistudy.config.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity // 웹 보안을 활성화합니다.
@EnableMethodSecurity // 메소드 수준의 보안 활성화
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final JwtFilter jwtFilter; // JwtFilter 추가

    // 생성자를 통해 CorsFilter와 JwtFilter를 주입합니다.
    public SecurityConfig(CorsFilter corsFilter, JwtFilter jwtFilter) {
        this.corsFilter = corsFilter;
        this.jwtFilter = jwtFilter; // JwtFilter 초기화
    }

    // 비밀번호 암호화를 위한 PasswordEncoder 빈을 생성합니다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SecurityFilterChain 빈을 생성하여 HTTP 보안 설정을 정의합니다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호를 비활성화
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class) // CORS 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // JwtFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/user/kakao/token", "/user/kakao/callback", "/oauth2/**", "/login").permitAll() // 특정 URL 경로에 대해 인증을 요구하지 않습니다.
                        .anyRequest().authenticated() // 그 외 모든 요청에 대해 인증을 요구합니다.
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리를 Stateless로 설정합니다.
                .headers(headers -> headers.frameOptions(options -> options.sameOrigin())); // 동일 출처에서의 iframe 사용을 허용합니다.
        return http.build();
    }
}
