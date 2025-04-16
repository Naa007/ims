package com.stepup.ims.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.stepup.ims.constants.UIRoutingConstants.*;

@Controller
public class CustomErrorController implements ErrorController {



    // Map all errors to this method
    @RequestMapping(ERROR_PATH)
    public String handleError(HttpServletRequest request
            , Model model) {
        // Retrieve HTTP status code
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        String errorMessage = "An error occurred";

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.UNAUTHORIZED.value()) { // Handle 401 Unauthorized
                errorMessage = "You are not authorized to access this resource.";
                model.addAttribute(ERROR_TITLE, "Unauthorized Access");
                model.addAttribute(ERROR_MESSAGE, errorMessage);
                return RETURN_TO_DEFAULT_ERROR; // Ensure it maps to the proper error path
            }
            if (statusCode == HttpStatus.FORBIDDEN.value()) { // Handle 401 Unauthorized
                errorMessage = "Forbidden: You are not authorized to access this resource.";
                model.addAttribute(ERROR_TITLE, "Access Forbidden");
                model.addAttribute(ERROR_MESSAGE, errorMessage);
                return RETURN_TO_DEFAULT_ERROR; // Ensure it maps to the proper error path
            }
        }

        // Handle other cases (like 404, 500, etc.)
        model.addAttribute(ERROR_TITLE, "Unexpected Error");
        model.addAttribute(ERROR_MESSAGE, errorMessage);
        return RETURN_TO_DEFAULT_ERROR; // Fallback to a default error page
    }

}
