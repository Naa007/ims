package com.stepup.ims.controller;

import com.stepup.ims.model.InpsectionStatsByRole;
import com.stepup.ims.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/coordinator-stats/{email}/{period}")
    public ResponseEntity<InpsectionStatsByRole> getCoordinatorStats(@PathVariable String email, @PathVariable String period) {
        try {
            InpsectionStatsByRole stats = statsService.getCoordinatorStats(email, period);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/inspector-stats/{email}/{period}")
    public ResponseEntity<InpsectionStatsByRole> getInspectorStats(@PathVariable String email, @PathVariable String period) {
        try {
            InpsectionStatsByRole stats = statsService.getInspectorStats(email, period);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/technical-coordinator-stats/{empId}/{period}")
    public ResponseEntity<InpsectionStatsByRole> getTechnicalCoordinatorStats(@PathVariable String empId, @PathVariable String period) {
        try {
            InpsectionStatsByRole stats = statsService.getTechnicalCoordinatorStats(empId, period);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/coordinator-report/{email}/{period}/{format}")
    public ResponseEntity<byte[]> exportCoordinatorReport(
            @PathVariable String email,
            @PathVariable String period,
            @PathVariable String format) {

        byte[] report = statsService.generateCoordinatorReport(email, period, format);
        return buildReportResponse(report, "coordinator-report." + format, format);
    }

    @GetMapping("/technical-coordinator-report/{empId}/{period}/{format}")
    public ResponseEntity<byte[]> exportTechCoordinatorReport(
            @PathVariable String empId,
            @PathVariable String period,
            @PathVariable String format) {

        byte[] report = statsService.generateTechCoordinatorReport(empId, period, format);
        return buildReportResponse(report, "TechnicalCoordinator-report." + format, format);
    }

    @GetMapping("/inspector-report/{email}/{period}/{format}")
    public ResponseEntity<byte[]> exportInspectorReport(
            @PathVariable String email,
            @PathVariable String period,
            @PathVariable String format) {

        byte[] report = statsService.generateInspectorReport(email, period, format);
        return buildReportResponse(report, "Inspector-report." + format, format);
    }

    private ResponseEntity<byte[]> buildReportResponse(
            byte[] reportData,
            String filename,
            String format) {

        HttpHeaders headers = new HttpHeaders();

        switch (format.toLowerCase()) {
            case "pdf":
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "excel":
                headers.setContentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
    }

}
