package org.site.BoU.Controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.site.BoU.Entities.Deposits;
import org.site.BoU.Repositories.DepositsRep;
import org.site.BoU.Services.AccountsService;
import org.site.BoU.Services.ClientService;
import org.site.BoU.Services.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    DepositService depositService;
    @Autowired
    AccountsService accountsService;
    @Autowired
    ClientService clientService;

    private static final Logger logger = LoggerFactory.getLogger(RegistrationContr.class);

    @PostMapping("/account/delete/{id}")
    public String deleteAccount(@PathVariable Long id, Model model, HttpSession session) {
        accountsService.deleteById(id);
        return "redirect:/admin/accountDel";
    }

    @PostMapping("/client/delete/{id}")
    public String deleteClient(@PathVariable Long id, Model model, HttpSession session) {
        clientService.deleteById(id);
        return "redirect:/admin/clientDel";
    }

    @PostMapping("/deposit")
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
                return "/admin/deposit";
            }
            depositService.save(deposit);
            model.addAttribute("successMessage", "Вклад успешно создан.");
            logger.info("Вклад создан: {}", deposit);
            return "redirect:/admin/home_admin";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при создании вклада.");
            return "/admin/deposit";
        }
    }
    @PostMapping("/deposit/delete/{id}")
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
