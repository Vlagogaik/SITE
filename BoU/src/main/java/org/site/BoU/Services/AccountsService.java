package org.site.BoU.Services;

import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Repositories.AccountsRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountsService {

    @Autowired
    private AccountsRep accountsRep;

    public List<Accounts> getAccountsByClient(Clients client) {
        return accountsRep.findByIdClient(client);
    }
    public List<Accounts> findAll() {
        return accountsRep.findAll();
    }
    public void deleteById(Long id){
        accountsRep.deleteById(id);
    }

}
