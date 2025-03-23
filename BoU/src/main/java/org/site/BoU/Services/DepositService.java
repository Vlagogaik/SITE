package org.site.BoU.Services;

import org.site.BoU.Entities.Deposits;
import org.site.BoU.Repositories.DepositsRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DepositService {
    @Autowired
    DepositsRep depositsRep;

    public Deposits save(Deposits deposit) {
        return depositsRep.save(deposit);
    }
    public boolean  existsByName(Deposits deposit){
        return depositsRep.existsByName(deposit.getName());
    }
    public List<Deposits> getAllDeposits() {
        return depositsRep.findAll();
    }
    public Deposits findByIdDeposit(Long idDeposit){
        return depositsRep.findByIdDeposit(idDeposit);
    }
    public void deleteById(Long idDeposit){
        depositsRep.deleteById(idDeposit);
    }
}
