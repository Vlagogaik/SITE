package org.site.BoU.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name = "clients_deposits")
//@IdClass(ClientDepositId.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_seq")
    @SequenceGenerator(name = "id_seq", sequenceName = "id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long idCD;

    @ManyToOne
    @JoinColumn(name = "id_deposit", referencedColumnName = "id_deposit")
    private Deposits idDeposit;
//    @Id
    @OneToOne
    @JoinColumn(name = "id_account", referencedColumnName = "id_account")
    private Accounts idAccount;

//    @Id



    @Column(name = "deposit_status", nullable = false)
    private String depositStatus;

    @Column(name = "initial_amount", nullable = false)
    private Long initialAmount;

//    @Column(name = "time_in_days", nullable = false)
//    private Long timeInDays;

    @Column(name = "open_date", nullable = false)
    private Date openDate;
    @Column(name = "close_date", nullable = false)
    private Date closeDate;

}
