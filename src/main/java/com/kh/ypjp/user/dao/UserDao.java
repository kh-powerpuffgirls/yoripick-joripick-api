package com.kh.ypjp.user.dao;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.security.model.dto.AuthDto.User;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserDao {

    private final SqlSession session;
    

    public int updateUserImage(Long userNo, Long imageNo) {
        return session.update("user.updateUserImage",
                java.util.Map.of("userNo", userNo, "imageNo", imageNo));
        
    }
    
    public Long getUserImageNo(Long userNo) {
    	return session.selectOne("user.getUserImageNo", userNo);
    };
    
    public int deleteImage(Long imageNo) {
    	return session.delete("user.deleteImage",imageNo);
    };
    
    public Map<String, Object> getMypageInfo(Long userNo) {
        return session.selectOne("user.getMypageInfo", userNo);
    }
    
    public int updateUser(Map<String, Object> param) {
        return session.update("user.updateUser", param);
    }

    public int updateAlarm(Map<String, Object> alarms) {
        return session.update("user.updateAlarm", alarms);
    }

    public Map<String, Object> getAlarmSettings(Long userNo) {
        return session.selectOne("user.getAlarmSettings", userNo);
    }

}
