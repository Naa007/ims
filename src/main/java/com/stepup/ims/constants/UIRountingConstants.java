package com.stepup.ims.constants;

import org.springframework.stereotype.Component;

@Component
public class UIRountingConstants {
    public static final String REDIRECT_ADMIN_EMPLOYEE_MANAGEMENT = "redirect:/employee/employee-management";
    public static final String RETURN_TO_EMPLOYEE_MANAGEMENT = "/employee-management";
    public static final String RETURN_TO_DEFAULT_ERROR = "/error/default";

    public static final String RETURN_TO_INSPECTION_NEW = "/inspection-new";
    public static final String RETURN_TO_INSPECTION_MANAGEMENT = "/inspection-management";
    public static final String REDIRECT_INSPECTION_MANAGEMENT = "redirect:/inspection/inspection-management";

    public static final String ADMIN_DASHBOARD_URL = "/admin/dashboard";
    public static final String COORDINATOR_DASHBOARD_URL = "/coordinator/dashboard";
    public static final String BUSINESS_DASHBOARD_URL = "/business/dashboard";
    public static final String TECHNICAL_COORDINATOR_DASHBOARD_URL = "/technical-coordinator/dashboard";
    public static final String INSPECTOR_DASHBOARD_URL = "/inspector/dashboard";
    public static final String DEFAULT_DASHBOARD_URL = "/admin/dashboard";

    private UIRountingConstants() {
    }
}
