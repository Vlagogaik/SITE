package org.site.BoU.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.site.BoU.Controllers.AdminController;
import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Entities.Transaction;
import org.site.BoU.Entities.TypeOfTransaction;
import org.site.BoU.Repositories.AccountsRep;
import org.site.BoU.Repositories.TransactionRep;
import org.site.BoU.Repositories.TypeOfTransactionRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final Long course = 100L;
    private final AccountsRep accountRepository;
    private final TransactionRep transactionRepository;
    private final TypeOfTransactionRep typeOfTransactionRep;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);


    public List<Transaction> findAllByClient(Clients clients){
        List<Accounts> accounts = accountRepository.findByIdClient(clients);

        List<Transaction> fromAccounts = transactionRepository.findAllByFromAccountIn(accounts);
        List<Transaction> toAccounts = transactionRepository.findAllByToAccountIn(accounts);
        Set<Transaction> allTransactions = new HashSet<>();
        allTransactions.addAll(fromAccounts);
        allTransactions.addAll(toAccounts);

        List<Transaction> sortedTransactions = new ArrayList<>(allTransactions);
        sortedTransactions.sort((t1, t2) -> t2.getTrDate().compareTo(t1.getTrDate()));

        return sortedTransactions;
    }
    @Transactional
    public void createDeposit(Long fromAccountId, Long toAccountId, Long amount) {
        Accounts fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет отправителя не найден"));

        Accounts toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет получателя не найден"));

        if (fromAccount.getAmount() < amount) {
            throw new IllegalStateException("Недостаточно средств на счете");
        }

        logger.info("Транзакция. Открытие Вклада. fromAccount={}, toAccount={}, amount={}, ", fromAccount.getIdAccount(), toAccount.getIdAccount(), amount);
        fromAccount.setAmount(fromAccount.getAmount() - amount);
        accountRepository.save(fromAccount);
        toAccount.setAmount(toAccount.getAmount() + amount);
        accountRepository.save(toAccount);

        TypeOfTransaction transferType = typeOfTransactionRep.findById(3L)
                .orElseThrow(() -> new IllegalArgumentException("Тип транзакции не найден"));

        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTrAmount(amount);
        transaction.setTrDate(new Date());
        transaction.setIdTransaction(transferType);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void transferMoney(Long fromAccountId, Long toAccountId, Long amount) {
        Accounts fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет отправителя не найден"));

        Accounts toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет получателя не найден"));

        if (fromAccount.getAmount() < amount) {
            throw new IllegalStateException("Недостаточно средств на счете");
        }

        if ((!Objects.equals(fromAccount.getCurrency(), toAccount.getCurrency()))) {
            if((Objects.equals(fromAccount.getCurrency(), "USD") && Objects.equals(toAccount.getCurrency(), "EUR") || (Objects.equals(toAccount.getCurrency(), "USD") && Objects.equals(fromAccount.getCurrency(), "EUR")))){
                logger.info("Транзакция. USD and EUR. fromAccount={}, toAccount={}, amount={}, ", fromAccount.getIdAccount(), toAccount.getIdAccount(), amount);
                fromAccount.setAmount(fromAccount.getAmount() - amount);
                accountRepository.save(fromAccount);
                toAccount.setAmount(toAccount.getAmount() + amount);
                accountRepository.save(toAccount);
            }else {
                if ((Objects.equals(fromAccount.getCurrency(), "USD") || Objects.equals(fromAccount.getCurrency(), "EUR")) && (!Objects.equals(fromAccount.getCurrency(), "RUB"))) {
                    fromAccount.setAmount(fromAccount.getAmount() - amount);
                    accountRepository.save(fromAccount);
                    toAccount.setAmount(toAccount.getAmount() + amount * course);
                    accountRepository.save(toAccount);
                    logger.info("Транзакция.  Разные валюты *. fromAccount={}, toAccount={}, amount={}, fromAccamount={}, toAccamount={}", fromAccount.getIdAccount(), toAccount.getIdAccount(), amount, fromAccount.getAmount(), toAccount.getAmount());

                } else {
                    fromAccount.setAmount(fromAccount.getAmount() - amount);
                    accountRepository.save(fromAccount);
                    toAccount.setAmount(toAccount.getAmount() + amount / course);
                    accountRepository.save(toAccount);
                    logger.info("Транзакция.  Разные валюты /. fromAccount={}, toAccount={}, amount={}, fromAccamount={}, toAccamount={}", fromAccount.getIdAccount(), toAccount.getIdAccount(), amount, fromAccount.getAmount(), toAccount.getAmount());

                }
            }
        }else{
            logger.info("Транзакция. Одна валюта. fromAccount={}, toAccount={}, amount={}, ", fromAccount.getIdAccount(), toAccount.getIdAccount(), amount);
            fromAccount.setAmount(fromAccount.getAmount() - amount);
            accountRepository.save(fromAccount);
            toAccount.setAmount(toAccount.getAmount() + amount);
            accountRepository.save(toAccount);
        }

//        fromAccount.setAmount(fromAccount.getAmount() - amount);
//        accountRepository.save(fromAccount);
//
//        toAccount.setAmount(toAccount.getAmount() + amount);
//        accountRepository.save(toAccount);
        TypeOfTransaction transferType = typeOfTransactionRep.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Тип транзакции не найден"));

        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTrAmount(amount);
        transaction.setTrDate(new Date());
        transaction.setIdTransaction(transferType);
        transactionRepository.save(transaction);
    }
    @Transactional
    public void closeDeposit(Long fromAccountId, Long toAccountId, Long amount) {
        Accounts fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет отправителя не найден"));

        Accounts toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Счет получателя не найден"));

//        if (fromAccount.getAmount() < amount) {
//            throw new IllegalStateException("Недостаточно средств на счете");
//        }
        if ((!Objects.equals(fromAccount.getCurrency(), toAccount.getCurrency())) && ((!Objects.equals(toAccount.getCurrency(),"USD") && !Objects.equals(fromAccount.getCurrency(),"EUR")) || ((!Objects.equals(toAccount.getCurrency(),"EUR") && !Objects.equals(fromAccount.getCurrency(),"USD"))))) {
            if((Objects.equals(fromAccount.getCurrency(), "USD") && Objects.equals(toAccount.getCurrency(), "EUR") || (Objects.equals(toAccount.getCurrency(), "USD") && Objects.equals(fromAccount.getCurrency(), "EUR")))){
                logger.info("Транзакция. USD and EUR. fromAccount={}, toAccount={}, amount={}, ", fromAccount.getIdAccount(), toAccount.getIdAccount(), amount);
                toAccount.setAmount(toAccount.getAmount() + amount);
                accountRepository.save(toAccount);
            }else {
                if ((Objects.equals(fromAccount.getCurrency(), "USD") || Objects.equals(fromAccount.getCurrency(), "EUR")) && (!Objects.equals(fromAccount.getCurrency(), "RUB"))) {
                    toAccount.setAmount(toAccount.getAmount() + amount * course);
                    accountRepository.save(toAccount);
                    logger.info("Транзакция.  Разные валюты *. fromAccount={}, toAccount={}, amount={}, fromAccamount={}, toAccamount={}", fromAccount.getIdAccount(), toAccount.getIdAccount(), amount, fromAccount.getAmount(), toAccount.getAmount());

                } else {

                    toAccount.setAmount(toAccount.getAmount() + amount / course);
                    accountRepository.save(toAccount);
                    logger.info("Транзакция.  Разные валюты /. fromAccount={}, toAccount={}, amount={}, fromAccamount={}, toAccamount={}", fromAccount.getIdAccount(), toAccount.getIdAccount(), amount, fromAccount.getAmount(), toAccount.getAmount());

                }
            }
        }else{
            logger.info("Транзакция. Одна валюта. fromAccount={}, toAccount={}, amount={}, ", fromAccount.getIdAccount(), toAccount.getIdAccount(), amount);
            toAccount.setAmount(toAccount.getAmount() + amount);
            accountRepository.save(toAccount);
        }
        TypeOfTransaction transferType = typeOfTransactionRep.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("Тип транзакции не найден"));
        logger.info("Транзакция. Пройдены проверки, идет обработка запроса. fromAccount={}, toAccount={}, amount={}, ", fromAccount.getIdAccount(), toAccount.getIdAccount(), amount);
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTrAmount(amount);
        transaction.setTrDate(new Date());
        transaction.setIdTransaction(transferType);
        transactionRepository.save(transaction);
    }
}

