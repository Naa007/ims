package com.stepup.ims.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.stepup.ims.entity.Inspection;
import com.stepup.ims.model.BusinessStats;
import com.stepup.ims.model.IndividualStats;
import com.stepup.ims.model.InpsectionStatsByRole;
import com.stepup.ims.model.PerformanceTrendResponse;
import com.stepup.ims.repository.InspectionRepository;
import com.stepup.ims.repository.StatsRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final StatsRepository statsRepository;

    @Autowired
    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }
    
    @Autowired
    private InspectionRepository inspectionRepository;

    public Map<String, Object> getBusinessStats() {
        BusinessStats stats = statsRepository.getBusinessStats();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("Employee Stats", createEmployeeStats(stats));
        result.put("Client Stats", createClientStats(stats));
        result.put("Inspector Stats", createInspectorStats(stats));
        result.put("Inspection Stats", createInspectionStats(stats));
        result.put("Inspection Status Stats", createInspectionStatusStats(stats));

        return result;
    }

    private Map<String, Object> createEmployeeStats(BusinessStats stats) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Total Employees", stats.getTotalEmployees());
        map.put("Active Employees", stats.getActiveEmployees());
        map.put("Coordinators", stats.getCoordinators());
        map.put("Technical Coordinators", stats.getTechnicalCoordinators());
        return map;
    }

    private Map<String, Object> createClientStats(BusinessStats stats) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Total Clients", stats.getTotalClients());
        map.put("Recently Onboarded", stats.getRecentlyOnboardedClients());
        return map;
    }

    private Map<String, Object> createInspectorStats(BusinessStats stats) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Total Inspectors", stats.getTotalInspectors());
        map.put("Active Inspectors", stats.getActiveInspectors());
        map.put("Inactive Inspectors", stats.getInactiveInspectors());
        map.put("Inhouse Inspectors", stats.getInHouseInspectors());
        map.put("Partner Inspectors", stats.getPartnerInspectors());
        map.put("Freelancer Inspectors", stats.getFreelancerInspectors());
        return map;
    }

    private Map<String, Object> createInspectionStats(BusinessStats stats) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Total Inspections", stats.getTotalInspections());
        map.put("New", stats.getNewInspections());

        // Calculate ongoing inspections by summing all intermediate statuses
        long ongoing = stats.getInspectorAssigned()
                + stats.getInspectorReviewAwaiting()
                + stats.getInspectorReviewCompleted()
                + stats.getInspectorApproved()
                + stats.getReferenceDocReceived()
                + stats.getReferenceDocReviewAwaiting()
                + stats.getReferenceDocReviewCompleted()
                + stats.getInspectionReportsReceived()
                + stats.getInspectionReportsReviewAwaiting()
                + stats.getInspectionReportsReviewCompleted()
                + stats.getInspectionReportsSentToClient();
        map.put("Ongoing", ongoing);

        map.put("Awarded", stats.getInspectionAwarded());
        map.put("Rejected", stats.getInspectionRejected());
        map.put("Closed", stats.getClosedInspections());
        return map;
    }
    private Map<String, Object> createInspectionStatusStats(BusinessStats stats) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("New", stats.getNewInspections());
        map.put("Inspector Assigned", stats.getInspectorAssigned());
        map.put("Inspector Approved", stats.getInspectorApproved());
        map.put("Inspection Reports Received", stats.getInspectionReportsReceived());
        map.put("Inspection Reports Sent to Client", stats.getInspectionReportsSentToClient());
        map.put("Inspection Awarded", stats.getInspectionAwarded());
        map.put("Inspection Rejected", stats.getInspectionRejected());
        return map;
    }

    public InpsectionStatsByRole getCoordinatorStats(String email, String period) {
        List<Inspection> inspections = inspectionRepository.findByCreatedBy(email);
        return getInspectionStatsByRole(inspections, period);
    }

    public InpsectionStatsByRole getInspectorStats(String email, String period) {
        List<Inspection> inspections = inspectionRepository.findByProposedCVs_Inspector_Email(email);
        return getInspectionStatsByRole(inspections, period);
    }

    public InpsectionStatsByRole getTechnicalCoordinatorStats(String empId, String period) {
        List<Inspection> inspections = inspectionRepository.findByProposedCVs_CvReviewBytechnicalCoordinator_EmpIdOrDocumentsReviewedByTechnicalCoordinatorOrInspectionReviewedBy(empId, empId, empId);

        return getInspectionStatsByRole(inspections, period);
    }


    public InpsectionStatsByRole getInspectionStatsByRole( List<Inspection> inspections, String period) {

        inspections = inspections.stream()
                .filter(inspection -> inspection.getCreatedDate() != null)
                .filter(inspection -> {
                    if ("WEEK".equalsIgnoreCase(period)) {
                        return !inspection.getCreatedDate().isBefore(LocalDateTime.now().minusWeeks(1));
                    } else if ("MONTH".equalsIgnoreCase(period)) {
                        return !inspection.getCreatedDate().isBefore(LocalDateTime.now().minusMonths(1));
                    } else if ("YEAR".equalsIgnoreCase(period)) {
                        return !inspection.getCreatedDate().isBefore(LocalDateTime.now().minusYears(1));
                    }
                    return true; // Default case for any other PeriodType
                })
                .toList();

        long totalInspections = inspections.size();
        long newInspections = inspections.stream().filter(inspection -> "NEW".equalsIgnoreCase(inspection.getInspectionStatus().toString())).count();
        long completedInspections = inspections.stream().filter(inspection -> "INSPECTION_AWARDED".equalsIgnoreCase(inspection.getInspectionStatus().toString())).count();
        long ongoingInspections = inspections.stream()
                .filter(inspection -> {
                    String status = inspection.getInspectionStatus().toString();
                    return "INSPECTOR_ASSIGNED".equalsIgnoreCase(status) ||
                            "INSPECTOR_REVIEW_AWAITING".equalsIgnoreCase(status) ||
                            "INSPECTOR_REVIEW_COMPLETED".equalsIgnoreCase(status) ||
                            "INSPECTOR_APPROVED".equalsIgnoreCase(status) ||
                            "REFERENCE_DOC_RECEIVED".equalsIgnoreCase(status) ||
                            "REFERENCE_DOC_REVIEW_AWAITING".equalsIgnoreCase(status) ||
                            "REFERENCE_DOC_REVIEW_COMPLETED".equalsIgnoreCase(status) ||
                            "INSPECTION_REPORTS_RECEIVED".equalsIgnoreCase(status) ||
                            "INSPECTION_REPORTS_REVIEW_AWAITING".equalsIgnoreCase(status) ||
                            "INSPECTION_REPORTS_REVIEW_COMPLETED".equalsIgnoreCase(status) ||
                            "INSPECTION_REPORTS_SENT_TO_CLIENT".equalsIgnoreCase(status);
                })
                .count();
        long rejectedInspections = inspections.stream().filter(inspection -> "INSPECTION_REJECTED".equalsIgnoreCase(inspection.getInspectionStatus().toString())).count();


        return new InpsectionStatsByRole(totalInspections, newInspections, completedInspections, ongoingInspections, rejectedInspections, InpsectionStatsByRole.PeriodType.valueOf(period.toUpperCase()));
    }
    public byte[] generateCoordinatorReport(String email, String period, String format) {
        return generateReport(email, period, format, "coordinator");
    }

    public byte[] generateTechCoordinatorReport(String empId, String period, String format) {
        return generateReport(empId, period, format, "technical");
    }

    public byte[] generateInspectorReport(String email, String period, String format) {
        return generateReport(email, period, format, "inspector");
    }

    private Map<String, Integer> getStatsByEmailAndPeriod(String identifier, String role, String period) {
        List<Inspection> inspections;

        switch(role.toLowerCase()) {
            case "coordinator":
                inspections = inspectionRepository.findByCreatedBy(identifier);
                break;
            case "technical":
                inspections = inspectionRepository.findByProposedCVs_CvReviewBytechnicalCoordinator_EmpIdOrDocumentsReviewedByTechnicalCoordinatorOrInspectionReviewedBy(
                        identifier, identifier, identifier);
                break;
            case "inspector":
                inspections = inspectionRepository.findByProposedCVs_Inspector_Email(identifier);
                break;
            default:
                inspections = new ArrayList<>();
        }

        LocalDate now = LocalDate.now();
        LocalDate startDate;
        switch (period.toLowerCase()) {
            case "week": startDate = now.minusDays(7); break;
            case "month": startDate = now.minusDays(30); break;
            case "quarter": startDate = now.minusMonths(3); break;
            case "year": startDate = now.withDayOfYear(1); break;
            default: startDate = now.minusDays(30); break;
        }

        return inspections.stream()
                .filter(i -> i.getOrderConfirmationDate() != null &&
                        !i.getOrderConfirmationDate().isBefore(startDate))
                .collect(Collectors.groupingBy(
                        i -> i.getInspectionStatus().name(),
                        Collectors.summingInt(i -> 1)
                ));
    }

    public byte[] generateReport(String id, String period, String format, String role) {
        Map<String, Integer> stats = getStatsByEmailAndPeriod(id, role, period);

        String title;
        String label;
        String sheetName;

        switch (role.toLowerCase()) {
            case "coordinator":
                title = "Coordinator Performance Report";
                label = "Coordinator email: ";
                sheetName = "Coordinator Report";
                break;
            case "technical":
                title = "Technical Coordinator Performance Report";
                label = "Technical Coordinator ID: ";
                sheetName = "Technical Coordinator Report";
                break;
            case "inspector":
                title = "Inspector Performance Report";
                label = "Inspector email: ";
                sheetName = "Inspector Report";
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }

        return "pdf".equalsIgnoreCase(format)
                ? generatePdfReport(title, label, id, period, stats)
                : generateExcelReport(title, label, id, period, stats, sheetName);
    }

    private byte[] generatePdfReport(String title, String label, String id, String period, Map<String, Integer> stats) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph(title).setBold().setFontSize(18));
        document.add(new Paragraph(label + id));
        document.add(new Paragraph("Period: " + period));
        document.add(new Paragraph("\n"));

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.addHeaderCell("Inspection Status");
        table.addHeaderCell("Count");

        stats.forEach((status, count) -> {
            String safeStatus = status != null ? status : "N/A";
            String safeCount = count != null ? count.toString() : "0";
            table.addCell(new Cell().add(new Paragraph(safeStatus)));
            table.addCell(new Cell().add(new Paragraph(safeCount)));
        });

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    private byte[] generateExcelReport(String title, String label, String id, String period, Map<String, Integer> stats, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue(title);

            sheet.createRow(1).createCell(0).setCellValue(label + id);
            sheet.createRow(2).createCell(0).setCellValue("Period: " + period);

            Row titleRow = sheet.createRow(4);
            titleRow.createCell(0).setCellValue("Inspection Status");
            titleRow.createCell(1).setCellValue("Count");

            int rowIdx = 5;
            for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                String status = entry.getKey() != null ? entry.getKey() : "N/A";
                int count = entry.getValue() != null ? entry.getValue() : 0;

                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(status);
                row.createCell(1).setCellValue(count);
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel", e);
        }
    }

    public PerformanceTrendResponse getPerformanceTrendData(String coordinator, String technical, String inspector) {
        List<String> labels = List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

        List<PerformanceTrendResponse.Dataset> datasets = new ArrayList<>();

        // ✅ Total Trend - ALL Inspections
        List<Inspection> allInspections = inspectionRepository.findAll(); // or filter by year if needed
        datasets.add(new PerformanceTrendResponse.Dataset(
                "All Inspections",
                groupByMonth(allInspections)
        ));

        if (coordinator != null && !coordinator.isBlank()) {
            List<Inspection> inspections = inspectionRepository.findByCreatedBy(coordinator);
            datasets.add(new PerformanceTrendResponse.Dataset(
                    "Coordinator - " + coordinator,
                    groupByMonth(inspections)
            ));
        }

        if (technical != null && !technical.isBlank()) {
            List<Inspection> inspections = inspectionRepository
                    .findByProposedCVs_CvReviewBytechnicalCoordinator_EmpIdOrDocumentsReviewedByTechnicalCoordinatorOrInspectionReviewedBy(
                            technical, technical, technical
                    );
            datasets.add(new PerformanceTrendResponse.Dataset(
                    "Technical - " + technical,
                    groupByMonth(inspections)
            ));
        }

        if (inspector != null && !inspector.isBlank()) {
            List<Inspection> inspections = inspectionRepository.findByProposedCVs_Inspector_Email(inspector);
            datasets.add(new PerformanceTrendResponse.Dataset(
                    "Inspector - " + inspector,
                    groupByMonth(inspections)
            ));
        }

        return new PerformanceTrendResponse(labels, datasets);
    }


    private List<Integer> groupByMonth(List<Inspection> inspections) {
        Map<Integer, Long> grouped = inspections.stream()
                .filter(i -> i.getCreatedDate() != null)
                .collect(Collectors.groupingBy(
                        i -> i.getCreatedDate().getMonthValue(),
                        Collectors.counting()
                ));

        List<Integer> counts = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            counts.add(grouped.getOrDefault(i, 0L).intValue());
        }
        return counts;
    }
    public Map<String, Object> getInspections(String email) {
        IndividualStats stats = statsRepository.getIndividualStats(email);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("Total Inspections", stats.getTotalInspections());
        result.put("New", stats.getNewInspections());
        result.put("Ongoing", calculateOngoingInspections(stats));
        result.put("Awarded", stats.getAwardedInspections());
        result.put("Rejected", stats.getRejectedInspections());
        result.put("Closed", stats.getClosedInspections());

        return result;
    }

    private long calculateOngoingInspections(IndividualStats stats) {
        return stats.getInspectorAssigned() +
                stats.getInspectorReviewAwaiting() +
                stats.getInspectorReviewCompleted() +
                stats.getInspectorApproved() +
                stats.getReferenceDocReceived() +
                stats.getReferenceDocReviewAwaiting() +
                stats.getReferenceDocReviewCompleted() +
                stats.getInspectionReportsReceived() +
                stats.getInspectionReportsReviewAwaiting() +
                stats.getInspectionReportsReviewCompleted() +
                stats.getInspectionReportsSentToClient();
    }
}