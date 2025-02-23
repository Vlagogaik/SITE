package org.site.BoU.Services;

import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Repositories.AccountsRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AccountsService {

    @Autowired
    private AccountsRep accountsRep;

    public List<Accounts> getAccountsByClient(Clients client) {
        List<Accounts> accounts = accountsRep.findByIdClient(client);
        return accounts.stream()
                .filter(account -> !"od".equals(account.getStatus()))
                .collect(Collectors.toList());
    }
    public List<Accounts> getDepositAccountsByClient(Clients client) {
        List<Accounts> accounts = accountsRep.findByIdClient(client);
        return accounts.stream()
                .filter(account -> "od".equals(account.getStatus()))
                .collect(Collectors.toList());
    }
    public boolean isAccount(Accounts accounts){
        if(Objects.equals(accounts.getStatus(), "od")){
            return false;
        } else{
            return true;
        }
    }
    public List<Accounts> findAll() {
        return accountsRep.findAll();
    }
    public void deleteById(Long id){
        accountsRep.deleteById(id);
    }
    public Accounts findById(Long id){
        return accountsRep.findByidAccount(id);
    }

}
