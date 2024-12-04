package org.site.BoU.Controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.site.BoU.Entities.*;
import org.site.BoU.Repositories.AccountsRep;
import org.site.BoU.Services.AccountsService;
import org.site.BoU.Services.ClientDepositService;
import org.site.BoU.Services.ClientService;
import org.site.BoU.Services.DepositService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class DepositController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationContr.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private DepositService depositService;

    @Autowired
    private AccountsRep accountsRep;
    @Autowired
    private AccountsService accountsService;

    @Autowired
    private ClientDepositService clientDepositService;

    @PostMapping("deposit/create")
    public String createDeposit(@RequestParam Long idDeposit, @RequestParam Long amount, @RequestParam String currency, @RequestParam Long idAccount,
                                Model model, HttpSession session) {
        Clients client = clientService.findByLogin((String) session.getAttribute("login"));
        if (client == null) {
            return "redirect:/signIn";
        }
        Accounts account = accountsService.findById(idAccount);
        Deposits deposit = depositService.findByIdDeposit(idDeposit);

        if (deposit == null) {
            logger.warn("Депозит не найден для ID: {}", idDeposit);
            model.addAttribute("error", "Указанный депозит не найден.");
            List<Deposits> deposits = depositService.getAllDeposits();
            model.addAttribute("deposits", deposits);
            model.addAttribute("accounts", accountsService.getAccountsByClient(client));
            return "allDeposits";
        }
        if (account == null) {
            logger.warn("Счёт не найден для ID: {}", idAccount);
            model.addAttribute("error", "Указанный счёт не найден.");
            List<Deposits> deposits = depositService.getAllDeposits();
            model.addAttribute("deposits", deposits);
            model.addAttribute("accounts", accountsService.getAccountsByClient(client));
            return "allDeposits";
        }
        if (!account.getCurrency().equals(currency)) {
            logger.warn("Валюта счёта не совпадает с валютой вклада. Пользователь: {}, Счёт ID: {}, Баланс: {} {}, Валюта счёта: {}",
                    client.getLogin(), idAccount, account.getAmount(), account.getCurrency(), currency);
            model.addAttribute("error", "Валюта счёта не совпадает с валютой вклада.");
            List<Deposits> deposits = depositService.getAllDeposits();
            model.addAttribute("deposits", deposits);
            model.addAttribute("accounts", accountsService.getAccountsByClient(client));
            return "allDeposits";
        }
        if (account.getAmount() < amount) {
            model.addAttribute("error", "Недостаточно средств на счете.");
            List<Deposits> deposits = depositService.getAllDeposits();
            model.addAttribute("deposits", deposits);
            model.addAttribute("accounts", accountsService.getAccountsByClient(client));
            return "allDeposits";
        }

        Accounts newAccount = new Accounts();
        newAccount.setIdClient(client);
        newAccount.setAmount(0L);
        newAccount.setCurrency(currency);
        newAccount.setStatus("od");
        accountsRep.save(newAccount);
        logger.info("Создался счет: {}.", newAccount);

        ClientDeposit clientDeposit = new ClientDeposit();
        clientDeposit.setDepositStatus("o");
        clientDeposit.setInitialAmount(amount);
        clientDeposit.setTimeInDays(deposit.getMaxTermDays());
        clientDeposit.setOpenDate(new Date());
        clientDeposit.setIdAccount(newAccount);
        clientDeposit.setIdDeposit(deposit);
        logger.info("Создался ClientDeposit: {} с idAcc: {}, idDep: {}.", clientDeposit, newAccount.getIdAccount(), deposit.getIdDeposit());
        clientDepositService.save(clientDeposit);

        account.setAmount(account.getAmount() - amount);
        newAccount.setAmount(amount);
        accountsRep.save(newAccount);
        accountsRep.save(account);
        logger.info("Сумма {} списана со счёта {}.", amount, idAccount);

        return "redirect:/user/profile";
    }
}
