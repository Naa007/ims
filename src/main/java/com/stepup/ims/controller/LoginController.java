package com.stepup.ims.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String loginPage(HttpSession session) {
       return invalidateSession(session);
    }

    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        return invalidateSession(session);
    }

    private String invalidateSession(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
