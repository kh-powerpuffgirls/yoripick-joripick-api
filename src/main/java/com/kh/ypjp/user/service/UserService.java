package com.kh.ypjp.user.service;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.user.dao.UserDao;
import lombok.RequiredArgsConstructor;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SqlSession session;
    private final UtilService utilService;

    public void updateUserProfile(MultipartFile file, Long userNo) {
        String changeName = utilService.getChangeName(file, "profile/" + userNo);

        Map<String, Object> param = new HashMap<>();
        param.put("originName", file.getOriginalFilename());
        param.put("serverName", changeName);

        session.insert("user.insertImage", param);

        Long imageNo = (Long) param.get("imageNo");
        if (imageNo == null) throw new RuntimeException("이미지 저장 실패");

        session.update("user.updateUserImage", Map.of("userNo", userNo, "imageNo", imageNo));
    }

    
    

}