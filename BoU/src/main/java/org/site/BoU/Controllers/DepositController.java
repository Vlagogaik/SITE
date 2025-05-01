package org.site.BoU.Controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.site.BoU.Entities.*;
import org.site.BoU.Repositories.AccountsRep;
import org.site.BoU.Repositories.CourseRep;
import org.site.BoU.Services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CourseRep courseRep;

    @PostMapping("prolongDeposit/{id}")
    public String prolongDeposit(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date closeDate,
            Model model, HttpSession session
    ) {
        ClientDeposit clientDeposit = clientDepositService.findById(id);
        Deposits deposit = depositService.findByIdDeposit(clientDeposit.getIdDeposit().getIdDeposit());

        if (!model.containsAttribute("activeSection")) {
            model.addAttribute("activeSection", "personal");
        }
        String login = (String) session.getAttribute("login");

        if (login != null) {
            Clients client = clientService.findByLogin(login);
            List<Accounts> Allaccounts = accountsService.findAllByClientId(client);

            Map<Long, ClientDeposit> clientDepositsMap = new HashMap<>();
            Map<Long, List<Accounts>> availableAccountsMap = new HashMap<>();
            for (Accounts account : Allaccounts) {
                ClientDeposit ALLclientDeposit = clientDepositService.findByAccountId(account.getIdAccount());
                if (ALLclientDeposit != null && (!Objects.equals(ALLclientDeposit.getDepositStatus(), "c"))) {
                    clientDepositsMap.put(account.getIdAccount(), ALLclientDeposit);
                    Clients owner = account.getIdClient();
                    if ((owner != null)) {
                        List<Accounts> ownerAccounts = accountsService.findAllByClientId(owner);
                        List<Accounts> availableAccounts = ownerAccounts.stream()
                                .filter(acc -> !acc.getIdAccount().equals(account.getIdAccount()))
                                .filter(acc -> "o".equals(acc.getStatus()))
                                .collect(Collectors.toList());
                        availableAccountsMap.put(account.getIdAccount(), availableAccounts);
                    }
                }
            }
            List<Accounts> uniqueAccounts = new ArrayList<>();
            for (List<Accounts> list : availableAccountsMap.values()) {
                uniqueAccounts.addAll(list);
            }
            uniqueAccounts = uniqueAccounts.stream().distinct().collect(Collectors.toList());
            model.addAttribute("accounts", uniqueAccounts);
            model.addAttribute("depaccounts", clientDepositsMap);
            model.addAttribute("client", client);

            List<Transaction> transactions = transactionService.findAllByClient(client);
            model.addAttribute("transactions", transactions);
        }

        if(clientDeposit.getDepositStatus().equals("ac") || clientDeposit.getDepositStatus().equals("c")){
            clientDeposit.setDepositStatus("o");
        }
        LocalDate today = clientDeposit.getOpenDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate close = closeDate
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        long monthsBetween = ChronoUnit.MONTHS.between(today.withDayOfMonth(1), close.withDayOfMonth(1));
        if (monthsBetween < deposit.getMinTermDays() || monthsBetween > deposit.getMaxTermDays()) {
            model.addAttribute("error", String.format(
                    "Срок вклада должен быть от %d до %d месяцев. Вы выбрали: %d",
                    deposit.getMinTermDays(), deposit.getMaxTermDays(), monthsBetween));
            logger.warn("Срок вклада должен быть от {} до {} месяцев. Вы выбрали: {}",
                    deposit.getMinTermDays(), deposit.getMaxTermDays(), monthsBetween);
            return "user/profile";
        }
        Accounts accounts = accountsService.findById(clientDeposit.getIdAccount().getIdAccount());
        if(accounts.getAmount() > deposit.getMaxAmount()){
            model.addAttribute("error",
                    "Сумма вклада слишком велика для пролонгации: " +accounts.getAmount() + " > " + deposit.getMaxAmount());
            logger.warn("Сумма вклада слишком велика для пролонгации: {} > {}",accounts.getAmount() ,deposit.getMaxAmount());
            return "user/profile";
        }
        clientDeposit.setCloseDate(closeDate);
        clientDepositService.save(clientDeposit);
        return "redirect:/user/profile";
    }

    @PostMapping("deposit/create")
    public String createDeposit(@RequestParam Long idDeposit, @RequestParam Long amount, @RequestParam String currency, @RequestParam Long idAccount,
                                Model model, HttpSession session, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date closeDate) {
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
            model.addAttribute("accounts", accountsService.getOpenAccountsByClient(client));
            return "user/allDepositsUser";
        }
        if (account == null) {
            logger.warn("Счёт не найден для ID: {}", idAccount);
            model.addAttribute("error", "Указанный счёт не найден.");
            List<Deposits> deposits = depositService.getAllDeposits();
            model.addAttribute("deposits", deposits);
            model.addAttribute("accounts", accountsService.getOpenAccountsByClient(client));
            return "user/allDepositsUser";
        }
        if (!account.getCurrency().equals(currency)) {
            logger.warn("Валюта счёта не совпадает с валютой вклада. Пользователь: {}, Счёт ID: {}, Баланс: {} {}, Валюта счёта: {}",
                    client.getLogin(), idAccount, account.getAmount(), account.getCurrency(), currency);
            model.addAttribute("error", "Валюта счёта не совпадает с валютой вклада.");
            List<Deposits> deposits = depositService.getAllDeposits();
            model.addAttribute("deposits", deposits);
            model.addAttribute("accounts", accountsService.getOpenAccountsByClient(client));
            return "user/allDepositsUser";
        }
        if (account.getAmount() < amount) {
            model.addAttribute("error", "Недостаточно средств на счете.");
            List<Deposits> deposits = depositService.getAllDeposits();
            model.addAttribute("deposits", deposits);
            model.addAttribute("accounts", accountsService.getOpenAccountsByClient(client));
            return "user/allDepositsUser";
        }

        if (!account.getCurrency().equals("RUB")) {
            String rub = "RUB";
            Optional<Course> optionalRate =
                    courseRep.findByFromCurrencyAndToCurrency(account.getCurrency(), rub);

            if (optionalRate.isEmpty()) {
                logger.info("Транзакция. Не найден курс из fromCurrency={}, toCurrency={}", account.getCurrency(), rub);
                optionalRate =
                        courseRep.findByFromCurrencyAndToCurrency(rub, account.getCurrency());
                if (optionalRate.isEmpty()) {throw new RuntimeException("Не найден курс из " + account.getCurrency() + " в " + rub);}
            }
            double rate = optionalRate.get().getRate();
            double usdAmount = amount*rate;
            logger.info("rate {}. amount {}" , rate, usdAmount);
            if(usdAmount < deposit.getMinAmount()){
                model.addAttribute("error", "Не удовлетворяет минимальным условиям");
                List<Deposits> deposits = depositService.getAllDeposits();
                model.addAttribute("deposits", deposits);
                model.addAttribute("accounts", accountsService.getOpenAccountsByClient(client));
                return "user/allDepositsUser";
            }
            if(usdAmount > deposit.getMaxAmount()){
                model.addAttribute("error", "Не удовлетворяет максимальным условиям");
                List<Deposits> deposits = depositService.getAllDeposits();
                model.addAttribute("deposits", deposits);
                model.addAttribute("accounts", accountsService.getOpenAccountsByClient(client));
                return "user/allDepositsUser";
            }
        }else{
            logger.info("!!!!!!!amount {}, minamount {}, maxamount {}" , amount, deposit.getMinAmount(), deposit.getMinAmount());
            if(amount < deposit.getMinAmount()){
                model.addAttribute("error", "Не удовлетворяет минимальным условиям");
                List<Deposits> deposits = depositService.getAllDeposits();
                model.addAttribute("deposits", deposits);
                model.addAttribute("accounts", accountsService.getOpenAccountsByClient(client));
                return "user/allDepositsUser";
            }
            if(amount > deposit.getMaxAmount()){
                model.addAttribute("error", "Не удовлетворяет максимальным условиям");
                List<Deposits> deposits = depositService.getAllDeposits();
                model.addAttribute("deposits", deposits);
                model.addAttribute("accounts", accountsService.getOpenAccountsByClient(client));
                return "user/allDepositsUser";
            }
        }

        LocalDate today = LocalDate.now();
        LocalDate close = closeDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        long monthsBetween = ChronoUnit.MONTHS.between(today.withDayOfMonth(1), close.withDayOfMonth(1));
        if (monthsBetween < deposit.getMinTermDays() || monthsBetween > deposit.getMaxTermDays()) {
            model.addAttribute("error", String.format(
                    "Срок вклада должен быть от %d до %d месяцев. Вы выбрали: %d",
                    deposit.getMinTermDays(), deposit.getMaxTermDays(), monthsBetween));
            model.addAttribute("deposits", depositService.getAllDeposits());
            model.addAttribute("accounts", accountsService.getOpenAccountsByClient(client));
            return "user/allDepositsUser";
        }

        Accounts newAccount = new Accounts();
        newAccount.setIdClient(client);
        newAccount.setAmount(0);
        newAccount.setCurrency(currency);
        newAccount.setStatus("od");
        accountsRep.save(newAccount);
        logger.info("Создался счет: {}.", newAccount);

        ClientDeposit clientDeposit = new ClientDeposit();
        clientDeposit.setDepositStatus("o");
        clientDeposit.setInitialAmount(amount);
