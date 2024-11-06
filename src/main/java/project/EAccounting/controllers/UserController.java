package project.EAccounting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.ResponseBody;
import project.EAccounting.exceptions.IncorrectCredentialsException;
import project.EAccounting.model.user.UserDetails;
import project.EAccounting.persistence.entities.User;
import project.EAccounting.repositories.UserRepository;
import project.EAccounting.services.LoggedUserManagementService;

@Controller
public class UserController {

    private final LoggedUserManagementService loggedUserManagementService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(LoggedUserManagementService loggedUserManagementService, UserRepository userRepository) {
        this.loggedUserManagementService = loggedUserManagementService;
        this.userRepository = userRepository;
    }

    @QueryMapping
    @ResponseBody
    LoggedUserManagementService getUser() {
        loggedUserManagementService.getId();
        return loggedUserManagementService;
    }

    @QueryMapping
    @ResponseBody
    boolean signIn(@Argument(name = "user") UserDetails userDetails) {
        User user = userRepository.find(userDetails.getId());

        if(user.getPassword() != userDetails.getPasswordHashCode()) throw new IncorrectCredentialsException();

        loggedUserManagementService.setUserName(userDetails.getId());
        return true;
    }

    @MutationMapping
    @ResponseBody
    boolean signUp(@Argument(name = "user") UserDetails userDetails) {
        User user = new User();
        user.setId(userDetails.getId());
        user.setPassword(userDetails.getPasswordHashCode());

        userRepository.store(user);

        return true;
    }
}
