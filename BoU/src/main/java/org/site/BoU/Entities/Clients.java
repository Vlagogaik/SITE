package org.site.BoU.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "clients")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clients {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clients_id_client_seq")
    @SequenceGenerator(name = "clients_id_client_seq", sequenceName = "clients_id_client_seq", allocationSize = 1)
    private Long id_client;

    @Column(name = "number_pasport", nullable = false)
    private Long numberPasport;

//    @Column(name = "series_pasport", nullable = false)
//    private Long seriesPasport;

    @Column(nullable = false)
    private String first_name;

    @Column(nullable = false)
    private String last_name;

    private String surname;

    @Column(nullable = false)
    private String mail;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String role;
}
