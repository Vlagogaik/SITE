package org.site.BoU.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.Objects;

@RequestMapping("/clients")
@Controller
public class RegistrationContr {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationContr.class);
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientService clientService;

    @PostMapping("register")
    public String save(@ModelAttribute("clients") @Valid Clients client, BindingResult bindingResult, Model model, HttpSession httpSession) {
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
                return "register";
            }
        } else {
            logger.error("Ошибка при регистрации пользователя {}", client.getLogin());
            return "register";
        }
    }
    @PostMapping("signIn")
    public String sign(@ModelAttribute("clients") Clients client, Model model, HttpServletRequest request, HttpSession httpSession) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(client.getLogin(), client.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.info("Аутентификация прошла успешно. Роль: {}; {}", authentication.getAuthorities(), authentication);
            logger.info("Текущие аутентифицированные данные: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            httpSession.setAttribute("login", client.getLogin());
            httpSession.setAttribute("lastAccessed", LocalDateTime.now());

            logger.info("Cессия при входе: id: {}; login: {}", httpSession.getId(), httpSession.getAttribute("login"));


            if(Objects.equals(client.getRole(), "ADMIN")){
                return  "redirect:/admin/home_admin";
            }else{
                return "redirect:/user/profile";
            }

        } catch (BadCredentialsException e) {
            logger.warn("Неудачная попытка входа для пользователя {}", client.getLogin());
            model.addAttribute("error", "Неверный логин или пароль.");
            model.addAttribute("error", "Неправильный логин или пароль");
            return "signIn";
        } catch (Exception e) {
            logger.error("Ошибка при входе пользователя {}: {}", client.getLogin(), e.getMessage());
            model.addAttribute("error", "Неверный логин или пароль, а может проблема у нас :).");
            model.addAttribute("error", "Произошла ошибка при входе.");
            return "signIn";
        }
    }
}
