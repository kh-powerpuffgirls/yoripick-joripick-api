package com.kh.ypjp.community.report.service;

import com.kh.ypjp.community.report.dao.ReportDao;
import com.kh.ypjp.community.report.dto.ReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportDao reportDao;

    @Transactional
    public void submitReport(ReportDto reportDto) {
        int result = reportDao.insertReport(reportDto);
        if (result == 0) {
            throw new RuntimeException("신고 등록에 실패했습니다.");
        }
    }
    
    @Transactional(readOnly = true)
    public List<ReportDto> getAllReportTypes() {
        return reportDao.selectAllReportTypes();
    }
    
    @Transactional(readOnly = true)
    public ReportDto getReportTargetProfile(int reportedUserNo) {

        // 1. DAO를 통해 DB 파일명 조회
        ReportDto reportInfo = reportDao.selectReportTargetProfile(reportedUserNo); 

        if (reportInfo == null) {
            // 사용자가 DB에 없는 경우 처리
            reportInfo = new ReportDto();
            reportInfo.setReportedUserNickname("알 수 없음"); 
            reportInfo.setReportedUserProfileImageUrl("/resources/images/default-profile.png"); 
        } else {
            // 2. 파일명 추출 (DB에서 가져온 값, 예: "abc.jpg")
            String serverName = reportInfo.getReportedUserProfileImageUrl();
            
            // 3. 파일명이 존재하는 경우에만 URL 가공 실행
            if (serverName != null && !serverName.isEmpty()) {
                // 4. 챌린지 서비스와 동일한 규칙으로 URL 생성
                String fullPath = "profile/" + reportedUserNo + "/" + serverName;
                String imageUrl = "/images/" + fullPath;
                
                // 5. DTO 필드에 최종 웹 접근 URL을 덮어쓰기
                reportInfo.setReportedUserProfileImageUrl(imageUrl); 
            } else {
                // 파일명이 NULL이거나 빈 문자열인 경우 기본 이미지 설정
                reportInfo.setReportedUserProfileImageUrl("/resources/images/default-profile.png");
            }
        }
        
        return reportInfo; // 최종 URL이 담긴 DTO 반환
    }
}