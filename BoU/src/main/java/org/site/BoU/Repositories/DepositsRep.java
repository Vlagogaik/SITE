package org.site.BoU.Repositories;

import org.site.BoU.Entities.Deposits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepositsRep extends JpaRepository<Deposits, Long> {
    Optional<Deposits> findByName(String name);
    Deposits findByIdDeposit(Long idDeposit);
    boolean existsByName(String name);
    void deleteById(Long idDeposit);
}
