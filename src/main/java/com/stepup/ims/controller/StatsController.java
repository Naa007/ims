package com.stepup.ims.controller;

import com.stepup.ims.model.InspectionStatsByRole;
import com.stepup.ims.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;

import static com.stepup.ims.constants.ApplicationConstants.*;


@Controller
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/{role}-stats/{emailOrEmpId}/{period}")
    public ResponseEntity<InspectionStatsByRole> getStats(@PathVariable String role, @PathVariable String emailOrEmpId, @PathVariable String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        try {
            InspectionStatsByRole stats;
            switch (role.toLowerCase()) {
                case COORDINATOR_LOWERCASE:
                    stats = statsService.getCoordinatorStats(emailOrEmpId, period, startDate, endDate);
                    break;
                case INSPECTOR_LOWERCASE:
                    stats = statsService.getInspectorStats(emailOrEmpId, period,startDate, endDate);
                    break;
                case TECHNICAL_COORDINATOR_LOWERCASE:
                    stats = statsService.getTechnicalCoordinatorStats(emailOrEmpId, period,startDate, endDate);
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{role}-report/{emailOrEmpId}/{period}/{format}")
    public ResponseEntity<byte[]> exportReport(@PathVariable String role, @PathVariable String emailOrEmpId, @PathVariable String period, @PathVariable String format,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){

        byte[] report;

        switch (role.toLowerCase()) {
            case COORDINATOR_LOWERCASE:
                report = statsService.generateCoordinatorReport(emailOrEmpId, period, format,startDate, endDate);
                break;
            case TECHNICAL_COORDINATOR_LOWERCASE:
                report = statsService.generateTechCoordinatorReport(emailOrEmpId, period, format,startDate, endDate);
                break;
            case INSPECTOR_LOWERCASE:
                report = statsService.generateInspectorReport(emailOrEmpId, period, format,startDate, endDate);
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return buildReportResponse(report, role + "-report." + format, format);
    }

    private ResponseEntity<byte[]> buildReportResponse(byte[] reportData, String filename, String format) {

        HttpHeaders headers = new HttpHeaders();

        switch (format.toLowerCase()) {
            case "pdf":
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "excel":
                headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
    }

}
