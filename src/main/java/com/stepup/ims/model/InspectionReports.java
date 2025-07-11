package com.stepup.ims.model;

import com.stepup.ims.entity.Employee;
import com.stepup.ims.entity.Inspector;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class InspectionReports {
    private Long id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reportDate;
    private String inspectorName;
    private String reportNumber;
    private ReportType reportType;
    private String reportLink;
    private Employee technicalCoordinator;
    private String technicalCoordinatorRemarks;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime sentToClientDate;

    @Getter
    @AllArgsConstructor
    public enum ReportType {
        FR("Flash Report"),
        IR("Inspection Report"),
        IRN("IRN Report"),
        EXPEDITING("Expediting Report"),
        AUDIT("Audit Report"),
        REVISED_FR("Revised Flash Report"),
        REVISED_IR("Revised Inspection Report"),
        REVISED_IRN("Revised IRN Report"),
        REVISED_EXPEDITING("Revised Expediting Report"),
        REVISED_AUDIT("Revised Audit Report");
        private final String description;
    }

}
