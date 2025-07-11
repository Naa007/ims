package com.stepup.ims.service;

import com.stepup.ims.model.Employee;
import com.stepup.ims.model.Inspector;
import com.stepup.ims.model.Inspection;
import com.stepup.ims.modelmapper.InspectionModelMapper;
import com.stepup.ims.modelmapper.InspectorModelMapper;
import com.stepup.ims.repository.InspectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.stepup.ims.constants.ApplicationConstants.*;

@Service
public class InspectionService {

    private static final Logger logger = LoggerFactory.getLogger(InspectionService.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private InspectorService inspectorService;

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private InspectionModelMapper inspectionModelMapper;
    
    @Autowired
    private InspectorModelMapper inspectorModelMapper;


    /**
     * Get all inspections.
     */
    public List<Inspection> getAllInspections() {
        logger.info("Fetching all inspections");
        return inspectionModelMapper.toModelList(inspectionRepository.findAll());
    }

    /**
     * Get all inspections by date.
     */
    public List<Inspection> getInspectionsByDate(String date) {
        try {
            logger.info("Fetching inspections by date: {}", date);
            LocalDateTime selectedDate = LocalDate.parse(date, inputFormatter).atStartOfDay();
            List<com.stepup.ims.entity.Inspection> inspections = inspectionRepository.findByCreatedDate(selectedDate);
            return inspectionModelMapper.toModelList(inspections);
        } catch (Exception e) {
            logger.error("Invalid date format for getInspectionsByDate: {}", date, e);
            throw new IllegalArgumentException("Invalid date format. Please use the correct format: " + inputFormatter.toString(), e);
        }
    }


    /**
     * Get all inspections between two dates.
     */
    public List<Inspection> getInspectionsBetweenDates(String fromDate, String toDate) {
        try {
            logger.info("Fetching inspections between {} and {}", fromDate, toDate);
            LocalDateTime start = LocalDateTime.parse(fromDate);
            LocalDateTime end = LocalDateTime.parse(toDate);
            List<com.stepup.ims.entity.Inspection> inspections = inspectionRepository.findByCreatedDateBetween(start, end);
            return inspectionModelMapper.toModelList(inspections);
        } catch (Exception e) {
            logger.error("Error parsing date range: {} - {}", fromDate, toDate, e);
            throw new IllegalArgumentException(
                    "Invalid date format. Please use the correct format: " + inputFormatter.toString(), e);
        }
    }

    /**
     * Get inspections by client and between two dates.
     */
    public List<Inspection> getInspectionsByClientAndBetweenDates(String client, String fromDate, String toDate) {
        try {
            logger.info("Fetching inspections for client {} between {} and {}", client, fromDate, toDate);
            LocalDateTime start = LocalDateTime.parse(fromDate);
            LocalDateTime end = LocalDateTime.parse(toDate);
            List<com.stepup.ims.entity.Inspection> inspections = inspectionRepository.findByClient_ClientIdAndCreatedDateBetween(Long.valueOf(client), start, end);
            return inspectionModelMapper.toModelList(inspections);
        } catch (Exception e) {
            logger.error("Error parsing client/date range for client {}: {} - {}", client, fromDate, toDate, e);
            throw new IllegalArgumentException(
                    "Invalid date format. Please use the correct format: " + inputFormatter.toString(), e);
        }
    }

    /**
     * Get all inspections created by the current user.
     */
    public List<Inspection> getAllInspectionsByCreatedBy() {
        logger.info("Fetching inspections created by user");
        return inspectionModelMapper.toModelList(inspectionRepository.findByCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    /**
     * Get an inspection by its ID.
     */
    public Optional<Inspection> getInspectionById(Long inspectionId) {
        logger.info("Fetching inspection by ID: {}", inspectionId);
        return inspectionModelMapper.getOptionalModel(inspectionRepository.findById(inspectionId));
    }

    /**
     * Save or update an inspection.
     */
    @Transactional
    public Inspection saveInspection(Inspection inspection) {
        logger.info("Saving inspection");
        var inspectionEntity = inspectionModelMapper.toEntity(inspection);

        if (inspectionEntity.getProposedCVs().size() == 1 && inspectionEntity.getProposedCVs().get(0).getId() == null && inspectionEntity.getProposedCVs().get(0).getInspector().getInspectorId() == null) {
            inspectionEntity.setProposedCVs(null);
            logger.debug("Proposed CVs cleared due to empty inspector data");
        }
        if (inspectionEntity.getInspectionReports().size() == 1 && (inspectionEntity.getInspectionReports().get(0).getReportNumber().isEmpty() || inspectionEntity.getInspectionReports().get(0).getInspectorName().isEmpty())) {
            inspectionEntity.setInspectionReports(null);
            logger.debug("Inspection reports cleared due to missing report number and inspector name");
        }
        if (inspectionEntity.getId() == null) {
            inspectionEntity.setCoordinatorName(employeeService.getEmployeeNameByEmail(SecurityContextHolder.getContext().getAuthentication().getName()));
        }
        var savedInspectionEntity = inspectionRepository.save(inspectionEntity);
        logger.info("Inspection saved successfully with ID: {}", savedInspectionEntity.getId());
        return inspectionModelMapper.toModel(savedInspectionEntity);
    }


    /**
     * Get all inspections reviewed by the current user.
     */
    public List<Inspection> getInspectionsReviewedByLoggedUser() {
        String currentUser = employeeService.getEmployeeIdByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName());
        logger.info("Fetching inspections reviewed by user ID: {}", currentUser);
        return inspectionModelMapper.toModelList(
                inspectionRepository.findByProposedCVs_CvReviewBytechnicalCoordinator_EmpIdOrDocumentsReviewedByTechnicalCoordinatorOrInspectionReviewedBy(
                       currentUser, currentUser, currentUser ));
    }

    /**
     * Get all inspections assigned to the current user.
     */
    public List<Inspection> getInspectionsOfInspectorByLoggedUser() {
        logger.info("Fetching inspections assigned to inspector");
        return inspectionModelMapper.toModelList(
                inspectionRepository.findByProposedCVs_Inspector_Email(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    public String[] getTechnicalCoordinatorsByInspectionId(Long inspectionId) {
        logger.info("Fetching technical coordinators for inspection ID: {}", inspectionId);
        List<String> techCoordinators = inspectionRepository.findAllTechnicalCoordinatorsOfInspection(inspectionId);
        return techCoordinators == null || techCoordinators.isEmpty()
                ? new String[0]
                : techCoordinators.stream().map(String::trim).distinct().toArray(String[]::new);
    }

    public List<Map<String, String>> fetchInspectionStats(String period) {
        logger.info("Fetching inspection statistics for period: {}", period);
        String startDate;
        String endDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        switch (period.toUpperCase()) {
            case WEEK -> {
                startDate = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).format(formatter);
                endDate = LocalDate.now().format(formatter);
            }
            case MONTH -> {
                startDate = LocalDate.now().withDayOfMonth(1).format(formatter);
                endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).format(formatter);
            }
            case QUARTER -> {
                int currentQuarter = (LocalDate.now().getMonthValue() - 1) / 3 + 1;
                startDate = LocalDate.now().withMonth((currentQuarter - 1) * 3 + 1).withDayOfMonth(1).format(formatter);
                endDate = LocalDate.parse(startDate, formatter).plusMonths(2).withDayOfMonth(LocalDate.parse(startDate, formatter).plusMonths(2).lengthOfMonth()).format(formatter);
            }
            case YEAR -> {
                startDate = LocalDate.now().withDayOfYear(1).format(formatter);
                endDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear()).format(formatter);
            }
            default -> throw new IllegalArgumentException("Invalid period. Allowed values are: month, quarter, year.");
        }
        logger.debug("Fetching inspections between {} and {}", startDate, endDate);
        List<com.stepup.ims.entity.Inspection> inspections = inspectionRepository.findByInspectionDateAsPerNotificationBetween(startDate, endDate);
        return getInspectionCalendarStats(inspections);
    }

    private List<Map<String, String>> getInspectionCalendarStats(List<com.stepup.ims.entity.Inspection> inspections) {
        logger.debug("Generating inspection calendar stats for {} inspections", inspections.size());
        List<Map<String, String>> detailsList = new ArrayList<>();

        // Pre-calculate inspection dates and participating inspectors
        Map<String, Set<String>> inspectorsByDate = new HashMap<>();
        inspections.stream()
                .filter(inspection -> inspection.getProposedCVs() != null && !inspection.getProposedCVs().isEmpty())
                .flatMap(inspection -> inspection.getProposedCVs().stream()
                        .filter(cv -> cv != null && cv.isCvStatus() && cv.getInspector() != null)
                        .flatMap(cv -> inspection.getInspectionDateAsPerNotification().stream().map(date -> {
                            String parsedDate = LocalDate.parse(date, inputFormatter).format(outputFormatter);
                            String inspectorId = String.valueOf(cv.getInspector().getInspectorId());
                            inspectorsByDate
                                    .computeIfAbsent(parsedDate, d -> new HashSet<>())
                                    .add(inspectorId);

                            Map<String, String> details = new HashMap<>();
                            details.put(ID, inspectorId);
                            details.put(TITLE, String.format("%s - %s", cv.getInspector().getInspectorName(), inspection.getInspectionLocationDetails()));
                            details.put(START, parsedDate);
                            details.put(DATE, parsedDate);
                            details.put(INSPECTOR_TYPE, cv.getInspector().getInspectorType().toString());
                            details.put(COUNTRY, INDIA_LOWERCASE.equalsIgnoreCase(inspection.getInspectionCountry())
                                    ? INDIA_LOWERCASE : INTERNATIONAL_LOWERCASE);
                            details.put(ON_FIELD, YES);
                            return details;
                        })))
                .forEach(detailsList::add);

        // Cache INHOUSE inspectors list
        List<Inspector> inHouseInspectors = inspectorService.getInspectorsListByCountry("India").stream()
                .filter(inspector -> Inspector.InspectorType.INHOUSE_INSPECTOR.equals(inspector.getInspectorType()))
                .toList();

        // Inspectors who were not participating in the inspection
        inspectorsByDate.forEach((date, participatingInspectorIds) ->
            inHouseInspectors.stream()
                    .filter(inspector -> !participatingInspectorIds.contains(String.valueOf(inspector.getInspectorId())))
                    .forEach(inspector -> {
                        Map<String, String> details = new HashMap<>();
                        details.put(ID, String.valueOf(inspector.getInspectorId()));
                        details.put(TITLE, inspector.getInspectorName());
                        details.put(START, date);
                        details.put(INSPECTOR_TYPE, inspector.getInspectorType().toString());
                        details.put(COUNTRY, INDIA_LOWERCASE);
                        details.put(ON_FIELD, NO);
                        detailsList.add(details);
                    })
        );

        // Cache all technical coordinators and process them
        List<Employee> technicalCoordinators = employeeService.getAllTechnicalCoordinateEmployees();
        inspectorsByDate.forEach((date, participatingInspectorIds) ->
            technicalCoordinators.forEach(technicalCoord -> {
                String inspectorId = inspectorService.getInspectorIdByEmail(technicalCoord.getEmail());
                if (!participatingInspectorIds.contains(inspectorId)) {
                    Map<String, String> details = new HashMap<>();
                    details.put(ID, String.valueOf(technicalCoord.getEmpId()));
                    details.put(TITLE, technicalCoord.getEmpName());
                    details.put(START, date);
                    details.put(INSPECTOR_TYPE, "TECHNICAL_COORDINATOR");
                    details.put(COUNTRY, INDIA_LOWERCASE);
                    details.put(ON_FIELD, REPORT_REVIEW);
                    detailsList.add(details);
                }
            })
        );
        logger.debug("Inspection calendar stats built with {} records", detailsList.size());
        return detailsList;
    }

}
