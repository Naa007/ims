package com.stepup.ims.controller;

import com.stepup.ims.service.AgreementService;
import com.stepup.ims.service.ReportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import static com.stepup.ims.constants.FilePathConstants.IMPARTIALITY_TEMPLATE;
import static com.stepup.ims.constants.FilePathConstants.TEMPLATE_DIR;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @Autowired
    ReportsService reportsService;
    @Autowired
    AgreementService agreementService;





    @GetMapping("/inspections/{period}/{from}/{to}/{format}")
    public ResponseEntity<byte[]> getInspectionReports(@PathVariable String period,
                                                       @PathVariable String from,
                                                       @PathVariable String to,
                                                       @PathVariable String format,
                                                       @RequestParam(required = false) String client,
                                                       Model model) throws IllegalAccessException {
        logger.info("Generating inspection report. Period: {}, Format: {}", period, format);
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
        logger.info("Generating ISO report. ISO Type: {}, Period: {}, Format: {}", isoType, period, format);
        byte[] report = reportsService.generateISOReport(isoType, period, from, to, format);
        return buildReportResponse(report, period + "-report." + format, format);
    }

    private ResponseEntity<byte[]> buildReportResponse(byte[] reportData, String filename, String format) {
        HttpHeaders headers = new HttpHeaders();

        logger.debug("Building report response. Filename: {}, Format: {}", filename, format);
        switch (format.toLowerCase()) {
            case "pdf":
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "excel":
                headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                break;
            default:
                logger.error("Unsupported report format: {}", format);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
    }

    @GetMapping("/inspectors")
    public ResponseEntity<byte[]> getInspectorsReports(@RequestParam(required = false) String country,
                                                       @RequestParam(required = false) String skill,
                                                       Model model) {
        logger.info("Generating inspectors report.");
        byte[] report = reportsService.generateInspectorsReport();
        return buildReportResponse(report, "inspectors_report" + ".xlsx", "excel");
    }

    @GetMapping("/inspectors/agreement")
    public ResponseEntity<byte[]> generateInspectorAgreement(
            @RequestParam Long inspectorId,
            @RequestParam String inspectorName,
            @RequestParam String address,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String country) throws IOException {

        logger.info("Generating inspector agreement for inspectorId: {}", inspectorId);
        byte[] documentContent = "India".equalsIgnoreCase(country)
                ? agreementService.generateIndiaEmpaneledInspectorAgreement(inspectorName, address, email, phone)
                : agreementService.generateInternationalEmpaneledInspectorAgreement(inspectorName, address, email, phone);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("Inspector_Agreement_" + inspectorName.replace(" ", "_") + ".docx")
                .build());

        return new ResponseEntity<>(documentContent, headers, HttpStatus.OK);
    }

    @GetMapping("/inspectors/impartiality-doc")
    public ResponseEntity<byte[]> generateImpartialityReport(@RequestParam String inspectorName) throws IOException {
        logger.info("Generating impartiality document for inspector: {}", inspectorName);
        byte[] documentContent = agreementService.generateImpartialityDocument(inspectorName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("Impartiality_Agreement_" + inspectorName.replace(" ", "_") + ".docx")
                .build());

        return new ResponseEntity<>(documentContent, headers, HttpStatus.OK);
    }

}
