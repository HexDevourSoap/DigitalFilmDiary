package lv.venta.fidi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lv.venta.fidi.config.LocaleRedirectPaths;
import lv.venta.fidi.service.IAppUserService;

@Controller
public class RegistrationController {

    @Autowired
    private IAppUserService appUserService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword,
                           Model model,
                           HttpServletRequest request) {
        if (password == null || !password.equals(confirmPassword)) {
            model.addAttribute("registerError", "passwordMismatch");
            return "register";
        }
        try {
            appUserService.registerNewUser(email, password);
            return LocaleRedirectPaths.redirectLogin(request, "registered");
        } catch (Exception e) {
            model.addAttribute("registerError", "server");
            model.addAttribute("registerErrorDetail", e.getMessage());
            return "register";
        }
    }
}
