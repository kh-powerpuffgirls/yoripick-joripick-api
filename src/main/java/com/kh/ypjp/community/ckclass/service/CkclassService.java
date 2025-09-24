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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CkclassService {

    private final CkclassDao ckclassDao;
    private final UtilService utilService;

    public List<CkclassDto> findMyClasses(int userNo) {
        return ckclassDao.selectMyClasses(userNo);
    }

    public List<CkclassDto> findJoinedClasses(int userNo) {
        return ckclassDao.selectJoinedClasses(userNo);
    }

    public CkclassDto findById(int roomNo) {
        return ckclassDao.selectById(roomNo);
    }

    @Transactional
    public void saveClass(CkclassDto dto, MultipartFile file) throws IOException {
        // 패스코드 유효성 검사 (null 또는 빈 문자열 허용)
        if (dto.getPasscode() != null && !dto.getPasscode().isEmpty() && !dto.getPasscode().matches("\\d{4}")) {
            throw new IllegalArgumentException("패스코드는 4자리 숫자여야 합니다.");
        }

        // 빈 문자열이면 null로 처리
        if (dto.getPasscode() != null && dto.getPasscode().isEmpty()) {
            dto.setPasscode(null);
        }

        Integer imageNo = null;

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

        if (imageNo != null) {
            dto.setImageNo(imageNo);
        }

        ckclassDao.insertClass(dto);

        Map<String, Object> enrollParams = new HashMap<>();
        enrollParams.put("roomNo", dto.getRoomNo());
        enrollParams.put("userNo", dto.getUserNo());
        ckclassDao.enrollCreator(enrollParams);
    }

    @Transactional
    public int updateClass(CkclassDto dto, MultipartFile file, int userNo, boolean isAdmin) throws IOException {
        // 패스코드 유효성 검사 (null 또는 빈 문자열 허용)
        if (dto.getPasscode() != null && !dto.getPasscode().isEmpty() && !dto.getPasscode().matches("\\d{4}")) {
            throw new IllegalArgumentException("패스코드는 4자리 숫자여야 합니다.");
        }

        // 빈 문자열이면 null로 처리
        if (dto.getPasscode() != null && dto.getPasscode().isEmpty()) {
            dto.setPasscode(null);
        }

        CkclassDto originalDto = ckclassDao.selectById(dto.getRoomNo());

        if (originalDto == null || (originalDto.getUserNo() != userNo && !isAdmin)) {
            return 0;
        }

        if (file != null && !file.isEmpty()) {
            Integer oldImageNo = ckclassDao.selectImageNoByRoomNo(dto.getRoomNo());

            String webPath = "ckclass/" + dto.getUserNo();
            String changeName = utilService.getChangeName(file, webPath);
            String serverName = webPath + "/" + changeName;

            Map<String, Object> param = new HashMap<>();
            param.put("serverName", serverName);
            param.put("originName", file.getOriginalFilename());

            utilService.insertImage(param);
            Integer newImageNo = ((Long) utilService.getImageNo(param)).intValue();

            ckclassDao.updateClassImageNo(dto.getRoomNo(), newImageNo);

            if (oldImageNo != null) {
                ckclassDao.deleteImageByImageNo(oldImageNo);
            }
        }

        return ckclassDao.updateClass(dto);
    }

    @Transactional
    public boolean deleteClass(int roomNo, int userNo) {
        CkclassDto dto = ckclassDao.selectById(roomNo);
        if (dto == null || dto.getUserNo() != userNo) {
            return false;
        }

        // [추가] 클래스 삭제 전 모든 참여자 삭제
        ckclassDao.deleteMembersByRoomNo(roomNo);

        int result = ckclassDao.deleteClass(roomNo);
        return result > 0;
    }

    public void markMessagesRead(int roomNo, int userNo) {
        ckclassDao.markMessagesRead(roomNo, userNo);
    }

    @Transactional
    public CkclassDto toggleNotification(int roomNo, int userNo) {
        ckclassDao.toggleNotification(roomNo, userNo);
        return ckclassDao.selectById(roomNo); // DB에서 업데이트된 객체를 다시 조회하여 반환
    }

    public List<CkclassDto> findAllClasses(boolean excludeCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("excludeCode", excludeCode);
        return ckclassDao.selectAllClasses(paramMap);
    }

    public List<CkclassDto> searchClasses(String searchType, String keyword, boolean excludeCode, Integer userNo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("excludeCode", excludeCode);
        paramMap.put("userNo", userNo);

        if ((keyword == null || keyword.isBlank()) &&
            (searchType == null || "all".equals(searchType))) {
            return ckclassDao.selectAllClasses(paramMap);
        }

        if ("all".equals(searchType)) {
            paramMap.put("keyword", keyword);
            return ckclassDao.searchAllFields(paramMap);
        }

        paramMap.put("searchType", searchType);
        paramMap.put("keyword", keyword);
        return ckclassDao.selectClassesForSearch(paramMap);
    }

    public List<CkclassDto> findMembersByClass(int roomNo) {
        return ckclassDao.selectMembersByRoomNo(roomNo)
                .stream()
                .map(user -> {
                    CkclassDto dto = new CkclassDto();
                    dto.setUserNo(user.getUserNo());
                    dto.setUsername(user.getUsername());
                    dto.setServerName(user.getServerName());
                    if (user.getServerName() != null) {
                        dto.setImageUrl("http://localhost:8081/images/" + user.getServerName());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void enrollUser(int roomNo, int userNo) {
        // 1. 이미 가입되어 있는지 확인
        Integer isEnrolled = ckclassDao.checkEnrollment(roomNo, userNo);
        if (isEnrolled != null) {
            throw new RuntimeException("이미 클래스에 가입되어 있습니다.");
        }

        // 2. DAO를 위한 파라미터 준비
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("roomNo", roomNo);
        paramMap.put("userNo", userNo); 

        // 3. 사용자 가입 처리
        ckclassDao.enrollUser(paramMap);
    }

    // ✅ 안 읽은 메시지 수를 업데이트하는 메서드
    public void markMessagesAsRead(int roomNo, int userNo) {
        ckclassDao.markMessagesRead(roomNo, userNo);
    }
    
    public boolean kickMember(int roomNo, int targetUserNo, int requesterUserNo) {
        // 방장인지 확인
        int ownerCheck = ckclassDao.isOwner(roomNo, requesterUserNo);
        if (ownerCheck == 0) {
            return false; // 방장이 아님 → 권한 없음
        }

        // 강퇴 실행
        int result = ckclassDao.kickMember(roomNo, targetUserNo);
        return result > 0;
    }

}