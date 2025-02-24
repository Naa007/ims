package com.stepup.ims.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class CustomErrorController implements ErrorController {

    // Map all errors to this method
    @RequestMapping("/error")
    public String handleError(HttpSession session, Model model) {
        // Retrieve HTTP status code
        Object status = session.getAttribute("javax.servlet.error.status_code");
        String errorMessage = "An error occurred";

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.UNAUTHORIZED.value()) { // Handle 401 Unauthorized
                errorMessage = "You are not authorized to access this resource.";
                model.addAttribute("errorTitle", "Unauthorized Access");
                model.addAttribute("errorMessage", errorMessage);
                return "error/401"; // Use a custom 401 error page
            }
        }

        // Handle other cases (like 404, 500, etc.)
        model.addAttribute("errorTitle", "Unexpected Error");
        model.addAttribute("errorMessage", errorMessage);
        return "error/default"; // Fallback to a default error page
    }
}
