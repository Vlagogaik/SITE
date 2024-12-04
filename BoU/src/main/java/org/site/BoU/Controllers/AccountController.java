package org.site.BoU.Controllers;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Repositories.AccountsRep;
import org.site.BoU.Services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountsRep accountsRep;

    @Autowired
    private ClientService clientService;

    @PostMapping("create")
    public String createAccount(@RequestParam("currency") String currency, Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = (String) session.getAttribute("login");
        Clients client = clientService.findByLogin(login);
        Accounts newAccount = new Accounts();
        newAccount.setIdClient(client);
        newAccount.setAmount(0L);
        newAccount.setCurrency(currency);
        newAccount.setStatus("o");
        accountsRep.save(newAccount);


        model.addAttribute("clients", client);
        model.addAttribute("accounts", newAccount);
        return "redirect:/user/profile";
    }

}

