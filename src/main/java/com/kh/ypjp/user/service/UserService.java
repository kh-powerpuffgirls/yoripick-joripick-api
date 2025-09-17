package com.kh.ypjp.user.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.security.model.dao.AuthDao;
import com.kh.ypjp.security.model.dto.AuthDto;
import com.kh.ypjp.security.model.dto.AuthDto.UserCredential;
import com.kh.ypjp.security.model.handler.OAuth2FailureHandler;
import com.kh.ypjp.user.dao.UserDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final OAuth2FailureHandler OAuth2FailureHandler;
    private final UtilService utilService;
    private final UserDao userDao;
    private final AuthDao authDao;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> updateUserProfile(MultipartFile file, Long userNo) {
        Map<String, Object> result = new HashMap<>();

        String webPath = "profile/" + userNo;
        String changeName = utilService.getChangeName(file, webPath);

        Map<String, Object> param = new HashMap<>();
        param.put("originName", file.getOriginalFilename());
        param.put("serverName", changeName);

        utilService.insertImage(param);


        Long imageNo = utilService.getImageNo(param);
        if (imageNo == null) {
            throw new RuntimeException("이미지 저장 실패");
        }

        userDao.updateUserImage(userNo, imageNo);

        result.put("imageNo", imageNo);
        result.put("originName", file.getOriginalFilename());
        result.put("serverName", changeName);
        
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path("profile/"+userNo+"/"+changeName)
                .toUriString();
        
        result.put("url", imageUrl);

        return result;
    }
    
    public boolean updateUser(Map<String, String> updatePayload) {
        Long userNo = Long.valueOf(updatePayload.get("userNo"));

        int updatedUser = 0;
        int updatedPassword = 0;

        Map<String, Object> param = new HashMap<>();
        param.put("userNo", userNo);
        if (updatePayload.containsKey("username")) {
            param.put("username", updatePayload.get("username"));
        }
        if (updatePayload.containsKey("email")) {
            param.put("email", updatePayload.get("email"));
        }
        if (param.size() > 1) {
            updatedUser = userDao.updateUser(param);
        }

        if (updatePayload.containsKey("newPassword")) {
            String rawPw = updatePayload.get("newPassword");
            String encodedPw = passwordEncoder.encode(rawPw);

            UserCredential credential = new UserCredential();
            credential.setUserNo(userNo);
            credential.setPassword(encodedPw);

            authDao.updatePassword(credential);
            updatedPassword = 1;
        }

        return updatedUser > 0 || updatedPassword > 0;
    }

    public Map<String, Object> getMypageInfo(Long userNo) {
        return userDao.getMypageInfo(userNo);
    }
    
    public boolean checkPassword(Long userNo, String currentPassword) {
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return false;
        }
        
        AuthDto.User user = authDao.findUserByUserNo(userNo).orElse(null);
        
        if (user == null || user.getPassword() == null) {
            return false;
        }

        return passwordEncoder.matches(currentPassword, user.getPassword());
    }
}