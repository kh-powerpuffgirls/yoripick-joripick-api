package com.kh.ypjp.security.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.ypjp.config.SecurityConfig;
import com.kh.ypjp.security.model.dao.AuthDao;
import com.kh.ypjp.security.model.dto.AuthDto.AuthResult;
import com.kh.ypjp.security.model.dto.AuthDto.Authority;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.dto.AuthDto.UserCredential;
import com.kh.ypjp.security.model.dto.AuthDto.UserIdentities;
import com.kh.ypjp.security.model.dto.UserNotiDto;
import com.kh.ypjp.security.model.provider.JWTProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SecurityConfig securityConfig;

    private final AuthDao authDao;
    private final PasswordEncoder encoder;
    private final JWTProvider jwt;
    
    private static final int ACCESS_TOKEN_EXPIRE_MINUTES = 30;
    private static final int REFRESH_TOKEN_EXPIRE_DAYS = 7;
    
    public UserNotiDto getNotiByUserNo(String userNo) {
    	return authDao.getNotiByUserNo(userNo);
    }

    public AuthResult login(String email, String password) {
        User user = authDao.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("이메일이 잘못되었습니다. 다시 확인해주세요."));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 잘못되었습니다. 다시 입력해주세요.");
        }

        if ("LOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("계정이 잠겼습니다. 문의해주세요.");
        }

        return issueTokens(user);
    }

    
    @Transactional
    public AuthResult enroll(String email, String username, String password) {
    	
        authDao.findUserByEmail(email).ifPresent(user -> {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        });
        
        authDao.findUserByUsername(username).ifPresent(user -> {
            throw new RuntimeException("이미 사용중인 닉네임입니다.");
        });
        String encodedPassword = encoder.encode(password);
        User user = User.builder()
                        .email(email)
                        .username(username)
                        .provider("local")
                        .build();
        authDao.insertUser(user); 

        UserCredential cred = UserCredential.builder()
                                            .userNo(user.getUserNo())
                                            .password(encodedPassword)
                                            .build();
        authDao.insertCred(cred);
        Authority role = Authority.builder()
                .userNo(user.getUserNo())
                .roles(List.of("ROLE_USER")) // 기본 권한 지정
                .build();
        authDao.insertUserRole(role);
        User savedUser = authDao.findUserByUserNo(user.getUserNo())
                                .orElseThrow(() -> new IllegalStateException("회원가입 직후 사용자 조회 실패"));

        String accessToken = jwt.createAccessToken(savedUser.getUserNo(),user.getProvider(), 30);
        String refreshToken = jwt.createRefreshToken(savedUser.getUserNo(),user.getProvider(), 7);

        return AuthResult.builder()
                         .accessToken(accessToken)
                         .refreshToken(refreshToken)
                         .user(user)
                         .build();
    }
    
    public AuthResult refreshByCookie(String refreshToken) {
        Long userNo;
        try {
            userNo = jwt.parseRefresh(refreshToken);
        } catch (Exception e) {
            throw new BadCredentialsException("유효하지 않은 리프레시 토큰입니다.");
        }

        User user = authDao.findUserByUserNo(userNo)
                .orElseThrow(() -> new BadCredentialsException("사용자 조회 실패"));

        String newAccessToken = jwt.createAccessToken(user.getUserNo(),user.getProvider(), 30);

        return AuthResult.builder()
                .accessToken(newAccessToken)
                .user(user)
                .build();
    }
    
    @Transactional
    public AuthResult enrollSocial(String email, String username, String provider,
                                   String providerUserId) {

        authDao.findUserByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        });
        authDao.findUserByUsername(username).ifPresent(u -> {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        });


        User user = User.builder()
                .email(email)
                .username(username)
                .provider(provider)
                .build();
        authDao.insertUser(user);


        Authority authority = Authority.builder()
                .userNo(user.getUserNo())
                .roles(List.of("ROLE_USER"))
                .build();
        authDao.insertUserRole(authority);

        String newAccessToken = jwt.createAccessToken(user.getUserNo(), provider, 30);
        String refreshToken = jwt.createRefreshToken(user.getUserNo(), provider, 7);


        UserIdentities identities = UserIdentities.builder()
                .provider(provider)
                .providerUserId(providerUserId)
                .accessToken(newAccessToken)
                .userNo(user.getUserNo())
                .build();
        authDao.insertUserIdentities(identities);

        return AuthResult.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .user(user)
                .build();
    }

    
    public Optional<User> findUserByUsername(String username) {
    	return authDao.findUserByUsername(username);
    }

    
	public String getKakaoAccessToken(Long userId) {
		return authDao.getKakaoAccessToken(userId);
	}
	
	public Optional<User> findUserByUserNo(Long userNo) {
		return authDao.findUserByUserNo(userNo);
	}
	
    public boolean resetPassword(String email, String newPassword) {
        Optional<User> userOpt = authDao.findUserByEmail(email);
        if (userOpt.isPresent()) {
            String encodedPassword = encoder.encode(newPassword);
            UserCredential credential = new UserCredential(userOpt.get().getUserNo(), encodedPassword);
            authDao.updatePassword(credential);
            return true;
        }
        return false;
    }
    
    private AuthResult issueTokens(User user) {
        String accessToken = jwt.createAccessToken(user.getUserNo(), user.getProvider(), ACCESS_TOKEN_EXPIRE_MINUTES);
        String refreshToken = jwt.createRefreshToken(user.getUserNo(), user.getProvider(), REFRESH_TOKEN_EXPIRE_DAYS);

        User safeUser = User.builder()
                .userNo(user.getUserNo())
                .email(user.getEmail())
                .username(user.getUsername())
                .imageNo(user.getImageNo())
                .roles(user.getRoles())
                .status(user.getStatus())
                .build();

        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(safeUser)
                .build();
    }
    
    public Optional<User> findUserByEmail(String email) {
        return authDao.findUserByEmail(email);
    }
	
}