package org.site.BoU.Repositories;

import org.site.BoU.Entities.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientsRep extends JpaRepository<Clients, Long> {

    Optional<Clients> findByMail(String mail);
    Optional<Clients> findByNumber(String number);
    Clients findByLogin(String login);
    List<Clients> findAll();
    boolean existsByLogin(String login);
//    boolean existsBySeriesPasport(Long seriesPasport);
    boolean existsByNumberPasport(Long numberPasport);
    boolean existsByNumber(String number);
    void deleteById(Long idClient);
}
