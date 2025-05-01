package org.site.BoU.Services;

import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.ClientDeposit;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Repositories.ClientsDepositsRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientDepositService {

    @Autowired
    private ClientsDepositsRep clientDepositRepository;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private ClientService clientService;

    public ClientDeposit save(ClientDeposit clientDeposit) {
        return clientDepositRepository.save(clientDeposit);
    }
    public ClientDeposit findByAccountId(Long idAccount){
        return clientDepositRepository.findByAccountIdOnec(idAccount);
    }
    public ClientDeposit findById(Long idCD){
        return clientDepositRepository.findByIdCD(idCD);
    }
    public List<ClientDeposit> findAllByClientLogin(String idClient){
//        Accounts acc = accountsService.findById(idAccount);
        Clients client = clientService.findByLogin(idClient);
        List<Accounts> accounts = accountsService.getDepositAccountsByClient(client);
        List<ClientDeposit> clientaccounts = clientDepositRepository.findAll();
        return clientDepositRepository.findAll().stream()
                .filter(cd -> cd.getIdAccount().getIdClient().equals(client))
                .filter(cd -> {
                    String status = cd.getDepositStatus();
                    return "o".equals(status) || "ac".equals(status);
                })
                .collect(Collectors.toList());
    }
}

