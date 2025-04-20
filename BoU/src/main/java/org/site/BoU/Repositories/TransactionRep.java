package org.site.BoU.Repositories;

import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRep extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByFromAccountIn(List<Accounts> accounts);
    List<Transaction> findAllByToAccountIn(List<Accounts> accounts);
    Optional<Transaction> findById(Long id);
}
