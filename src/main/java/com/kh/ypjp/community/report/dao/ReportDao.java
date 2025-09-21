package com.kh.ypjp.community.report.dao;

import com.kh.ypjp.community.report.dto.ReportDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReportDao {

    // 신고 등록
    int insertReport(ReportDto reportDto);

    // 신고 유형 전체 조회
    List<ReportDto> selectAllReportTypes();
}
