package project.EAccounting.services;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import project.EAccounting.exceptions.UserNotLoggedException;

@Service
@SessionScope
public class LoggedUserManagementService {

    private String id;

    public String getId() {
        if(id == null)
            throw new UserNotLoggedException();

        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
