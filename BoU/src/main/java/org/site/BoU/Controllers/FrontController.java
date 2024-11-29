package org.site.BoU.Controllers;

import org.site.BoU.Entities.Deposits;
import org.site.BoU.Services.DepositService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.site.BoU.Entities.Clients;

import java.util.List;


@Controller
@RequestMapping("/")
public class FrontController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationContr.class);

    @Autowired
    private DepositService depositService;

    @GetMapping("/allDeposits")
    public String getDeposits(Model model) {
        List<Deposits> deposits = depositService.getAllDeposits();
        model.addAttribute("deposits", deposits);
        return "/allDeposits";
    }
//    @RequestMapping("/allDeposits")
//    public String allDeposits(Model model) {
//        return "/allDeposits";
//    }
    @RequestMapping("/home")
    public String home() {
        return "/home";
    }
    @RequestMapping("/admin/home_admin")
    public String homeAdmin(Model model, @AuthenticationPrincipal Clients client) {
//        model.addAttribute("clients", new Clients());
        return "/admin/home_admin";
    }
    @RequestMapping("/admin/deposit")
    public String depositCreate(Model model) {
        model.addAttribute("deposits", new Deposits());
        return "/admin/deposit";
    }

    @RequestMapping("/user/profile")
    public String profile(Model model, @AuthenticationPrincipal Clients client) {
        model.addAttribute("clients", new Clients());
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
            return "redirect:/signIn";
        }
        model.addAttribute("clients", new Clients());
        return "/signIn";
    }

}
