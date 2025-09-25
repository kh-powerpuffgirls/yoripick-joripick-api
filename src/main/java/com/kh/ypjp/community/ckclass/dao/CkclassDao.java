package com.kh.ypjp.community.ckclass.dao;

import com.kh.ypjp.community.ckclass.dto.CkclassDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CkclassDao {

    List<CkclassDto> selectMyClasses(int userNo);

    List<CkclassDto> selectJoinedClasses(int userNo);

    CkclassDto selectById(int roomNo);
    
    void insertClass(CkclassDto dto);

    void enrollCreator(Map<String, Object> params);

    int updateClass(CkclassDto dto);

    int deleteClass(int roomNo);

    void deleteMembersByRoomNo(int roomNo);
    
    // void markMessagesAsRead(int roomNo, int userNo);
    
    void toggleNotification(int roomNo, int userNo);

    List<CkclassDto> selectAllClasses(Map<String, Object> paramMap);

    List<CkclassDto> selectClassesForSearch(Map<String, Object> paramMap);
    
    List<CkclassDto> selectMembersByRoomNo(int roomNo);

    void insertImage(Map<String, Object> param);

    Long getImageNo(Map<String, Object> param);
    
    void updateClassImageNo(int roomNo, int imageNo);

    Integer selectImageNoByRoomNo(int roomNo);

    void deleteImageByImageNo(int imageNo);
    
    List<CkclassDto> searchAllFields(Map<String, Object> paramMap);
    
    Integer checkEnrollment(@Param("roomNo") int roomNo, @Param("userNo") int userNo);

    void enrollUser(Map<String, Object> paramMap);
    
    int kickMember(@Param("roomNo") int roomNo, @Param("userNo") int userNo);

    int isOwner(@Param("roomNo") int roomNo, @Param("userNo") int userNo);
    
    int deleteClassMember(@Param("roomNo") int roomNo, @Param("userNo") int userNo);

}