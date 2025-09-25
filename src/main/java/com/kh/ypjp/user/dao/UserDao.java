package com.kh.ypjp.user.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.model.dto.AllergyDto.AllergyList;
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
    
    public List<AllergyList> getAllergyList() {
        return session.selectList("util.getAllergyList");
    }

    public List<Long> getUserAllergies(Long userNo) {
        return session.selectList("user.getUserAllergies", userNo);
    }

    public int insertUserAllergy(Long userNo, Long allergyNo) {
        return session.insert("user.insertUserAllergy",
                Map.of("userNo", userNo, "allergyNo", allergyNo));
    }

    public int deleteUserAllergy(Long userNo, Long allergyNo) {
        return session.delete("user.deleteUserAllergy",
                Map.of("userNo", userNo, "allergyNo", allergyNo));
    }
    
    public int inactiveUser(Long userNo) {
    	return session.update("user.inactiveUser", userNo);
    }
	
    public Optional<User> getUserByUserNo(Long userNo) {
        User user = session.selectOne("user.getUserByUserNo", userNo);
        return Optional.ofNullable(user);
    }
    
    public List<Map<String, Object>> getUserRecipes(Long userNo) {
        return session.selectList("user.getUserRecipes", userNo);
    }

    public List<Map<String, Object>> getUserLikedRecipes(Long userNo) {
        return session.selectList("user.getUserLikedRecipes", userNo);
    }
    
    public List<Map<String, Object>> getUserRecipesPaging(Long userNo, int startRow, int endRow) {
        return session.selectList("user.getUserRecipesPaging",
                Map.of("userNo", userNo, "startRow", startRow, "endRow", endRow));
    }

    public List<Map<String, Object>> getUserLikedRecipesPaging(Long userNo, int startRow, int endRow) {
        return session.selectList("user.getUserLikedRecipesPaging",
                Map.of("userNo", userNo, "startRow", startRow, "endRow", endRow));
    }
}
