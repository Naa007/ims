package com.stepup.ims.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Filter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "inspection_reports")
public class InspectionReports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "report_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reportDate;

    @Column(name = "inspector_name")
    private String inspectorName;

    @Column(name = "report_number")
    private String reportNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private ReportType reportType;

    @Column(name = "report_link")
    private String reportLink;

    @ManyToOne
    @JoinColumn(name = "technical_coordinator_id", referencedColumnName = "emp_id")
    @Filter(name = "technicalCoordinatorFilter", condition = "role = 'TECHNICAL_COORDINATOR'")
    @ToString.Exclude
    private Employee technicalCoordinator;

    @Column(name = "technical_coordinator_remarks")
    private String technicalCoordinatorRemarks;

    @Column(name = "sent_to_client_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime sentToClientDate;

    public enum ReportType {
        FR, IR, IRN, EXPEDITING, AUDIT, REVISED_FR, REVISED_IR, REVISED_IRN, REVISED_EXPEDITING, REVISED_AUDIT
    }

}
