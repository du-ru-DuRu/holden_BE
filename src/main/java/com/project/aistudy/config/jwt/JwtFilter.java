package com.project.aistudy.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        //로그인이나 회원가인시에는 토큰 검증을 하지 않는다.
        String requestURI = httpServletRequest.getRequestURI();
        if ("/user/kakao/callback".equals(requestURI) || "/user/kakao/token".equals(requestURI)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }


        // 요청 헤더에서 JWT 토큰을 추출합니다.
        String jwt = resolveToken(httpServletRequest);

        // JWT 토큰이 유효한지 검사합니다.
        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 토큰에서 id 값을 추출하여 로깅합니다.
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtTokenProvider.getKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            Long id = claims.get("id", Long.class);

            log.info("Security Context에 '{}' 인증 정보를 저장했습니다, id: {}", authentication.getName(), id);
        } else {
            log.info("유효한 JWT 토큰이 없습니다, uri: {}", httpServletRequest.getRequestURI());
        }

        // 다음 필터를 호출합니다.
        filterChain.doFilter(servletRequest, servletResponse);
    }

    // 요청 헤더에서 토큰 정보를 꺼내오기 위한 메소드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        log.info(bearerToken);

        // 토큰이 "Bearer "로 시작하는지 확인합니다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
