package org.site.BoU.Controllers;

import jakarta.validation.Valid;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
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

@RequestMapping("/clients")
@Controller
public class RegistrationContr {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientService clientService;

    @PostMapping("/register")
    public String save(@ModelAttribute("clients") @Valid Clients client, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            if (!clientService.existByLogin(client) && !clientService.existByPasport(client) && !clientService.existByNumber(client)) {
                client.setPassword(passwordEncoder.encode(client.getPassword()));
                client.getRole();
                clientService.save(client);
                return "/home";
            } else {
                model.addAttribute("error", "Пользователь с таким логином уже существует!");
                return "/register";
            }
        } else {
            return "/register";
        }
    }
    @PostMapping("/signIn")
    public String sign(@ModelAttribute("clients") Clients client, Model model) {
//        if (clientService.existByLoginAndPassword(client)) {
//            clientService.loadUserByUsername(client.getLogin());
//            return "/home";
//        } else {
//            System.out.println("Login failed for: " + client.getLogin());
//            model.addAttribute("error", "Неверный логин или пароль!");
//            return "/signIn";
//        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(client.getLogin(), client.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "/home";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Неправильный логин или пароль");
            return "signIn";
        }
    }
//    public Clients createClient(@RequestBody Clients client){
//
//        return clientsRep.save(client);
//    }
}
