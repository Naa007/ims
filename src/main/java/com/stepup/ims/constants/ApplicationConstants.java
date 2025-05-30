package com.stepup.ims.constants;

import java.time.format.DateTimeFormatter;

public class ApplicationConstants {

    /* Terminologies in IMS */
    public static final String INSPECTION = "inspection";
    public static final String INSPECTIONS = "inspections";
    public static final String CONTRACT_REVIEW = "contractReview";
    public static final String INSPECTION_ADVISE = "inspectionAdvise";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String PQR = "pqr";

    /* Roles in IMS */
    public static final String ADMIN = "ADMIN";
    public static final String BUSINESS = "BUSINESS";
    public static final String COORDINATOR = "COORDINATOR";
    public static final String TECHNICAL_COORDINATOR = "TECHNICAL_COORDINATOR";
    public static final String INSPECTOR = "INSPECTOR";

    public static final String ADMIN_LOWERCASE = "admin";
    public static final String BUSINESS_LOWERCASE = "business";
    public static final String COORDINATOR_LOWERCASE = "coordinator";
    public static final String TECHNICAL_COORDINATOR_LOWERCASE = "technical-coordinator";
    public static final String INSPECTOR_LOWERCASE = "inspector";
    
    
    /* Scopes in IMS */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_BUSINESS = "ROLE_BUSINESS";
    public static final String ROLE_COORDINATOR = "ROLE_COORDINATOR";
    public static final String ROLE_TECHNICAL_COORDINATOR = "ROLE_TECHNICAL_COORDINATOR";
    public static final String ROLE_INSPECTOR = "ROLE_INSPECTOR";

    /* Commonly used words in IMS */
    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";
    public static final String TODAY = "TODAY";
    public static final String WEEK = "WEEK";
    public static final String MONTH = "MONTH";
    public static final String QUARTER = "QUARTER";
    public static final String YEAR = "YEAR";
    public static final String CUSTOM = "CUSTOM";
    public static final String TOTAL = "TOTAL";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String START = "start";
    public static final String DATE = "date";
    public static final String ON_FIELD = "onField";
    public static final String COUNTRY = "country";
    public static final String INSPECTOR_TYPE = "inspectorType";
    public static final String REPORT_REVIEW = "reportReview";


    /* Countries */
    public static final String INDIA_UPPERCASE = "INDIA";
    public static final String INDIA_LOWERCASE = "india";
    public static final String INTERNATIONAL_UPPERCASE = "INTERNATIONAL";
    public static final String INTERNATIONAL_LOWERCASE = "international";

    public static final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");




    /* Email constants */
    public static final String STATUS_CHANGE_EMAIL_SUBJECT = "IISPL Inspection #%d Status Update";


    public static final String DFAULT_MAP_ADDRESS = "Hyderabad, India";


    private ApplicationConstants() {
    }
}