//        clientDeposit.setTimeInDays(deposit.getMaxTermDays());

        clientDeposit.setOpenDate(new Date());
        clientDeposit.setCloseDate(closeDate);
        clientDeposit.setIdAccount(newAccount);
        clientDeposit.setIdDeposit(deposit);
        logger.info("Создался ClientDeposit: {} с idAcc: {}, idDep: {}.", clientDeposit, newAccount.getIdAccount(), deposit.getIdDeposit());
        clientDepositService.save(clientDeposit);

//        account.setAmount(account.getAmount() - amount);
//        newAccount.setAmount(amount);
//        accountsRep.save(newAccount);
//        accountsRep.save(account);
//
//        logger.info("Сумма {} списана со счёта {}.", amount, idAccount);
        Transaction transaction = new Transaction();
        transactionService.createDeposit(account.getIdAccount(), newAccount.getIdAccount(), amount);


        return "redirect:/user/allDepositsUser";
    }
    @PostMapping("closeDeposit/{id}")
    public String closeDeposit(@PathVariable("id") Long depositId,
                               @RequestParam("accountId") Long accountId,
                               @RequestParam("targetAccountId") Long targetAccountId,
                               Model model) {
        ClientDeposit clientDeposit = clientDepositService.findById(depositId);
        List<Accounts> accounts = accountsService.findAll();

        if (clientDeposit == null || clientDeposit.getDepositStatus().equals("c")) {
            model.addAttribute("errorId", accountId);
            model.addAttribute("errorMessage", "Невозможно закрыть депозит: он уже закрыт или не найден.");
            return "user/profile";
        }
        Accounts depositAccount = clientDeposit.getIdAccount();
        if (depositAccount == null) {
            model.addAttribute("errorMessage", "Ошибка: у вклада нет привязанного счета.");
            return "user/profile";
        }
        Clients client = depositAccount.getIdClient();
        if (client == null) {
            model.addAttribute("errorMessage", "Ошибка: не найден владелец вклада.");
            return "user/profile";
        }
        List<Accounts> clientAccounts = accountsService.findAllByClientId(client);

        Accounts targetAccount = accountsService.findById(targetAccountId);
//        logger.info("accountId={}, targetAccount={}", accountId, targetAccount.getIdAccount());
//        Accounts targetAccount = clientAccounts.stream()
//                .filter(acc -> acc.getIdAccount().equals(targetAccountId))
//                .findFirst()
//                .orElse(null);

        if (targetAccount == null) {
            model.addAttribute("errorMessage", "Ошибка: у клиента нет активных счетов для перевода денег.");
            return "user/profile";
        }

        Deposits deposit = depositService.findByIdDeposit(clientDeposit.getIdDeposit().getIdDeposit());
        LocalDate openDate = clientDeposit.getOpenDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate now = LocalDate.now();
        long monthsPassed = ChronoUnit.MONTHS.between(openDate, now);
        Accounts acc = accountsService.findById(clientDeposit.getIdAccount().getIdAccount());
        double amount;
        if(clientDeposit.getDepositStatus().equals("ac")){
//            amount = clientDeposit.getInitialAmount() + acc.getAmount() +  Math.round(monthsPassed * deposit.getRate() / 100.0);
            amount = acc.getAmount() +  Math.round(monthsPassed * deposit.getRate() / 100.0);
        }else{
//            amount = clientDeposit.getInitialAmount() + acc.getAmount() +  Math.round(monthsPassed * 0.01);
            amount = acc.getAmount() +  Math.round(monthsPassed * 0.01);
        }
        logger.info("Закрытие депозита. accountId={}, targetAccount={}, amount={}, opendate={}, datenow={}, MONTHS={}", accountId, targetAccount.getIdAccount(), amount, openDate, now, monthsPassed);
        transactionService.closeDeposit(accountId, targetAccount.getIdAccount(), amount);

        clientDeposit.setDepositStatus("c");
        clientDepositService.save(clientDeposit);
        Accounts accounts1 = accountsService.findById(accountId);
        accounts1.setStatus("c");
        accountsService.save(accounts1);
//        model.addAttribute("accounts", accounts);
//        model.addAttribute("clientDeposits", getClientDepositsMap(accounts));

        return "redirect:/user/profile";
    }
}
