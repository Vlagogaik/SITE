package org.site.BoU.Repositories;

import org.site.BoU.Entities.ClientDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClientsDepositsRep extends JpaRepository<ClientDeposit, Long> {
    @Query("SELECT cd FROM ClientDeposit cd WHERE cd.idAccount.idAccount = :idAccount")
    ClientDeposit findByAccountId(@Param("idAccount") Long idAccount);
    void deleteById(Long id);
    ClientDeposit findByIdCD(Long idCD);
}
