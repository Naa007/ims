package com.stepup.ims.service;

import com.stepup.ims.model.Client;
import com.stepup.ims.model.Employee;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.model.Inspector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportsService {

    @Autowired
    EmployeeService employeeService;
    @Autowired
    ClientService clientService;
    @Autowired
    InspectorService inspectorService;
    @Autowired
    InspectionService inspectionService;

    public Map<String, Object> getBusinessStats() {
        Map<String, Object> businessStats = new LinkedHashMap<>();

        businessStats.put("Employee Stats", getEmployeeStatistics());
        businessStats.put("Client Stats", getClientStatistics());
        businessStats.put("Inspector Stats", getInspectorStatistics());
        businessStats.put("Inspection Stats", getInspectionStatistics());

        return businessStats;
    }

    public Map<String, Integer> getEmployeeStatistics() {
        List<Employee> employees = employeeService.getAllEmployees();
        Map<String, Integer> employeeStats = new LinkedHashMap<>();

        int totalEmployees = employees.size();
        int activeEmployees = 0;
        int totalCoordinators = 0;
        int totalTechnicalCoordinators = 0;

        for (Employee employee : employees) {
            if ("YES".equalsIgnoreCase(employee.getActive())) {
                activeEmployees++;
            }
            if ("COORDINATOR".equalsIgnoreCase(employee.getRole())) {
                totalCoordinators++;
            }
            if ("TECHNICAL_COORDINATOR".equalsIgnoreCase(employee.getRole())) {
                totalTechnicalCoordinators++;
            }
        }

        employeeStats.put("Total Employees", totalEmployees);
        employeeStats.put("Active Employees", activeEmployees);
        employeeStats.put("Coordinators", totalCoordinators);
        employeeStats.put("Technical Coordinators", totalTechnicalCoordinators);

        return employeeStats;
    }

    public Map<String, Integer> getClientStatistics() {
        List<Client> clients = clientService.getAllClients();
        Map<String, Integer> clientStats = new LinkedHashMap<>();

        int totalClients = clients.size();
        int recentlyAddedClients = 0;
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        for (Client client : clients) {
            if (client.getConfirmationDate() != null && client.getConfirmationDate().isAfter(oneYearAgo)) {
                recentlyAddedClients++;
            }
        }

        clientStats.put("Total Clients", totalClients);
        clientStats.put("Recently Onboarded", recentlyAddedClients);

        return clientStats;
    }

    public Map<String, Integer> getInspectorStatistics() {
        List<Inspector> inspectors = inspectorService.getAllInspectors();
        Map<String, Integer> inspectorStats = new LinkedHashMap<>();

        int totalInspectors = inspectors.size();
        int activeInspectors = 0;
        int inactiveInspectors = 0;
        int blockedInspectors = 0;
        int inHouseInspectors = 0;
        int partnerInspectors = 0;
        int freelancerInspectors = 0;
        int technicalCoordinators = 0;

        for (Inspector inspector : inspectors) {
            if ("ACTIVE".equalsIgnoreCase(String.valueOf(inspector.getInspectorStatus()))) {
                activeInspectors++;
            } else if ("INACTIVE".equalsIgnoreCase(String.valueOf(inspector.getInspectorStatus()))) {
                inactiveInspectors++;
            } else if ("BLOCKED".equalsIgnoreCase(String.valueOf(inspector.getInspectorStatus()))) {
                blockedInspectors++;
            }

            if ("INHOUSE_INSPECTOR".equalsIgnoreCase(String.valueOf(inspector.getInspectorType()))) {
                inHouseInspectors++;
            } else if ("PARTNER_INSPECTOR".equalsIgnoreCase(String.valueOf(inspector.getInspectorType()))) {
                partnerInspectors++;
            } else if ("FREELANCER".equalsIgnoreCase(String.valueOf(inspector.getInspectorType()))) {
                freelancerInspectors++;
            } else if ("TECHNICAL_COORDINATOR".equalsIgnoreCase(String.valueOf(inspector.getInspectorType()))) {
                technicalCoordinators++;
            }
        }

        inspectorStats.put("Total Inspectors", totalInspectors);
        inspectorStats.put("Active Inspectors", activeInspectors);
        inspectorStats.put("Inhouse Inspectors", inHouseInspectors + technicalCoordinators);
        inspectorStats.put("Partner Inspectors", partnerInspectors);
        inspectorStats.put("Freelancer Inspectors", freelancerInspectors);

        return inspectorStats;
    }

    public Map<String, Integer> getInspectionStatistics() {
        List<Inspection> inspections = inspectionService.getAllInspections();
        Map<String, Integer> inspectionStats = new LinkedHashMap<>();

        int totalInspections = inspections.size();
        int completedInspections = 0;
        int ongoingInspections = 0;
        int pendingInspections = 0;

        for (Inspection inspection : inspections) {
            if (inspection.getProposedCVs().stream().anyMatch(cv -> "YES".equalsIgnoreCase(cv.isCvStatus() ? "YES" : "NO"))) {
                completedInspections++;
            } else {
                pendingInspections++;
            }
        }

        inspectionStats.put("Total Inspections", totalInspections);
        inspectionStats.put("Completed Inspections", completedInspections);
        inspectionStats.put("Ongoing Inspections", ongoingInspections);
        inspectionStats.put("Pending Inspections", pendingInspections);

        return inspectionStats;
    }
}
