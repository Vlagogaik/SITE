package org.site.BoU.Controllers;

import jakarta.servlet.http.HttpSession;
import org.site.BoU.Entities.*;
import org.site.BoU.Repositories.AccountsRep;
import org.site.BoU.Services.ClientDepositService;
import org.site.BoU.Services.ClientService;
import org.site.BoU.Services.DepositService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.Date;

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
    private ClientDepositService clientDepositService;

    @PostMapping("/deposit/create")
    public String createDeposit(@RequestParam Long depositId, @RequestParam Long amount, @RequestParam String currency, Model model, HttpSession session) {
        Clients client = clientService.findByLogin((String) session.getAttribute("login"));
        if (client == null) {
            return "redirect:/signIn";
        }



        Deposits deposit = depositService.findByIdDeposit(depositId);
        Accounts newAccount = new Accounts();
        newAccount.setIdClient(client);
        newAccount.setAmount(0L);
        newAccount.setCurrency(currency);
        newAccount.setStatus('o');
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

        return "redirect:/user/profile";
    }
}
