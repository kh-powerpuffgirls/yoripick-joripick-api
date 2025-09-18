package com.kh.ypjp.security.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kh.ypjp.security.model.provider.JWTProvider;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter{
   
   private final JWTProvider jwt;
   // accessToken인증 확인용 필터
   
   
   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
         throws ServletException, IOException {
	    System.out.println("JWTAuthenticationFilter - request URI: " + request.getRequestURI());
	    
	    // 현재 요청 경로를 가져옵니다.
	      String requestURI = request.getRequestURI();

	      // /auth/refresh 경로는 토큰 검증을 건너뜁니다.
	      if ("/auth/refresh".equals(requestURI)) {
	          filterChain.doFilter(request, response);
	          return; // 이 필터의 나머지 로직을 실행하지 않고 종료
	      }
	    
	    
      // 1) 요청 header에서 Authorization 추출
      String header = request.getHeader("Authorization");
      if(header != null && header.startsWith("Bearer")) {
         try {
         //2) 토큰에서 userId 추출
         String token = header.substring(7).trim();
         Long userNo = jwt.getUserNo(token);
         
         log.debug("userId : {}",userNo);
         UsernamePasswordAuthenticationToken authToken =
         new UsernamePasswordAuthenticationToken(userNo,null,
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
			// payload에 담아서 같이 보내거나
			// db에서 가져오거나 성능이 안 좋음
         );
         // 인증처리 끝
         SecurityContextHolder.getContext().setAuthentication(authToken);
         }catch(ExpiredJwtException e) {
            SecurityContextHolder.clearContext(); // 인증정보 지우기
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 401 반환
            return;
         }
      }
      filterChain.doFilter(request, response);
   }
   
}