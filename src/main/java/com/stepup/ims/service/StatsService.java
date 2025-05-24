package com.stepup.ims.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.stepup.ims.entity.Inspection;
import com.stepup.ims.model.BusinessStats;
import com.stepup.ims.model.InspectionStatsByRole;
import com.stepup.ims.model.PerformanceTrendResponse;
import com.stepup.ims.repository.InspectionRepository;
import com.stepup.ims.repository.StatsRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.stepup.ims.constants.ApplicationConstants.*;

@Service
public class StatsService {

    private final StatsRepository statsRepository;
    @Autowired
    private InspectionRepository inspectionRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private InspectorService inspctorService;


    @Autowired
    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

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
        long ongoing = stats.getInspectorAssigned() + stats.getInspectorReviewAwaiting() + stats.getInspectorReviewCompleted() + stats.getInspectorApproved() + stats.getReferenceDocReceived() + stats.getReferenceDocReviewAwaiting() + stats.getReferenceDocReviewCompleted() + stats.getInspectionReportsReceived() + stats.getInspectionReportsReviewAwaiting() + stats.getInspectionReportsReviewCompleted() + stats.getInspectionReportsSentToClient();
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

    public InspectionStatsByRole getCoordinatorStats(String email, String period, LocalDateTime startDate, LocalDateTime endDate) {
        List<Inspection> inspections = inspectionRepository.findByCreatedByAndCreatedDateBetween(email, startDate, endDate);
        return getInspectionStatsByRole(inspections, period);
    }

    public InspectionStatsByRole getInspectorStats(String email, String period, LocalDateTime startDate, LocalDateTime endDate) {
        List<Inspection> inspections = inspectionRepository.findByProposedCVs_Inspector_EmailAndCreatedDateBetween(email, startDate, endDate);
        return getInspectionStatsByRole(inspections, period);
    }

    public InspectionStatsByRole getTechnicalCoordinatorStats(String empId, String period, LocalDateTime startDate, LocalDateTime endDate) {
        List<Inspection> inspections = inspectionRepository.inspectionsReviewedByTechnicalCoordinatorsBetweenDates(empId, empId, empId, startDate, endDate);

        return getInspectionStatsByRole(inspections, period);
    }

    public InspectionStatsByRole getInspectionStatsByRole(List<Inspection> inspections, String period) {

        long totalInspections = inspections.size();
        long newInspections = inspections.stream().filter(inspection -> "NEW".equalsIgnoreCase(inspection.getInspectionStatus().toString())).count();
        long completedInspections = inspections.stream().filter(inspection -> "INSPECTION_AWARDED".equalsIgnoreCase(inspection.getInspectionStatus().toString())).count();
        long ongoingInspections = inspections.stream().filter(inspection -> {
            String status = inspection.getInspectionStatus().toString();
            return "INSPECTOR_ASSIGNED".equalsIgnoreCase(status) || "INSPECTOR_REVIEW_AWAITING".equalsIgnoreCase(status) || "INSPECTOR_REVIEW_COMPLETED".equalsIgnoreCase(status) || "INSPECTOR_APPROVED".equalsIgnoreCase(status) || "REFERENCE_DOC_RECEIVED".equalsIgnoreCase(status) || "REFERENCE_DOC_REVIEW_AWAITING".equalsIgnoreCase(status) || "REFERENCE_DOC_REVIEW_COMPLETED".equalsIgnoreCase(status) || "INSPECTION_REPORTS_RECEIVED".equalsIgnoreCase(status) || "INSPECTION_REPORTS_REVIEW_AWAITING".equalsIgnoreCase(status) || "INSPECTION_REPORTS_REVIEW_COMPLETED".equalsIgnoreCase(status) || "INSPECTION_REPORTS_SENT_TO_CLIENT".equalsIgnoreCase(status);
        }).count();
        long rejectedInspections = inspections.stream().filter(inspection -> "INSPECTION_REJECTED".equalsIgnoreCase(inspection.getInspectionStatus().toString())).count();


        return new InspectionStatsByRole(totalInspections, newInspections, completedInspections, ongoingInspections, rejectedInspections, InspectionStatsByRole.PeriodType.valueOf(period.toUpperCase()));
    }


    public byte[] generateReport(String id,
                                 String period,
                                 String format,
                                 String role,
                                 Long totalInspections,
                                 Long newInspections,
                                 Long completedInspections,
                                 Long ongoingInspections,
                                 Long rejectedInspections) {
        InspectionStatsByRole stats;
        String empName = getEmployeeName(id, role);
        stats = new InspectionStatsByRole(totalInspections, newInspections, completedInspections, ongoingInspections, rejectedInspections, InspectionStatsByRole.PeriodType.valueOf(period.toUpperCase()));

        String title;
        String nameLabel;
        String label;
        String sheetName;

        switch (role.toLowerCase()) {
            case COORDINATOR_LOWERCASE:
                title = "Coordinator Performance Report";
                nameLabel = "Coordinator name: ";
                label = "Coordinator email: ";
                sheetName = "Coordinator Report";
                break;
            case TECHNICAL_COORDINATOR_LOWERCASE:
                title = "Technical Coordinator Performance Report";
                nameLabel = "Technical Coordinator name: ";
                label = "Technical Coordinator ID: ";
                sheetName = "Technical Coordinator Report";
                break;
            case INSPECTOR_LOWERCASE:
                title = "Inspector Performance Report";
                nameLabel = "Inspector name: ";
                label = "Inspector email: ";
                sheetName = "Inspector Report";
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }

        return "pdf".equalsIgnoreCase(format) ?
                generatePdfReport(title, label, nameLabel, id, empName, period, stats) :
                generateExcelReport(title, label, nameLabel, id, empName, period, stats, sheetName);
    }

