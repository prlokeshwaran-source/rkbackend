package com.rksolutions.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberReportDTO {
    private Long totalMembers;
    private Long activeMembers;
    private Long pendingMembers;
    private List<MemberReportItem> members;
}
