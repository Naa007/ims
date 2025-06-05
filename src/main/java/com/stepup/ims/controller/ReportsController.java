package com.stepup.ims.controller;

import com.stepup.ims.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    @Autowired
    ReportsService reportsService;


    @GetMapping("/inspections/{period}/{from}/{to}/{format}")
    public ResponseEntity<byte[]> getInspectionReports(@PathVariable String period,
                                                       @PathVariable String from,
                                                       @PathVariable String to,
                                                       @PathVariable String format,
                                                       @RequestParam(required = false) String client,
                                                       Model model) throws IllegalAccessException {

        byte[] report = reportsService.generateReport(client, period, from, to, format);
        return buildReportResponse(report, period + "-report." + format, format);
    }

    @GetMapping("/iso/{isoType}/{period}/{from}/{to}/{format}")
    public ResponseEntity<byte[]> getISOReports(@PathVariable String isoType,
                                                @PathVariable String period,
                                                @PathVariable String from,
                                                @PathVariable String to,
                                                @PathVariable String format,
                                                Model model) throws IllegalAccessException {

        byte[] report = reportsService.generateISOReport(isoType, period, from, to, format);
        return buildReportResponse(report, period + "-report." + format, format);
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

    @GetMapping("/inspectors")
    public ResponseEntity<byte[]> getInspectorsReports(@RequestParam(required = false) String country,
                                                       @RequestParam(required = false) String skill,
                                                       Model model) {

        byte[] report = reportsService.generateInspectorsReport();
        return buildReportResponse(report, "inspectors_report" + ".xlsx", "excel");
    }

}
