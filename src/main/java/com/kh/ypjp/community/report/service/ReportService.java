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
}
