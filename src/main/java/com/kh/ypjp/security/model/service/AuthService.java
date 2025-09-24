package com.kh.ypjp.security.model.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.common.exception.AuthException;
import com.kh.ypjp.config.SecurityConfig;
import com.kh.ypjp.security.model.dao.AuthDao;
import com.kh.ypjp.security.model.dto.AuthDto.AuthResult;
import com.kh.ypjp.security.model.dto.AuthDto.Authority;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.dto.AuthDto.UserCredential;
import com.kh.ypjp.security.model.dto.AuthDto.UserIdentities;
import com.kh.ypjp.security.model.provider.JWTProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SecurityConfig securityConfig;

    private final AuthDao authDao;
    private final PasswordEncoder encoder;
    private final JWTProvider jwt;
    private final UtilService utilService;
    
    private static final int ACCESS_TOKEN_EXPIRE_MINUTES = 30;
    private static final int REFRESH_TOKEN_EXPIRE_DAYS = 7;
    
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[\\u1100-\\u11FF가-힣ㄱ-ㅎa-zA-Z0-9]+$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*]).{8,15}$");

    public void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new AuthException("INVALID_EMAIL");
        }
    }

    private int getByteLength(String str) {
        int byteLength = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if ((ch >= 0xAC00 && ch <= 0xD7A3) || // 완성형 한글
                (ch >= 0x1100 && ch <= 0x1112) || // 초성
                (ch >= 0x1161 && ch <= 0x1175) || // 중성
                (ch >= 0x11A8 && ch <= 0x11C2)) { // 종성
                byteLength += 2;
            } else {
                byteLength += 1;
            }
        }
        return byteLength;
    }

    public void validateUsername(String username) {
        if (username == null) throw new AuthException("INVALID_USERNAME");
        int byteLength = getByteLength(username);
        if (!USERNAME_PATTERN.matcher(username).matches() || byteLength < 4 || byteLength > 16) {
            throw new AuthException("INVALID_USERNAME");
        }
    }

    public void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new AuthException("INVALID_PASSWORD");
        }
    }


    public AuthResult login(String email, String password) {
        User user = authDao.findUserByEmail(email)
                .orElseThrow(() -> new AuthException("WRONG_EMAIL"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new AuthException("WRONG_PASSWORD");
        }

        if ("LOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new AuthException("ACCOUNT_LOCKED");
        }

        return issueTokens(user);
    }


    
    @Transactional
    public AuthResult enroll(String email, String username, String password) {
        validateEmail(email);
        validateUsername(username);
        validatePassword(password);

        authDao.findUserByEmail(email).ifPresent(user -> {
            throw new AuthException("EMAIL_ALREADY_EXISTS");
        });

        authDao.findUserByUsername(username).ifPresent(user -> {
            throw new AuthException("USERNAME_ALREADY_EXISTS");
        });

        String encodedPassword = encoder.encode(password);

        User user = User.builder()
                .email(email)
                .username(username)
                .build();
        authDao.insertUser(user);

        UserCredential cred = UserCredential.builder()
                .userNo(user.getUserNo())
                .password(encodedPassword)
                .build();
        authDao.insertCred(cred);

        Authority role = Authority.builder()
                .userNo(user.getUserNo())
                .roles(List.of("ROLE_USER"))
                .build();
        authDao.insertUserRole(role);


        User savedUser = authDao.findUserByUserNo(user.getUserNo())
                .orElseThrow(() -> new IllegalStateException("회원가입 직후 사용자 조회 실패"));

        String accessToken = jwt.createAccessToken(savedUser.getUserNo(), user.getProvider(), ACCESS_TOKEN_EXPIRE_MINUTES);
        String refreshToken = jwt.createRefreshToken(savedUser.getUserNo(), user.getProvider(), REFRESH_TOKEN_EXPIRE_DAYS);

        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(savedUser)
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

        String newAccessToken = jwt.createAccessToken(user.getUserNo(), user.getProvider(), 30);

        return AuthResult.builder()
                .accessToken(newAccessToken)
                .user(user)
                .build();
    }
    
    @Transactional
    public AuthResult enrollSocial(String email, String username, String provider,
                                   String providerUserId) {
        validateEmail(email);
        validateUsername(username);

        authDao.findUserByEmail(email).ifPresent(u -> {
            throw new AuthException("EMAIL_ALREADY_EXISTS");
        });
        authDao.findUserByUsername(username).ifPresent(u -> {
            throw new AuthException("USERNAME_ALREADY_EXISTS");
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