package com.stepup.ims.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/")
    public String loginPage(HttpSession session) {
        logger.info("Login page accessed. Invalidating existing session.");
       return invalidateSession(session);
    }

    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        logger.info("Logout initiated. Invalidating session.");
        return invalidateSession(session);
    }

    private String invalidateSession(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
