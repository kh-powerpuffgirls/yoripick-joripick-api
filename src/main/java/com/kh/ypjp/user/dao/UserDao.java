package com.kh.ypjp.user.dao;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserDao {

    private final SqlSession session;

    public int updateUserImage(Long userNo, Long imageNo) {
        return session.update("user.updateUserImage",
                java.util.Map.of("userNo", userNo, "imageNo", imageNo));
        
    }
    
    public Map<String, Object> getMypageInfo(Long userNo) {
        return session.selectOne("user.getMypageInfo", userNo);
    }
}
