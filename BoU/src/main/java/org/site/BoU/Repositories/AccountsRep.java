package org.site.BoU.Repositories;

import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountsRep extends JpaRepository<Accounts, Long> {
    List<Accounts> findByIdClient(Clients client);
    void deleteById(Long idAccount);
    List<Accounts> findAll();
//    Optional<Accounts> findByIdAccounts(Accounts account);

}
