package org.site.BoU.Entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deposits")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Deposits {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deposits_id_deposit_seq")
    @SequenceGenerator(name = "deposits_id_deposit_seq", sequenceName = "deposits_id_deposit_seq", allocationSize = 1)
    private Long id_deposit;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false)
    private char prolongation;

    @Column(name = "replenishment_capasity", nullable = false)
    private char replenishmentCapasity;

    @Column(nullable = false)
    private Long rate;

    @Column(name = "deposit_status", nullable = false)
    private char depositStatus;

    @Column(name = "min_term_days", nullable = false)
    private Long minTermDays;

    @Column(name = "max_term_days", nullable = false)
    private Long maxTermDays;

    @Column(name = "min_amount", nullable = false)
    private Long minAmount;

    @Column(name = "max_amount", nullable = false)
    private Long maxAmount;

}