package org.site.BoU.Services;

import org.site.BoU.Entities.Accounts;
import org.site.BoU.Entities.Clients;
import org.site.BoU.Repositories.ClientsRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<Clients> findAll() {
        return clientsRep.findAll();
    }
    public Clients findByLogin(String login) {
        return clientsRep.findByLogin(login);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Clients client = clientsRep.findByLogin(login);
        if (client == null){
            throw new UsernameNotFoundException(login);
        }else{
//            Clients client = optionalClient.get();
            return User.builder()
                    .username(client.getLogin())
                    .password(client.getPassword())
                    .roles(getRole(client))
                    .build();
        }
    }
    public boolean existById(Clients client) {
        return clientsRep.existsById(client.getIdClient());
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
    public void deleteById(Long id){
        clientsRep.deleteById(id);
    }
}
