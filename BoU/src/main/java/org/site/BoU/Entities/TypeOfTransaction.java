package org.site.BoU.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "type_of_transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "type_of_transaction_id_ttransaction_seq")
    @SequenceGenerator(name = "type_of_transaction_id_ttransaction_seq", sequenceName = "type_of_transaction_id_ttransaction_seq", allocationSize = 1)
    private Long idTransaction;

    @Column(name = "name", nullable = false)
    private String name;
}