    private byte[] generatePdfReport(String title, String label, String nameLabel, String id, String empName, String period, InspectionStatsByRole stats) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            document.setFont(font);

            // Title and metadata
            document.add(new Paragraph(title).setBold().setFontSize(18));
            document.add(new Paragraph(nameLabel + empName));
            document.add(new Paragraph(label + id));
            document.add(new Paragraph("Period: " + period.toUpperCase()));
            document.add(new Paragraph("\n"));

            // Summary table
            Table summaryTable = new Table(new float[]{1, 1});
            summaryTable.setWidth(UnitValue.createPercentValue(100));
            summaryTable.addHeaderCell("Metric");
            summaryTable.addHeaderCell("Count");

            summaryTable.addCell(new Cell().add(new Paragraph("Total Inspections")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getTotalInspections()))));

            summaryTable.addCell(new Cell().add(new Paragraph("New Inspections")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getNewInspections()))));

            summaryTable.addCell(new Cell().add(new Paragraph("Ongoing Inspections")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getOngoingInspections()))));

            summaryTable.addCell(new Cell().add(new Paragraph("Awarded Inspections")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getCompletedInspections()))));

            summaryTable.addCell(new Cell().add(new Paragraph("Rejected Inspections")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getRejectedInspections()))));

            document.add(summaryTable);
            document.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error while generating PDF", e);
        }
    }


    private byte[] generateExcelReport(String title, String label, String nameLabel, String id, String name, String period, InspectionStatsByRole stats, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            // Title and metadata
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue(title);

            Row infoRow1 = sheet.createRow(1);
            infoRow1.createCell(0).setCellValue(label + id);

            Row infoRowName = sheet.createRow(2);
            infoRowName.createCell(0).setCellValue(nameLabel + name);

            Row infoRow2 = sheet.createRow(3);
            infoRow2.createCell(0).setCellValue("Period: " + period.toUpperCase());

            // Header row
            Row headerRow = sheet.createRow(5);
            headerRow.createCell(0).setCellValue("Metric");
            headerRow.createCell(1).setCellValue("Count");

            // Data rows
            int rowIdx = 6;
            createDataRow(sheet, rowIdx++, "Total Inspections", stats.getTotalInspections());
            createDataRow(sheet, rowIdx++, "New Inspections", stats.getNewInspections());
            createDataRow(sheet, rowIdx++, "Ongoing Inspections", stats.getOngoingInspections());
            createDataRow(sheet, rowIdx++, "Awarded Inspections", stats.getCompletedInspections());
            createDataRow(sheet, rowIdx++, "Rejected Inspections", stats.getRejectedInspections());

            // Auto-size columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel", e);
        }
    }

    private void createDataRow(Sheet sheet, int rowIdx, String label, long value) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    public PerformanceTrendResponse getPerformanceTrendData(String coordinator, String technical, String inspector) {
        List<String> labels = List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

        List<PerformanceTrendResponse.Dataset> datasets = new ArrayList<>();

        // ✅ Total Trend - ALL Inspections
        List<Inspection> allInspections = inspectionRepository.findAll(); // or filter by year if needed
        datasets.add(new PerformanceTrendResponse.Dataset("All Inspections", groupByMonth(allInspections)));

        if (coordinator != null && !coordinator.isBlank()) {
            List<Inspection> inspections = inspectionRepository.findByCreatedBy(coordinator);
            datasets.add(new PerformanceTrendResponse.Dataset("Coordinator - " + coordinator, groupByMonth(inspections)));
        }

        if (technical != null && !technical.isBlank()) {
            List<Inspection> inspections = inspectionRepository.findByProposedCVs_CvReviewBytechnicalCoordinator_EmpIdOrDocumentsReviewedByTechnicalCoordinatorOrInspectionReviewedBy(technical, technical, technical);
            datasets.add(new PerformanceTrendResponse.Dataset("Technical - " + technical, groupByMonth(inspections)));
        }

        if (inspector != null && !inspector.isBlank()) {
            List<Inspection> inspections = inspectionRepository.findByProposedCVs_Inspector_Email(inspector);
            datasets.add(new PerformanceTrendResponse.Dataset("Inspector - " + inspector, groupByMonth(inspections)));
        }

        return new PerformanceTrendResponse(labels, datasets);
    }

    private List<Integer> groupByMonth(List<Inspection> inspections) {
        Map<Integer, Long> grouped = inspections.stream().filter(i -> i.getCreatedDate() != null).collect(Collectors.groupingBy(i -> i.getCreatedDate().getMonthValue(), Collectors.counting()));

        List<Integer> counts = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            counts.add(grouped.getOrDefault(i, 0L).intValue());
        }
        return counts;
    }

    private String getEmployeeName(String id, String role) {
        try {
            return switch (role.toLowerCase()) {
                case COORDINATOR_LOWERCASE -> employeeService.getEmployeeNameByEmail(id);
                case INSPECTOR_LOWERCASE -> inspctorService.getInspectorNameByEmail(id);
                case TECHNICAL_COORDINATOR_LOWERCASE -> employeeService.getEmployeeNameByEmpId(id);
                default -> "Unknown";
            };
        } catch (NumberFormatException e) {
            return "Invalid ID format";
        } catch (Exception e) {
            return "Name not available";
        }
    }
}