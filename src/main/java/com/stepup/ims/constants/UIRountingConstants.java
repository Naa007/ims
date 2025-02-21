package com.stepup.ims.constants;

import org.springframework.stereotype.Component;

@Component
public class UIRountingConstants {
    public static final String REDIRECT_ADMIN_EMPLOYEE_MANAGEMENT = "redirect:/admin/employee-management";
    public static final String RETURN_TO_EMPLOYEE_MANAGEMENT = "employee-management";

    private UIRountingConstants() {
    }
}
