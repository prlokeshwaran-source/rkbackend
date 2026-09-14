package com.rksolutions.report.controller;

import com.rksolutions.report.dto.CommissionReportDTO;
import com.rksolutions.report.dto.DailyReportDTO;
import com.rksolutions.report.dto.MemberReportDTO;
import com.rksolutions.report.dto.MonthlyReportDTO;
import com.rksolutions.report.dto.PerformanceReportDTO;
import com.rksolutions.report.dto.SalesReportDTO;
import com.rksolutions.report.service.ReportService;
import com.rksolutions.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SalesReportDTO>> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        SalesReportDTO report = reportService.getSalesReport(fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(report, "Sales report generated successfully"));
    }

    @GetMapping("/commission")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CommissionReportDTO>> getCommissionReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String status) {
        CommissionReportDTO report = reportService.getCommissionReport(fromDate, toDate, status);
        return ResponseEntity.ok(ApiResponse.success(report, "Commission report generated successfully"));
    }

    @GetMapping("/members")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MemberReportDTO>> getMemberReport() {
        MemberReportDTO report = reportService.getMemberReport();
        return ResponseEntity.ok(ApiResponse.success(report, "Member report generated successfully"));
    }

    @GetMapping("/performance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PerformanceReportDTO>> getPerformanceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        PerformanceReportDTO report = reportService.getPerformanceReport(fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(report, "Performance report generated successfully"));
    }

    @GetMapping("/daily")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DailyReportDTO>> getDailyReport() {
        DailyReportDTO report = reportService.getDailyReport();
        return ResponseEntity.ok(ApiResponse.success(report, "Daily report generated successfully"));
    }

    @GetMapping("/monthly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MonthlyReportDTO>> getMonthlyReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        MonthlyReportDTO report = reportService.getMonthlyReport(year, month);
        return ResponseEntity.ok(ApiResponse.success(report, "Monthly report generated successfully"));
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Resource> exportReport(
            @RequestParam String reportType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        byte[] data = reportService.exportReport(reportType, fromDate, toDate);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + reportType + "_report.csv\"")
                .body(new ByteArrayResource(data));
    }
}
