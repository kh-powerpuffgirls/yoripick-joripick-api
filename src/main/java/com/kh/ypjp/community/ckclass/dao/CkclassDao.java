package com.kh.ypjp.community.ckclass.dao;

import com.kh.ypjp.community.ckclass.dto.CkclassDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository // 스프링 빈으로 등록하기 위해 추가
@Mapper // MyBatis 매퍼임을 명시
public interface CkclassDao {
    // 사용자가 개설한 클래스 목록 조회
    List<CkclassDto> selectMyClasses(int userNo);

    // 사용자가 참여 중인 클래스 목록 조회
    List<CkclassDto> selectJoinedClasses(int userNo);

    // 특정 클래스 상세 정보 조회
    CkclassDto selectById(int roomNo);

    // 새로운 클래스 생성
    int insertClass(CkclassDto dto);

    // 클래스 정보 수정
    int updateClass(CkclassDto dto);

    // 클래스 논리적 삭제
    int deleteClass(int roomNo);

    // ================== 이미지 관련 메서드 추가 ==================

    /**
     * 이미지 정보를 DB에 저장합니다. (FreeDao 참고)
     * @param imageInfo 이미지 정보 맵 (serverName, originName)
     * @return 성공 여부 (영향을 받은 행의 수)
     */
    int insertImage(Map<String, Object> imageInfo);

    /**
     * 클래스에 이미지 번호를 업데이트합니다. (FreeDao 참고)
     * @param roomNo 클래스 번호
     * @param imageNo 이미지 번호 (이미지 없을 시 null)
     * @return 성공 여부
     */
    int updateClassImageNo(@Param("roomNo") int roomNo, @Param("imageNo") Integer imageNo);

    /**
     * 특정 클래스의 이미지 번호를 조회합니다. (FreeDao 참고)
     * @param roomNo 클래스 번호
     * @return 이미지 번호 (없으면 null)
     */
    Integer selectImageNoByRoomNo(int roomNo);

    /**
     * 이미지 메타데이터를 DB에서 삭제합니다. (FreeDao 참고)
     * @param imageNo 삭제할 이미지 번호
     * @return 성공 여부
     */
    int deleteImageByImageNo(int imageNo);
    
    void markMessagesRead(@Param("roomNo") int roomNo, @Param("userNo") int userNo);

    List<CkclassDto> selectClassesForSearch(Map<String, Object> paramMap);
    
    // 전체 목록 조회
    List<CkclassDto> selectAllClasses(Map<String, Object> paramMap);

    // 클래스명 + 방장명 전체 검색
    List<CkclassDto> searchAllFields(Map<String, Object> paramMap);


}