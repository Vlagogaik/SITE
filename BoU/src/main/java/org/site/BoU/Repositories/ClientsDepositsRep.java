package org.site.BoU.Repositories;

import org.site.BoU.Entities.ClientDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientsDepositsRep extends JpaRepository<ClientDeposit, Long> {

}
