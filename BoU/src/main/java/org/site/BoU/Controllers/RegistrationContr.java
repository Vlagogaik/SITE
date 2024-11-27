package org.site.BoU.Controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.site.BoU.Entities.Clients;
import org.site.BoU.JWT.JwtTokenProvider;
import org.site.BoU.Services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.Objects;
import java.util.Optional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

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
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public String save(@ModelAttribute("clients") @Valid Clients client, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            if (!clientService.existByLogin(client) && !clientService.existByPasport(client) && !clientService.existByNumber(client)) {
                client.setPassword(passwordEncoder.encode(client.getPassword()));
                client.setRole("USER");
                clientService.save(client);
                logger.info("Пользователь {} успешно зарегистрирован", client.getLogin());
                return "/user/profile";
            } else {
                logger.warn("Ошибка регистрации: пользователь с логином {} или паспортными данными {} или номером телефона {} уже существует", client.getLogin(), client.getNumberPasport(), client.getNumber());
                model.addAttribute("error", "Пользователь с таким логином уже существует или не найден.");
                return "/register";
            }
        } else {
            logger.error("Ошибка при регистрации пользователя {}", client.getLogin());
            return "/register";
        }
    }
    @PostMapping("/signIn")
//    public String sign(@ModelAttribute("clients") Clients client, Model model, HttpServletResponse response) {
    public String sign(@ModelAttribute("clients") Clients client, HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(client.getLogin(), client.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Optional<Clients> optionalClient = clientService.findByLogin(client);
            client = optionalClient.get();
            logger.info("Пользователь {} успешно вошел в систему, роль: {}", client.getLogin(), client.getRole());
//            String role = client.getRole();
//            HttpSession session = request.getSession();
//            session.setAttribute("USER_ROLE", role);

            String token = jwtTokenProvider.generateToken(client.getLogin(), client.getRole());
            Cookie cookie = new Cookie("JWT", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);

            if(Objects.equals(client.getRole(), "ADMIN")){
                return  "/admin/home_admin";
            }else{
                return "/user/profile";
            }

        } catch (BadCredentialsException e) {
            logger.warn("Неудачная попытка входа для пользователя {}", client.getLogin());
            model.addAttribute("error", "Неправильный логин или пароль");
            return "signIn";
        } catch (Exception e) {
            logger.error("Ошибка при входе пользователя {}: {}", client.getLogin(), e.getMessage());
            model.addAttribute("error", "Произошла ошибка при входе.");
            return "signIn";
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/profile")
    public String profile(@AuthenticationPrincipal Clients client, Model model) {
        if (client != null) {
            model.addAttribute("login", client.getLogin());
            return "profile";
        } else {
            return "redirect:/signIn";
        }
    }

}
