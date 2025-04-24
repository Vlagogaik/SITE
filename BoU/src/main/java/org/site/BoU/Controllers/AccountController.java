package org.site.BoU.Controllers;

import org.site.BoU.Entities.ClientDeposit;
import org.site.BoU.Entities.Transaction;
import org.site.BoU.Services.AccountsService;
import org.site.BoU.Services.ClientDepositService;
import org.site.BoU.Services.TransactionService;
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
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private ClientDepositService clientDepositService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping("create")
    public String createAccount(@RequestParam("currency") String currency, Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = (String) session.getAttribute("login");
        Clients client = clientService.findByLogin(login);
        Accounts newAccount = new Accounts();
        newAccount.setIdClient(client);
        newAccount.setAmount(1000L);
        newAccount.setCurrency(currency);
        newAccount.setStatus("o");
        accountsRep.save(newAccount);


        model.addAttribute("clients", client);
        model.addAttribute("accounts", newAccount);
        return "redirect:/user/profile";
    }

    @PostMapping("delete/{id}")
    public String deleteAccount(
            @PathVariable Long id,
            @RequestParam("targetAccountId") Long targetAccountId,
            Model model,
            HttpSession session) {
        Accounts acc = accountsService.findById(id);
        if (acc == null) {
            model.addAttribute("error", "Счет не найден.");
            return "redirect:/user/profile";
        }
        Accounts targetAcc = accountsService.findById(targetAccountId);
        if (targetAcc == null) {
            model.addAttribute("error", "Не выбран счёт для перевода остатка.");
            return "redirect:/user/profile";
        }
        double remaining = acc.getAmount();
        if (remaining > 0) {
            Transaction transaction = transactionService.transferMoney(id, targetAccountId, remaining);
        }
        acc.setStatus("c");
        accountsService.save(acc);
        return "redirect:/user/profile";
    }
}

