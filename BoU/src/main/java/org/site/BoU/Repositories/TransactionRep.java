package org.site.BoU.Repositories;

import org.site.BoU.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRep extends JpaRepository<Transaction, Long> {

}
