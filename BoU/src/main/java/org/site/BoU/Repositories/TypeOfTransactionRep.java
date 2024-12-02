package org.site.BoU.Repositories;

import org.site.BoU.Entities.TypeOfTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeOfTransactionRep extends JpaRepository<TypeOfTransaction, Long> {
}
