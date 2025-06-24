package com.stepup.ims.model;

import com.stepup.ims.entity.Inspector;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class InspectionReports {
    private Long id;
    private LocalDateTime reportDate;
    private Inspector inspector;
    private String reportNumber;
    private ReportType reportType;
    private String reportLink;

    @Getter
    @AllArgsConstructor
    public enum ReportType {
        FR("Flash Report"), IR("Inspection Report");
        private final String description;
    }

}
