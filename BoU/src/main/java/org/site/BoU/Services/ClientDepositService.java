package org.site.BoU.Services;

import org.site.BoU.Entities.ClientDeposit;
import org.site.BoU.Repositories.ClientsDepositsRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientDepositService {

    @Autowired
    private ClientsDepositsRep clientDepositRepository;

    public ClientDeposit save(ClientDeposit clientDeposit) {
        return clientDepositRepository.save(clientDeposit);
    }
    public ClientDeposit findByAccountId(Long idAccount){
        return clientDepositRepository.findByAccountId(idAccount);
    }
    public ClientDeposit findById(Long idCD){
        return clientDepositRepository.findByIdCD(idCD);
    }

}

