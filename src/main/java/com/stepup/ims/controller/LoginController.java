package com.stepup.ims.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String loginPage() {
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "redirect:/auth/login";
    }
}
