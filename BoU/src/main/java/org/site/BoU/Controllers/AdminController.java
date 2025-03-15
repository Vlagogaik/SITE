package org.site.BoU.Controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.ClientDeposit;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Entities.Deposits;
import org.site.BoU.Repositories.DepositsRep;
import org.site.BoU.Services.AccountsService;
import org.site.BoU.Services.ClientDepositService;
import org.site.BoU.Services.ClientService;
import org.site.BoU.Services.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    DepositService depositService;
    @Autowired
    AccountsService accountsService;
    @Autowired
    ClientService clientService;
    @Autowired
    ClientDepositService clientDepositService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(RegistrationContr.class);

    @PostMapping("closeDeposit/{id}")
    public String closeDeposit(@PathVariable("id") Long depositId,
                               @RequestParam("accountId") Long accountId,
                               Model model) {
        ClientDeposit clientDeposit = clientDepositService.findById(depositId);
        List<Accounts> accounts = accountsService.findAll();

        if (clientDeposit == null || clientDeposit.getDepositStatus().equals("c")) {
            model.addAttribute("errorId", accountId);
            model.addAttribute("errorMessage", "Невозможно закрыть депозит: он уже закрыт или не найден.");
//            model.addAttribute("accounts", accounts);
//            model.addAttribute("clientDeposits", getClientDepositsMap(accounts));
            return "admin/accountDel";
        }

//        Accounts linkedAccount = clientDeposit.getIdAccount();
//        if (linkedAccount != null) {
//            linkedAccount.setAmount(linkedAccount.getAmount() + clientDeposit.getInitialAmount());
//            accountsService.save(linkedAccount);
//        }

        clientDeposit.setDepositStatus("c");
        clientDepositService.save(clientDeposit);

//        model.addAttribute("accounts", accounts);
//        model.addAttribute("clientDeposits", getClientDepositsMap(accounts));

        return "admin/accountDel";
    }


    @PostMapping("account/delete/{id}")
    public String deleteAccount(@PathVariable Long id, Model model, HttpSession session) {
        Accounts acc = accountsService.findById(id);
        ClientDeposit clientDepositOpt = clientDepositService.findByAccountId(acc.getIdAccount());
        if (clientDepositOpt != null) {
            model.addAttribute("account", acc);
            model.addAttribute("clientDeposit", clientDepositOpt);
        }
        if (!accountsService.isAccount(acc)){
            model.addAttribute("errorId", id);
            model.addAttribute("errorMessage", "Невозможно удалить счет: на него ссылается открытый вклад.");
            model.addAttribute("accounts", accountsService.findAll());
            return "admin/accountDel";
        }
        accountsService.deleteById(id);
        return "redirect:/admin/accountDel";
    }

    @PostMapping("client/delete/{id}")
    public String deleteClient(@PathVariable Long id, Model model, HttpSession session) {
        Optional<Clients> optionalClient = clientService.findById(id);

        if (optionalClient.isPresent()) {
            Clients client = optionalClient.get();

            if (clientService.existById(client)) {
                model.addAttribute("errorId", id);
                model.addAttribute("errorMessage", "Невозможно удалить клиента: у него есть счета.");
                model.addAttribute("clients", clientService.findAll());
                return "admin/clientDel";
            }

            clientService.deleteById(id);
            model.addAttribute("success", "Клиент успешно удален.");
            return "redirect:/admin/clientDel";
        } else {

            model.addAttribute("errorMessage", "Клиент не найден.");
            model.addAttribute("clients", clientService.findAll());
            return "admin/clientDel";
        }
    }

    @PostMapping("clientAdd")
    public String clientAdd(@ModelAttribute("clients") @Valid Clients client, BindingResult bindingResult, Model model, HttpSession httpSession) {
        if (!bindingResult.hasErrors()) {
            if (!clientService.existByLogin(client) && !clientService.existByPasport(client) && !clientService.existByNumber(client)) {
                client.setPassword(passwordEncoder.encode(client.getPassword()));
                client.setRole("USER");
                clientService.save(client);
                logger.info("Пользователь {} успешно зарегистрирован", client.getLogin());

                httpSession.setAttribute("login", client.getLogin());
                httpSession.setAttribute("lastAccessed", LocalDateTime.now());
                logger.info("Cессия при регистрации: id: {}; login: {}", httpSession.getId(), httpSession.getAttribute("login"));

                return "redirect:/user/profile";
            } else {
                logger.warn("Ошибка регистрации: пользователь с логином {} или паспортными данными {} или номером телефона {} уже существует", client.getLogin(), client.getNumberPasport(), client.getNumber());
                model.addAttribute("error", "Пользователь с таким логином уже существует или не найден.");
                return "admin/clientAdd";
            }
        } else {
            logger.error("Ошибка при регистрации пользователя {}", client.getLogin());
            return "admin/clientAdd";
        }
    }
    @PostMapping("deposit")
    public String createDeposit(@ModelAttribute("deposits") @Valid Deposits deposit, Model model, BindingResult bindingResult) {

        try {
            if (deposit.getMinTermDays() > deposit.getMaxTermDays()) {
                bindingResult.rejectValue("minTermDays", "error.minTermDays", "Минимальный срок не может быть больше максимального срока.");
            }
            if (deposit.getMinAmount() > deposit.getMaxAmount()) {
                bindingResult.rejectValue("minAmount", "error.minAmount", "Минимальная сумма не может быть больше максимальной суммы.");
            }
            if (depositService.existsByName(deposit)) {
                bindingResult.rejectValue("name", "error.name", "Вклад с таким именем уже существует.");
            }
            if (deposit.getProlongation() == null || deposit.getReplenishmentCapasity() == null || deposit.getDepositStatus() == null) {
                bindingResult.rejectValue("prolongation", "error.prolongation", "Поле пролонгации должно быть заполнено");
                bindingResult.rejectValue("replenishmentCapasity", "error.replenishmentCapasity", "Поле пополнения должно быть заполнено");
                bindingResult.rejectValue("depositStatus", "error.depositStatus", "Поле статуса должно быть заполнено");
            }
            if (bindingResult.hasErrors()) {
                model.addAttribute("deposits", deposit);
                model.addAttribute("errorMessage", "Что-то пошло не так, проверьте данные");
                return "admin/deposit";
            }
            depositService.save(deposit);
            model.addAttribute("successMessage", "Вклад успешно создан.");
            logger.info("Вклад создан: {}", deposit);
            return "redirect:/admin/home_admin";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при создании вклада.");
            return "admin/deposit";
        }
    }
    @PostMapping("deposit/delete/{id}")
    public String deleteDeposit(@PathVariable Long id, Model model, HttpSession session) {
        try {
            depositService.deleteById(id);
            model.addAttribute("successMessage", "Вклад успешно удален.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при удалении вклада.");
        }
        return "redirect:/admin/delete";
    }

}
