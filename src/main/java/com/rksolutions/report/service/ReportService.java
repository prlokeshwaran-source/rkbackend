package com.rksolutions.report.service;

import com.rksolutions.report.dto.CommissionReportDTO;
import com.rksolutions.report.dto.DailyReportDTO;
import com.rksolutions.report.dto.MemberReportDTO;
import com.rksolutions.report.dto.MonthlyReportDTO;
import com.rksolutions.report.dto.PerformanceReportDTO;
import com.rksolutions.report.dto.SalesReportDTO;

import java.time.LocalDate;

public interface ReportService {

    SalesReportDTO getSalesReport(LocalDate fromDate, LocalDate toDate);

    CommissionReportDTO getCommissionReport(LocalDate fromDate, LocalDate toDate, String status);

    MemberReportDTO getMemberReport();

    PerformanceReportDTO getPerformanceReport(LocalDate fromDate, LocalDate toDate);

    DailyReportDTO getDailyReport();

    MonthlyReportDTO getMonthlyReport(Integer year, Integer month);

    byte[] exportReport(String reportType, LocalDate fromDate, LocalDate toDate);
}
