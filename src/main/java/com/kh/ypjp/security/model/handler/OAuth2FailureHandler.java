package com.kh.ypjp.security.model.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component


public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        log.error("OAuth2 로그인 실패: {}", exception.getMessage());

        // 리디렉션할 URL 설정
        String redirectUrl = UriComponentsBuilder
                .fromUriString("https://front.ypjp.store/oauth2/failure")
                .queryParam("error", exception.getMessage())
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}