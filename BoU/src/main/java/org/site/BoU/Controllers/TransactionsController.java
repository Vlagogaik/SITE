package org.site.BoU.Controllers;

import jakarta.servlet.http.HttpSession;
import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.ClientDeposit;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/user")
@Controller
public class TransactionsController {
    @Autowired
    DepositService depositService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    AccountsService accountsService;
    @Autowired
    ClientService clientService;
    @Autowired
    ClientDepositService clientDepositService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(TransactionsController.class);

    @GetMapping("/getAccounts")
    @ResponseBody
    public List<Accounts> getRecipientAccounts(@RequestParam Long clientId) {
        Optional<Clients> clients = clientService.findById(clientId);
        logger.info("Метод getRecipientAccounts вызван с clientId = {}", clientId);
        List<Accounts> allAccounts = accountsService.findAllByClientId(clients.orElseThrow(() ->
                new IllegalArgumentException("Клиент с id " + clientId + " не найден")));
        logger.info("Метод getRecipientAccounts нашел  allAccounts = {}", allAccounts);
        return allAccounts.stream()
                .filter(acc -> "o".equals(acc.getStatus()))
                .collect(Collectors.toList());
    }

    @GetMapping("/searchClient")
    @ResponseBody
    public List<Clients> searchClient(@RequestParam String query) {
        logger.info("Метод searchClient вызван с query = {}", query);
        List<Clients> clients = clientService.findByIdOrPhone(query);
        logger.info("Найдено клиентов: {}", clients);
        return clients;
    }


    @PostMapping("/transfer")
    public String transferMoney(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam Long amount,
            @RequestParam String query,
            Model model,
            HttpSession session) {

        String login = (String) session.getAttribute("login");
        Clients client = clientService.findByLogin(login);
        List<Accounts> clientAccounts = accountsService.findAllByClientId(client);
        model.addAttribute("clientAccounts", clientAccounts);

        Accounts fromAccount = accountsService.findById(fromAccountId);
        if (fromAccount == null) {
            model.addAttribute("error", "Счет отправителя не найден.");
            return "user/transactions";
        }

//        Clients recipient = clientService.findByIdOrPhone(toClientIdOrPhone);
        List<Clients> recipientList = clientService.findByIdOrPhone(query);
        if (recipientList.isEmpty()) {
            model.addAttribute("error", "Получатель не найден.");
            return "user/transactions";
        }
        Accounts toAccount = accountsService.findById(toAccountId);
        if (toAccount == null || !"o".equals(toAccount.getStatus())) {
            model.addAttribute("error", "Выбранный счет получателя недоступен.");
            return "user/transactions";
        }
        logger.info("fromAccount={},toAccount={}, recipientList={}",fromAccount, toAccount, recipientList);
        try {
            transactionService.transferMoney(fromAccountId, toAccountId, amount);
            model.addAttribute("success", "Перевод выполнен успешно.");
        } catch (IllegalStateException e) {
            model.addAttribute("error", "Ошибка: " + e.getMessage());
        }

        return "redirect:/user/transactions";
    }

}
