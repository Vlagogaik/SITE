package org.site.BoU.Controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.site.BoU.Entities.Clients;

@Controller
@RequestMapping("/")
public class FrontController {

    @RequestMapping("/home")
    public String home() {
        return "/home";
    }
    @RequestMapping("/profile")
    public String profile() {
        return "/profile";
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
