package com.kh.ypjp.security.model.handler;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.kh.ypjp.model.dto.CustomOAuth2User;
import com.kh.ypjp.security.controller.AuthController;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.provider.JWTProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler{
	
	private final JWTProvider jwt;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                    Authentication authentication) throws IOException, ServletException {
	    CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

	    Long id = oauthUser.getUserNo();

	    if (id == null) {
	        Map<String, Object> kakaoAccount = (Map<String, Object>) oauthUser.getAttributes().get("kakao_account");
	        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

	        String providerUserId = String.valueOf(oauthUser.getAttributes().get("id"));

	        String redirect = UriComponentsBuilder
	                .fromUriString("http://localhost:5173/oauth2/username")
	                .queryParam("email", email)
	                .queryParam("provider", "kakao")
	                .queryParam("providerUserId", providerUserId)
	                .build().toUriString();

	        response.sendRedirect(redirect);
	        return;
	    }
	    
	    String provider = "kakao";

	    String accessToken = jwt.createAccessToken(id, provider, 30);
	    String refreshToken = jwt.createRefreshToken(id,provider, 7);

	    ResponseCookie cookie = ResponseCookie.from(AuthController.REFRESH_COOKIE, refreshToken)
	            .httpOnly(true)
	            .secure(true)
	            .domain(".ypjp.store")
	            .sameSite("None")
	            .path("/")
	            .maxAge(Duration.ofDays(7))
	            .build();
	    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

	    String redirect = UriComponentsBuilder
	            .fromUriString("http://localhost:5173/oauth2/success")
	            .queryParam("accessToken", accessToken)
	            .build().toUriString();

	    response.sendRedirect(redirect);
	}
	
	
}
