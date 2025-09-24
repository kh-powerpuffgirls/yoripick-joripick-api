package com.kh.ypjp.community.report.controller;

import com.kh.ypjp.community.report.dto.ReportDto;
import com.kh.ypjp.community.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<String> submitReport(
            @RequestBody ReportDto reportDto,
            @AuthenticationPrincipal Long userNo) {

        if (userNo == null) {
            return new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }

        try {
            reportDto.setUserNo(userNo.intValue());
            reportService.submitReport(reportDto);
            return new ResponseEntity<>("신고가 성공적으로 접수되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("신고 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/types")
    public ResponseEntity<List<ReportDto>> getReportTypes() {
        try {
            List<ReportDto> types = reportService.getAllReportTypes();
            return new ResponseEntity<>(types, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
