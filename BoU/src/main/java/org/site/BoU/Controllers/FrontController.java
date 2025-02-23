package org.site.BoU.Controllers;

import jakarta.servlet.http.HttpSession;
import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.ClientDeposit;
import org.site.BoU.Entities.Deposits;
import org.site.BoU.Services.AccountsService;
import org.site.BoU.Services.ClientDepositService;
import org.site.BoU.Services.ClientService;
import org.site.BoU.Services.DepositService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.site.BoU.Entities.Clients;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/")
public class FrontController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationContr.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientDepositService clientDepositService;

    @Autowired
    private AccountsService accountService;

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

    @RequestMapping("admin/accountDel")
    public String accDel(Model model, HttpSession session) {
        List<Accounts> accounts = accountService.findAll();
        String login = (String) session.getAttribute("login");
        model.addAttribute("clients", clientService.findByLogin(login));
        model.addAttribute("accounts", accounts);
        for (Accounts account : accounts) {
            ClientDeposit clientDeposit = clientDepositService.findByAccountId(account.getIdAccount());
            if (clientDeposit != null) {
                model.addAttribute("clientDeposit" + account.getIdAccount(), clientDeposit);
            }
        }
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
    @GetMapping("allDeposits")
    public String getDeposits(Model model, HttpSession session) {
        String login = (String) session.getAttribute("login");
        if (login != null) {
            Clients client = clientService.findByLogin(login);
            List<Deposits> deposits = depositService.getAllDeposits();
            List<Accounts> accounts = accountService.getAccountsByClient(client);

            model.addAttribute("deposits", deposits);
            model.addAttribute("accounts", accounts);
        } else {
            List<Deposits> deposits = depositService.getAllDeposits();
            model.addAttribute("deposits", deposits);
            return "allDeposits";
        }
        return "allDeposits";
    }

    @RequestMapping("")
    public String start(HttpSession session) {
        return "home";
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

    @RequestMapping("/user/deposit/create")
    public String userDepositCreate(Model model, HttpSession session, @RequestParam Long depositId, @RequestParam Long amount, @RequestParam String currency,
                                    @RequestParam Long accountId) {
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
        String login = (String) session.getAttribute("login");
        if (login != null) {
            Clients client = clientService.findByLogin(login);
            if (client != null) {
                model.addAttribute("clients", client);
                model.addAttribute("accounts", accountService.getAccountsByClient(client));
                model.addAttribute("depaccounts", accountService.getDepositAccountsByClient(client));
            } else {
                model.addAttribute("error", "Client not found.");
            }
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
