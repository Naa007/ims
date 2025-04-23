package com.stepup.ims.controller;

import com.stepup.ims.model.InpsectionStatsByRole;
import com.stepup.ims.model.PerformanceTrendResponse;
import com.stepup.ims.service.EmployeeService;
import com.stepup.ims.service.InspectorService;
import com.stepup.ims.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_BUSINESS_DASHBOARD;

@Controller
@RequestMapping("/business")
@PreAuthorize("hasRole('BUSINESS')")
public class BusinessRoleController {

    @Autowired
    StatsService statsService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    InspectorService inspectorService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> businessStats = statsService.getBusinessStats();
        // Add the statistics to the model
        model.addAttribute("employeeStats", businessStats.get("Employee Stats"));
        model.addAttribute("clientStats", businessStats.get("Client Stats"));
        model.addAttribute("inspectorStats", businessStats.get("Inspector Stats"));
        model.addAttribute("inspectionStats", businessStats.get("Inspection Stats"));
        model.addAttribute("inspectionStatusStats", businessStats.get("Inspection Status Stats"));

        model.addAttribute("coordinatorsList", employeeService.getAllCoordinateEmployees());
        model.addAttribute("technicalCoordinatorsList", employeeService.getAllTechnicalCoordinateEmployees());
        model.addAttribute("inspectorsList",inspectorService.getAllActiveInspectors());
        return RETURN_TO_BUSINESS_DASHBOARD;
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
    @GetMapping("/trend-data")
    @ResponseBody
    public ResponseEntity<PerformanceTrendResponse> getTrendData(
            @RequestParam(required = false) String coordinator,
            @RequestParam(required = false) String technical,
            @RequestParam(required = false) String inspector) {

        PerformanceTrendResponse trendData = statsService.getPerformanceTrendData(coordinator, technical, inspector);
        return ResponseEntity.ok(trendData);
    }

}
