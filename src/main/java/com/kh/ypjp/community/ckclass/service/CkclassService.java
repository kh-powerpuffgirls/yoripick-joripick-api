// CkclassService.java
package com.kh.ypjp.community.ckclass.service;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.community.ckclass.dao.CkclassDao;
import com.kh.ypjp.community.ckclass.dto.CkclassDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CkclassService {
    private final CkclassDao ckclassDao;
    private final UtilService utilService;

    /** 사용자가 개설한 클래스 목록 조회 */
    public List<CkclassDto> findMyClasses(int userNo) {
        return ckclassDao.selectMyClasses(userNo);
    }

    /** 사용자가 참여 중인 클래스 목록 조회 */
    public List<CkclassDto> findJoinedClasses(int userNo) {
        return ckclassDao.selectJoinedClasses(userNo);
    }

    /** 클래스 상세 정보 조회 */
    public CkclassDto findById(int roomNo) {
        return ckclassDao.selectById(roomNo);
    }

    /** 새로운 클래스 생성 및 저장 */
    @Transactional
    public void saveClass(CkclassDto dto, MultipartFile file) throws IOException {
        Integer imageNo = null;

        // 1. 파일이 있을 때만 이미지 처리
        if (file != null && !file.isEmpty()) {
            String webPath = "ckclass/" + dto.getUserNo();
            String changeName = utilService.getChangeName(file, webPath);
            String serverName = webPath + "/" + changeName;

            dto.setOriginName(file.getOriginalFilename());
            dto.setServerName(serverName);

            Map<String, Object> param = new HashMap<>();
            param.put("serverName", serverName);
            param.put("originName", file.getOriginalFilename());

            utilService.insertImage(param);
            Long newImageNo = utilService.getImageNo(param);
            if (newImageNo != null) {
                imageNo = newImageNo.intValue();
            }
        }

        // 2. 이미지 번호가 있으면 DTO에 세팅
        if (imageNo != null) {
            dto.setImageNo(imageNo);
        }

        // 3. 클래스 INSERT
        ckclassDao.insertClass(dto);
    }

    /** 클래스 정보 수정 */
    @Transactional
    public int updateClass(CkclassDto dto, MultipartFile file, int userNo) throws IOException {
        CkclassDto originalDto = ckclassDao.selectById(dto.getRoomNo());
        if (originalDto == null || originalDto.getUserNo() != userNo) {
            return 0;
        }

        Integer imageNo = null;

        // 새 파일이 있을 경우만 처리
        if (file != null && !file.isEmpty()) {
            Integer oldImageNo = ckclassDao.selectImageNoByRoomNo(dto.getRoomNo());
            if (oldImageNo != null) {
                ckclassDao.deleteImageByImageNo(oldImageNo);
            }

            String webPath = "ckclass/" + dto.getUserNo();
            String changeName = utilService.getChangeName(file, webPath);
            String serverName = webPath + "/" + changeName;

            dto.setOriginName(file.getOriginalFilename());
            dto.setServerName(serverName);

            Map<String, Object> param = new HashMap<>();
            param.put("serverName", serverName);
            param.put("originName", file.getOriginalFilename());

            utilService.insertImage(param);
            Long newImageNo = utilService.getImageNo(param);
            if (newImageNo != null) {
                imageNo = newImageNo.intValue();
                dto.setImageNo(imageNo);
            }
        }

        // 클래스 정보 UPDATE
        return ckclassDao.updateClass(dto);
    }

    /** 클래스 논리 삭제 */
    public boolean deleteClass(int roomNo, int userNo) {
        CkclassDto dto = ckclassDao.selectById(roomNo);
        if (dto == null || dto.getUserNo() != userNo) {
            return false;
        }

        int result = ckclassDao.deleteClass(roomNo);
        return result > 0;
    }

    /** 메시지 읽음 처리 */
    public void markMessagesRead(int roomNo, int userNo) {
        ckclassDao.markMessagesRead(roomNo, userNo);
    }

    /** 조건에 따라 전체 클래스 목록을 조회합니다. */
    public List<CkclassDto> findAllClasses(boolean excludeCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("excludeCode", excludeCode);
        return ckclassDao.selectAllClasses(paramMap);
    }

    /** 클래스 검색 */
    public List<CkclassDto> searchClasses(String searchType, String keyword, boolean excludeCode, Integer userNo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("excludeCode", excludeCode);
        paramMap.put("userNo", userNo);

        // 검색어 없고 searchType이 null or all → 전체 목록
        if ((keyword == null || keyword.isBlank()) &&
            (searchType == null || "all".equals(searchType))) {
            return ckclassDao.selectAllClasses(paramMap);
        }

        // searchType == "all" → OR 조건 검색
        if ("all".equals(searchType)) {
            paramMap.put("keyword", keyword);
            return ckclassDao.searchAllFields(paramMap);
        }

        // 나머지 일반 검색
        paramMap.put("searchType", searchType);
        paramMap.put("keyword", keyword);
        return ckclassDao.selectClassesForSearch(paramMap);
    }
}