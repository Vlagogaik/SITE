package org.site.BoU.Repositories;

import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountsRep extends JpaRepository<Accounts, Long> {
    List<Accounts> findByIdClient(Clients client);

    Accounts findByidAccount(Long idAccount);

    void deleteById(Long idAccount);

    List<Accounts> findAll();
//    Optional<Accounts> findByIdAccounts(Accounts account);

}
