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
        FR("Flash Report"), IR("Inspection Report");
        private final String description;
    }

}
