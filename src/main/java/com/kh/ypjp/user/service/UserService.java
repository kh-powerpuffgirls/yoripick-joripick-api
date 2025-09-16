package com.kh.ypjp.user.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.user.dao.UserDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UtilService utilService;
    private final UserDao userDao;

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

    public Map<String, Object> getMypageInfo(Long userNo) {
        return userDao.getMypageInfo(userNo);
    }
}