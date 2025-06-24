package com.stepup.ims.model;

import com.stepup.ims.entity.Inspector;
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
    @ToString.Exclude
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
