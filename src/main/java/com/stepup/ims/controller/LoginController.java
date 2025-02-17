package com.stepup.ims.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Welcome to Thymeleaf Demo");
        return "login";
    }
}
