package org.site.BoU.Services;

import org.site.BoU.Entities.Clients;
import org.site.BoU.Repositories.ClientsRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService implements UserDetailsService {

    @Autowired
    private ClientsRep clientsRep;


    @Autowired
    public ClientService(ClientsRep clientsRep) {
        this.clientsRep = clientsRep;
    }

    public Clients save(Clients client) {

        return clientsRep.save(client);
    }

    public Optional<Clients> findById(long id) {
        return clientsRep.findById(id);
    }
    public Optional<Clients> findByLogin(Clients client) {
        return clientsRep.findByLogin(client.getLogin());
    }
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Clients> optionalClient = clientsRep.findByLogin(login);
        if (optionalClient.isEmpty()){
            throw new UsernameNotFoundException(login);
        }else{
            Clients client = optionalClient.get();
            return User.builder()
                    .username(client.getLogin())
                    .password(client.getPassword())
                    .roles(getRole(client))
                    .build();
        }
    }
    public boolean existByLogin(Clients client) {
        return clientsRep.existsByLogin(client.getLogin());
    }

    private String[] getRole(Clients clients){
        if(clients.getRole() == null){
            return new String[]{"USER"};
        }
        return clients.getRole().split(",");
    }


    public boolean existByPasport(Clients client) {
        return clientsRep.existsByNumberPasport(client.getNumberPasport());
    }

    public boolean existByNumber(Clients client) {
        return clientsRep.existsByNumber(client.getNumber());
    }
}
