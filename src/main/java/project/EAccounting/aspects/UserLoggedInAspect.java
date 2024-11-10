package project.EAccounting.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import project.EAccounting.services.LoggedUserManagementService;

@Aspect
@Component
public class UserLoggedInAspect {

    private final LoggedUserManagementService loggedUserManagementService;

    public UserLoggedInAspect(LoggedUserManagementService loggedUserManagementService) {
        this.loggedUserManagementService = loggedUserManagementService;
    }

    @Around("@annotation(project.EAccounting.annotations.CheckIfLoggedIn)")
    public Object checkIfLoggedIn(ProceedingJoinPoint joinPoint) throws Throwable {
        loggedUserManagementService.getId();
        return joinPoint.proceed();
    }
}
