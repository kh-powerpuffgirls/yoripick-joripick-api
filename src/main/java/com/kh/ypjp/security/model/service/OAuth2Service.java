package com.kh.ypjp.security.model.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.ypjp.model.dto.CustomOAuth2User;
import com.kh.ypjp.security.model.dao.AuthDao;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.dto.AuthDto.UserIdentities;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AuthDao authDao;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "kakao"
        String providerUserId = String.valueOf(attributes.get("id"));
        String accessToken = userRequest.getAccessToken().getTokenValue();

        if (provider.equals("kakao")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            String email = (String) kakaoAccount.get("email");

            Optional<User> optionalUser = authDao.findUserByEmail(email);

            User user;

            if (optionalUser.isEmpty()) {
                return new CustomOAuth2User(
                        oAuth2User.getAuthorities(),
                        attributes,
                        "id",
                        null
                );
            } else {
                user = optionalUser.get();

                UserIdentities updateIdentity = UserIdentities.builder()
                        .provider(provider)
                        .providerUserId(providerUserId)
                        .accessToken(accessToken)
                        .userNo(user.getUserNo())
                        .build();
                authDao.updateUserIdentities(updateIdentity);

                return new CustomOAuth2User(
                        oAuth2User.getAuthorities(),
                        attributes,
                        "id",
                        user.getUserNo()
                );
            }

        }
        return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "id");
    }
}