package org.site.BoU.Controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Entities.Deposits;
import org.site.BoU.JWT.JwtTokenProvider;
import org.site.BoU.Repositories.DepositsRep;
import org.site.BoU.Services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import java.util.Optional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private DepositsRep depositsRep;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationContr.class);

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/deposit/create")
    public String createDeposit(@ModelAttribute Deposits deposit, Model model) {
        try {
            depositsRep.save(deposit);
            model.addAttribute("successMessage", "Вклад успешно создан.");
            return "redirect:/admin/deposit/create";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при создании вклада.");
            return "admin/deposit/create";
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/home_admin")
    public String adminPage() {
        return "/admin/home_admin";
    }
}
