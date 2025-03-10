package com.stepup.ims.constants;

import org.springframework.stereotype.Component;

@Component
public class UIRoutingConstants {

    // Employee Management
    public static final String RETURN_TO_EMPLOYEE_MANAGEMENT = "employees/employee-management";
    public static final String REDIRECT_ADMIN_EMPLOYEE_MANAGEMENT = "redirect:/employee/employee-management";

    // Client Management
    public static final String RETURN_TO_CLIENT_MANAGEMENT = "clients/client-management";
    public static final String REDIRECT_TO_CLIENT_MANAGEMENT = "redirect:/client/list";

    // Inspector Management
    public static final String RETURN_TO_INSPECTOR_MANAGEMENT = "inspectors/inspector-management";
    public static final String REDIRECT_TO_INSPECTOR_MANAGEMENT = "redirect:/inspectors/list";
    public static final String REDIRECT_TO_INSPECTOR_FORM = "inspectors/inspector-form";
    public static final String REDIRECT_TO_INSPECTOR_VIEW = "inspectors/inspector-view";

    // Inspection Management
    public static final String RETURN_TO_INSPECTION_FORM = "inspections/inspection-form";
    public static final String RETURN_TO_INSPECTION_VIEW = "inspections/inspection-view";
    public static final String RETURN_TO_INSPECTION_MANAGEMENT = "inspections/inspection-management";
    public static final String REDIRECT_INSPECTION_MANAGEMENT = "redirect:/inspection/inspection-management";

    // Role Based Management
    public static final String ADMIN_DASHBOARD_URL = "/admin/dashboard";
    public static final String COORDINATOR_DASHBOARD_URL = "/coordinator/dashboard";
    public static final String BUSINESS_DASHBOARD_URL = "/business/dashboard";
    public static final String TECHNICAL_COORDINATOR_DASHBOARD_URL = "/technical-coordinator/dashboard";
    public static final String INSPECTOR_DASHBOARD_URL = "/inspector/dashboard";
    public static final String DEFAULT_DASHBOARD_URL = "/admin/dashboard";

    public static final String RETURN_TO_ADMIN_DASHBOARD = "admin/admin-dashboard";
    public static final String RETURN_TO_COORDINATOR_DASHBOARD = "coordinator/coordinator-dashboard";
    public static final String RETURN_TO_BUSINESS_DASHBOARD = "business/business-dashboard";
    public static final String RETURN_TO_TECHNICAL_COORDINATOR_DASHBOARD = "technical-coordinator/technical-coordinator-dashboard";
    public static final String RETURN_TO_INSPECTOR_DASHBOARD = "inspector/inspector-dashboard";

    // Default Error
    public static final String RETURN_TO_DEFAULT_ERROR = "/error/default";

    private UIRoutingConstants() {
    }
}
