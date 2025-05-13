package com.stepup.ims.controller;

import com.stepup.ims.model.InpsectionStatsByRole;
import com.stepup.ims.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/{role}/{email}/{empId}/{period}/{from}/{to}/{fileType}")
    public Map<String, Long> getStats(@PathVariable String role, @PathVariable String email, @PathVariable String empId, @PathVariable String period, @PathVariable String from, @PathVariable String to, @PathVariable String fileType) {
        return statsService.getStats(role, email, empId, period, from, to, fileType);
    }

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

    @GetMapping("/coordinator-report/{email}/{period}/pdf")
    public ResponseEntity<byte[]> exportCoordinatorPdf(@PathVariable String email, @PathVariable String period) {
        byte[] report = statsService.generateCoordinatorReport(email, period, "pdf");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("coordinator-report.pdf").build());

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

    @GetMapping("/coordinator-report/{email}/{period}/excel")
    public ResponseEntity<byte[]> exportCoordinatorExcel(@PathVariable String email, @PathVariable String period) {
        byte[] report = statsService.generateCoordinatorReport(email, period, "excel");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("coordinator-report.xlsx").build());

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

    @GetMapping("/technical-coordinator-report/{empId}/{period}/pdf")
    public ResponseEntity<byte[]> exportTechCoordinatorPdf(@PathVariable String empId, @PathVariable String period) {
        byte[] report = statsService.generateTechCoordinatorReport(empId, period, "pdf");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("TechnicalCoordinator-report.pdf").build());

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

    @GetMapping("/technical-coordinator-report/{empId}/{period}/excel")
    public ResponseEntity<byte[]> exportTechCoordinatorExcel(@PathVariable String empId, @PathVariable String period) {
        byte[] report = statsService.generateTechCoordinatorReport(empId, period, "excel");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("TechnicalCoordinator-report.xlsx").build());

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

    @GetMapping("/inspector-report/{email}/{period}/pdf")
    public ResponseEntity<byte[]> exportInspectorPdf(@PathVariable String email, @PathVariable String period) {
        byte[] report = statsService.generateInspectorReport(email, period, "pdf");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("Inspector-report.pdf").build());

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

    @GetMapping("/inspector-report/{email}/{period}/excel")
    public ResponseEntity<byte[]> exportInspectorExcel(@PathVariable String email, @PathVariable String period) {
        byte[] report = statsService.generateInspectorReport(email, period, "excel");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("Inspector-report.xlsx").build());

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }
}
