package org.site.BoU.Services;

import org.site.BoU.Entities.ClientDeposit;
import org.site.BoU.Repositories.ClientsDepositsRep;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DepositSchedulerService {

    private final ClientsDepositsRep clientDepRep;

    public DepositSchedulerService(ClientsDepositsRep clientsDepositsRep) {
        this.clientDepRep = clientsDepositsRep;
    }

    @Scheduled(fixedRate = 10000)
//    @Scheduled(cron = "0 0 0 * * ?") // каждый день в полночь
    public void checkExpiredDeposits() {
        List<ClientDeposit> deposits = clientDepRep.findAll();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();

        for (ClientDeposit deposit : deposits) {
            if (deposit.getCloseDate() != null &&
                    !"ac".equals(deposit.getDepositStatus()) && !"c".equals(deposit.getDepositStatus()) &&
                    !deposit.getCloseDate().after(today)) {
                deposit.setDepositStatus("ac");
                clientDepRep.save(deposit);

                System.out.println("Вклад " + deposit.getIdCD() + " закрыт по сроку и ожидает перевод.");
            }
        }
    }
}
