package org.site.BoU;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.BoU.Controllers.AccountController;
import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Repositories.AccountsRep;
import org.site.BoU.Services.AccountsService;
import org.site.BoU.Services.ClientDepositService;
import org.site.BoU.Services.ClientService;
import org.site.BoU.Services.TransactionService;
import org.springframework.ui.Model;
import org.springframework.mock.web.MockHttpSession;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @InjectMocks
    private AccountController controller;

    @Mock
    private AccountsRep accountsRep;
    @Mock
    private ClientService clientService;
    @Mock
    private AccountsService accountsService;
    @Mock
    private ClientDepositService clientDepositService;
    @Mock
    private TransactionService transactionService;

    private Model model;
    private MockHttpSession session;

    @BeforeEach
    void setup() {
        model = mock(Model.class);
        session = new MockHttpSession();
    }

    @Test
    void createAccount_shouldSaveNewAccountAndRedirect() {
        session.setAttribute("login", "john");
        Clients client = new Clients();
        client.setLogin("john");
        when(clientService.findByLogin("john")).thenReturn(client);

        String view = controller.createAccount("USD", model, session);

        assertThat(view).isEqualTo("redirect:/user/profile");
        ArgumentCaptor<Accounts> cap = ArgumentCaptor.forClass(Accounts.class);
        verify(accountsRep).save(cap.capture());
        Accounts saved = cap.getValue();
        assertThat(saved.getCurrency()).isEqualTo("USD");
        assertThat(saved.getAmount()).isEqualTo(1000L);
        assertThat(saved.getStatus()).isEqualTo("o");
        assertThat(saved.getIdClient()).isSameAs(client);

        verify(model).addAttribute("clients", client);
        verify(model).addAttribute("accounts", saved);
    }

    @Test
    void deleteAccount_whenAccountNotFound_shouldRedirectWithError() {
        session.setAttribute("login", "john");
        when(accountsService.findById(1L)).thenReturn(null);
        String view = controller.deleteAccount(1L, 2L, model, session);
        assertThat(view).isEqualTo("redirect:/user/profile");
        verify(model).addAttribute("error", "Счет не найден.");
        verifyNoMoreInteractions(transactionService, accountsService);
    }

    @Test
    void deleteAccount_whenTargetNotFound_shouldRedirectWithError() {
        Accounts acc = new Accounts();
        acc.setStatus("o");
        acc.setAmount(0L);
        when(accountsService.findById(1L)).thenReturn(acc);
        when(accountsService.findById(2L)).thenReturn(null);

        String view = controller.deleteAccount(1L, 2L, model, session);

        assertThat(view).isEqualTo("redirect:/user/profile");
        verify(model).addAttribute("error", "Не выбран счёт для перевода остатка.");
        verify(accountsService).findById(1L);
        verify(accountsService).findById(2L);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    void deleteAccount_whenValidAccountsAndPositiveBalance_shouldTransferAndClose() {
        Accounts acc = new Accounts();
        acc.setIdAccount(1L);
        acc.setStatus("o");
        acc.setAmount(500L);

        Accounts target = new Accounts();
        target.setIdAccount(2L);

        when(accountsService.findById(1L)).thenReturn(acc);
        when(accountsService.findById(2L)).thenReturn(target);
        when(transactionService.transferMoney(1L, 2L, 500.0)).thenReturn(null);

        String view = controller.deleteAccount(1L, 2L, model, session);

        assertThat(view).isEqualTo("redirect:/user/profile");

        verify(transactionService).transferMoney(1L, 2L, 500.0);

        assertThat(acc.getStatus()).isEqualTo("c");
        verify(accountsService).save(acc);
    }
}
