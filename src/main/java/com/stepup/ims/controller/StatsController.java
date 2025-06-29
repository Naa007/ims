package com.stepup.ims.controller;

import com.stepup.ims.model.InspectionStatsByRole;
import com.stepup.ims.service.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

import static com.stepup.ims.constants.ApplicationConstants.*;


@Controller
@RequestMapping("/stats")
public class StatsController {

    private static final Logger logger = LoggerFactory.getLogger(StatsController.class);

    @Autowired
    private StatsService statsService;

    @GetMapping("/{role}-stats/{emailOrEmpId}/{period}")
    public ResponseEntity<InspectionStatsByRole> getStats(@PathVariable String role,
                                                          @PathVariable String emailOrEmpId,
                                                          @PathVariable String period,
                                                          @RequestParam String from,
                                                          @RequestParam String to) {
        logger.info("Fetching stats for role: {}, period: {}", role, period);
        logger.debug("Params => emailOrEmpId: {}, from: {}, to: {}", emailOrEmpId, from, to);

        try {
            InspectionStatsByRole stats;
            switch (role.toLowerCase()) {
                case COORDINATOR_LOWERCASE:
                    stats = statsService.getCoordinatorStats(emailOrEmpId, period, LocalDateTime.parse(from), LocalDateTime.parse(to));
                    break;
                case INSPECTOR_LOWERCASE:
                    stats = statsService.getInspectorStats(emailOrEmpId, period, LocalDateTime.parse(from), LocalDateTime.parse(to));
                    break;
                case TECHNICAL_COORDINATOR_LOWERCASE:
                    stats = statsService.getTechnicalCoordinatorStats(emailOrEmpId, period, LocalDateTime.parse(from), LocalDateTime.parse(to));
                    break;
                default:
                    logger.error("Invalid role provided: {}", role);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Failed to fetch stats for role: {}, error: {}", role, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{role}-report/{emailOrEmpId}/{period}/{format}")
    public ResponseEntity<byte[]> exportReport(@PathVariable String role,
                                               @PathVariable String emailOrEmpId,
                                               @PathVariable String period,
                                               @PathVariable String format,
                                               @RequestParam String totalInspections,
                                               @RequestParam String newInspections,
                                               @RequestParam String completedInspections,
                                               @RequestParam String ongoingInspections,
                                               @RequestParam String rejectedInspections) {
        logger.info("Generating {} report in {} format", role, format);
        logger.debug("Report params => total: {}, new: {}, completed: {}, ongoing: {}, rejected: {}",
                totalInspections, newInspections, completedInspections, ongoingInspections, rejectedInspections);

        if(Long.valueOf(totalInspections) < 1){
            logger.warn("No data to generate report for role: {}", role);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        byte[] report = statsService.generateReport(emailOrEmpId, period, format, role, Long.valueOf(totalInspections),
                Long.valueOf(newInspections),
                Long.valueOf(completedInspections),
                Long.valueOf(ongoingInspections),
                Long.valueOf(rejectedInspections));

        if (report == null) {
            logger.warn("Report generation returned null for role: {}", role);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return buildReportResponse(report, role + "-report." + format, format);
    }

    private ResponseEntity<byte[]> buildReportResponse(byte[] reportData, String filename, String format) {

        logger.debug("Building report response. Filename: {}, Format: {}", filename, format);
        HttpHeaders headers = new HttpHeaders();

        switch (format.toLowerCase()) {
            case "pdf":
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "excel":
                headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                break;
            default:
                logger.error("Unsupported format requested: {}", format);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
    }

}
