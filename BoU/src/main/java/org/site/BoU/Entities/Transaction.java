package org.site.BoU.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_tr_seq")
    @SequenceGenerator(name = "transaction_id_tr_seq", sequenceName = "transaction_id_tr_seq", allocationSize = 1)
    private Long idTr;

    @ManyToOne
    @JoinColumn(name = "id_account")
    private Accounts idAccount;

    @ManyToOne
    @JoinColumn(name = "id_account1")
    private Accounts idAccount1;

    @Column(name = "tr_amount", nullable = false)
    private Long trAmount;

    @Column(name = "tr_date", nullable = false)
    private Date trDate;

    @ManyToOne
    @JoinColumn(name = "id_transaction")
    private Transaction id_transaction;


}
