package org.site.BoU.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.site.BoU.Entities.Clients;

@Controller
@RequestMapping("/")
public class FrontController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationContr.class);

    @RequestMapping("/home")
    public String home() {
        return "/home";
    }
    @RequestMapping("/admin/home_admin")
    public String homeAdmin() {
        return "/admin/home_admin";
    }
    @RequestMapping("/admin/deposit/create")
    public String depositCreate() {
        return "/admin/deposit/create";
    }

    @RequestMapping("/user/profile")
    public String profile() {
        return "/user/profile";
    }
    @RequestMapping("/register")
    public String regIn(Model model) {
        model.addAttribute("clients", new Clients());
        return "/register";
    }
    @RequestMapping("/signIn")
    public String signIn(Model model, @AuthenticationPrincipal Clients client) {
        if (client != null) {
            return "redirect:/profile";
        }
        model.addAttribute("clients", new Clients());
        return "/signIn";
    }
}
