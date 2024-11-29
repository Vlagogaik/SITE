package org.site.BoU.Session;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
public class SessionsManager {
    private final SessionRegistry sessionRegistry;

    public SessionsManager(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public void deleteSessionExceptCurrentByUser(String login, String sesssionId) {
        log.debug("deleteSessionExceptCurrent#user: {}", login);
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        for (Object principal : allPrincipals) {
            if (principal.toString().equals(login)) {
                List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                for (SessionInformation session : sessions) {
                    if (!session.getSessionId().equals(sesssionId)) {
                        session.expireNow();
                    }
                }
            }
        }
    }
}
