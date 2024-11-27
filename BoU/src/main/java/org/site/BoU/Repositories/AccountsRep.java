package org.site.BoU.Repositories;

import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountsRep extends JpaRepository<Accounts, Long> {
//    Optional<Accounts> findByIdClient(Clients id_client);
//    Optional<Accounts> findByIdAccounts(Accounts account);
}
