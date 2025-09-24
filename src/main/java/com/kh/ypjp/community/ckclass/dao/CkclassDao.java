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

    // ✅ 클래스 삭제 시 모든 멤버를 삭제하는 메소드는 이미 존재합니다.
    void deleteMembersByRoomNo(int roomNo);

    void markMessagesRead(int roomNo, int userNo);
    
    // ✅ 이 메소드가 알림 상태를 토글합니다.
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
    
 // 멤버 강퇴 (특정 유저를 방에서 제거)
    int kickMember(@Param("roomNo") int roomNo, @Param("userNo") int userNo);

    // 방장이 맞는지 확인
    int isOwner(@Param("roomNo") int roomNo, @Param("userNo") int userNo);

}