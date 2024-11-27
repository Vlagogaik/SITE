package org.site.BoU.Entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Accounts {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounts_id_account_seq")
    @SequenceGenerator(name = "accounts_id_account_seq", sequenceName = "accounts_id_account_seq", allocationSize = 1)
    private Long id_account;

    @ManyToOne
    @JoinColumn(name = "id_client")
    private Clients id_client;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private char status;
}
