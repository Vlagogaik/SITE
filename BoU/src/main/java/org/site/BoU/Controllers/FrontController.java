package org.site.BoU.Controllers;

import jakarta.servlet.http.HttpSession;
import org.site.BoU.Entities.*;
import org.site.BoU.Services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/")
public class FrontController {
    private static final Logger logger = LoggerFactory.getLogger(FrontController.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientDepositService clientDepositService;

    @Autowired
    private AccountsService accountService;
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private DepositService depositService;
    @RequestMapping("admin/delete")
    public String depDel(Model model, HttpSession session) {
        List<Deposits> deposits = depositService.getAllDeposits();
        model.addAttribute("deposits", deposits);
        return "admin/delete";
    }
    @RequestMapping("admin/accountAdd")
    public String accAdd(Model model, HttpSession session) {

        return "admin/accountAdd";
    }

    @RequestMapping("user/transactions")
    public String transactions(Model model, HttpSession session) {
        List<Accounts> accounts = accountService.findAll();
        String login = (String) session.getAttribute("login");
        model.addAttribute("clients", clientService.findByLogin(login));

        Clients client = clientService.findByLogin(login);
        List<Accounts> clientAccounts = accountService.findAllByClientId(client);

        List<Accounts> availableСAccounts = clientAccounts.stream()
                .filter(acc -> "o".equals(acc.getStatus()))
                .collect(Collectors.toList());
        model.addAttribute("clientAccounts", availableСAccounts);

        if (login != null) {
            if(client.getRole().equals("ADMIN")){
                return "redirect:/admin/home_admin";
            }else{
                return "user/transactions";
            }
        } else {
            return "redirect:/signIn";
        }
    }
    @RequestMapping("user/transfer-success")
    public String transferSuccess(@RequestParam Long id, Model model, HttpSession session){
        Optional<Transaction> optionalTransaction = transactionService.findById(id);
        if (optionalTransaction.isEmpty()) {
            model.addAttribute("error", "Транзакция не найдена.");
            return "redirect:/user/transfer-fail";
        }
        Transaction transaction = optionalTransaction.get();
        logger.info("transaction: {}", transaction);
        model.addAttribute("transaction", transaction);
        return "transfer-success";
    }
    @RequestMapping("user/transfer-fail")
    public String transferFail(Model model, HttpSession session){
        return "user/transfer-fail";
    }
    @RequestMapping("user/closeDeposit")
    public String accDelUsr(Model model, HttpSession session) {
        List<Accounts> accounts = accountService.findAll();
        String login = (String) session.getAttribute("login");
        model.addAttribute("clients", clientService.findByLogin(login));
        model.addAttribute("accounts", accounts);
        Map<Long, ClientDeposit> clientDepositsMap = new HashMap<>();
        Map<Long, List<Accounts>> availableAccountsMap = new HashMap<>();
        for (Accounts account : accounts) {
            ClientDeposit clientDeposit = clientDepositService.findByAccountId(account.getIdAccount());
            if (clientDeposit != null) {
                clientDepositsMap.put(account.getIdAccount(), clientDeposit);
                Clients owner = account.getIdClient();
                if ((owner != null)) {
                    List<Accounts> ownerAccounts = accountService.findAllByClientId(owner);
                    List<Accounts> availableAccounts = ownerAccounts.stream()
                            .filter(acc -> !acc.getIdAccount().equals(account.getIdAccount()))
                            .filter(acc -> "o".equals(acc.getStatus()))
                            .collect(Collectors.toList());
                    availableAccountsMap.put(account.getIdAccount(), availableAccounts);
                }
            }
        }
        model.addAttribute("clientDeposits", clientDepositsMap);
        model.addAttribute("availableAccountsMap", availableAccountsMap);
        logger.info("availableAccountsMap: {}", availableAccountsMap);
//        logger.info("Доступные счета для закрытия вклада: {}", availableAccountsMap);
        return "user/closeDeposit";
    }

    @RequestMapping("admin/accountDel")
    public String accDel(Model model, HttpSession session) {
        List<Accounts> accounts = accountService.findAll();
        String login = (String) session.getAttribute("login");
        model.addAttribute("clients", clientService.findByLogin(login));
        model.addAttribute("accounts", accounts);
//        model.addAttribute("clientDeposits", clientDeposits);
        Map<Long, ClientDeposit> clientDepositsMap = new HashMap<>();
        Map<Long, List<Accounts>> availableAccountsMap = new HashMap<>();
        for (Accounts account : accounts) {
            ClientDeposit clientDeposit = clientDepositService.findByAccountId(account.getIdAccount());
            if (clientDeposit != null) {
                clientDepositsMap.put(account.getIdAccount(), clientDeposit);
                Clients owner = account.getIdClient();
                if ((owner != null)) {
                    List<Accounts> ownerAccounts = accountService.findAllByClientId(owner);
                    List<Accounts> availableAccounts = ownerAccounts.stream()
                            .filter(acc -> !acc.getIdAccount().equals(account.getIdAccount()))
                            .filter(acc -> "o".equals(acc.getStatus()))
                            .collect(Collectors.toList());
                    availableAccountsMap.put(account.getIdAccount(), availableAccounts);
                }
            }
        }
        model.addAttribute("clientDeposits", clientDepositsMap);
        model.addAttribute("availableAccountsMap", availableAccountsMap);
        logger.info("availableAccountsMap: {}", availableAccountsMap);
        return "admin/accountDel";
    }
    @RequestMapping("admin/clientDel")
    public String clientDel(Model model, HttpSession session) {
        List<Clients> client = clientService.findAll();
        model.addAttribute("clients", client);
        return "admin/clientDel";
    }
    @RequestMapping("admin/clientAdd")
    public String clientAdd(Model model, HttpSession session) {
        model.addAttribute("clients", new Clients());
        return "admin/clientAdd";
    }
    @RequestMapping("admin/topUp")
    public String topUp(Model model, HttpSession session) {
        return "admin/topUp";
    }
    @GetMapping("allDeposits")
    public String getDeposits(Model model, HttpSession session) {
        String login = (String) session.getAttribute("login");
        if (login != null) {
            return "redirect:/user/allDepositsUser";
        } else {
            List<Deposits> deposits = depositService.getAllDeposits();
            List<Deposits> availableDep =deposits.stream()
                    .filter(dep -> "o".equals(dep.getDepositStatus()))
                    .collect(Collectors.toList());
            model.addAttribute("deposits", availableDep);
            return "allDeposits";
        }
    }
    @GetMapping("user/allDepositsUser")
    public String getDepositsUser(Model model, HttpSession session) {
        String login = (String) session.getAttribute("login");
        if (login != null) {
            Clients client = clientService.findByLogin(login);
            List<Deposits> deposits = depositService.getAllDeposits();
            List<Deposits> availableDep =deposits.stream()
                    .filter(dep -> "o".equals(dep.getDepositStatus()))
                    .collect(Collectors.toList());
            model.addAttribute("deposits", availableDep);
            List<Accounts> accounts = accountService.getOpenAccountsByClient(client);
            model.addAttribute("deposits", deposits);
            model.addAttribute("accounts", accounts);
            return "user/allDepositsUser";
        } else {
            List<Deposits> deposits = depositService.getAllDeposits();
            List<Deposits> availableDep =deposits.stream()
                    .filter(dep -> "o".equals(dep.getDepositStatus()))
                    .collect(Collectors.toList());
            model.addAttribute("deposits", availableDep);
            model.addAttribute("deposits", deposits);
            return "redirect:/allDeposits";
        }
    }

    @RequestMapping("")
    public String start(HttpSession session) {
        return "home";
    }
    @RequestMapping("cards")
    public String cards(Model model, HttpSession session) {
        String login = (String) session.getAttribute("login");
        if (login != null) {
            Clients client = clientService.findByLogin(login);
            model.addAttribute("clients", client);
        }
        return "cards";
    }
    @RequestMapping("home")
    public String home(HttpSession session) {
        return "home";
    }
    @RequestMapping("admin/home_admin")
    public String homeAdmin(Model model, @AuthenticationPrincipal Clients client, HttpSession session) {
        return "admin/home_admin";
    }
    @RequestMapping("admin/deposit")
    public String depositCreate(Model model) {
        model.addAttribute("deposits", new Deposits());
        return "admin/deposit";
    }
    @RequestMapping("account/create")
    public String accountCreate(Model model, HttpSession session) {
        String login = (String) session.getAttribute("login");
        if (login != null) {
            Clients client = clientService.findByLogin(login);
            model.addAttribute("clients", client);

            return "account/create";
        } else {
            return "redirect:/user/profile";
        }
    }

    @RequestMapping("user/deposit/create")
    public String userDepositCreate(Model model, HttpSession session, @RequestParam Long depositId, @RequestParam Long amount, @RequestParam String currency,
                                    @RequestParam Long accountId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date closeDate) {
        String login = (String) session.getAttribute("login");
        if (login != null) {
            Clients client = clientService.findByLogin(login);
            model.addAttribute("clients", client);
            model.addAttribute("depositId", depositId);
            model.addAttribute("accounts", accountService.getAccountsByClient(client));
            return "user/deposit/create";
        }else{
            return "allDeposits";
        }
    }
    @RequestMapping("user/profile")
    public String profile(Model model, HttpSession session) {
        if (!model.containsAttribute("activeSection")) {
            model.addAttribute("activeSection", "personal");
        }
        String login = (String) session.getAttribute("login");
        if (login != null) {
            Clients client = clientService.findByLogin(login);
            List<Accounts> accounts = accountService.getOpenAccountsByClient(client);

            List<ClientDeposit> clientDepositsMap = clientDepositService.findAllByClientLogin(login);

            model.addAttribute("accounts", accounts);
            model.addAttribute("depaccounts", clientDepositsMap);
            model.addAttribute("client", client);

            List<Transaction> transactions = transactionService.findAllByClient(client);
            model.addAttribute("transactions", transactions);
            if(client.getRole().equals("ADMIN")){
                return "redirect:/admin/home_admin";
            }else{
                return "user/profile";
            }
        } else {
            return "redirect:/signIn";
        }
    }
    @RequestMapping("register")
    public String regIn(Model model, HttpSession session) {
        model.addAttribute("clients", new Clients());
        return "register";
    }

    @RequestMapping("signIn")
    public String signIn(Model model, @AuthenticationPrincipal Clients client, HttpSession session) {
        if (client != null) {
            return "redirect:/signIn";
        }
        model.addAttribute("clients", new Clients());
        return "signIn";
    }

}
