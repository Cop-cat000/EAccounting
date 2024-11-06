package project.EAccounting.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import project.EAccounting.exceptions.IncorrectAccountException;
import project.EAccounting.persistence.entities.Account;
import project.EAccounting.persistence.entities.User;
import project.EAccounting.persistence.entities.datatypes.AccountTypes;
import project.EAccounting.services.LoggedUserManagementService;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AccountRepository {

    private final LoggedUserManagementService loggedUserManagementService;
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public AccountRepository(EntityManagerFactory entityManagerFactory, LoggedUserManagementService loggedUserManagementService) {
        this.entityManagerFactory = entityManagerFactory;
        this.loggedUserManagementService = loggedUserManagementService;
    }

    public List<Account> retrieveAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Account> result = new ArrayList<>();

        try {
            User user = entityManager.find(User.class, loggedUserManagementService.getId());
            result = user.getAccounts();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        return result;
    }

    public List<Account> retrieveById(List<Integer> listOfId) {
        List<Account> accounts = retrieveAll();
        List<Account> result = new ArrayList<>();

        for(Account account : accounts) {
            if(listOfId.contains(account.getId()))
                result.add(account);
        }

        return result;
    }

    public List<Account> retrieveByType(List<AccountTypes> listOfTypes) {
        List<Account> accounts = retrieveAll();
        List<Account> result = new ArrayList<>();

        for(Account account : accounts) {
            if(listOfTypes.contains(account.getType()))
                result.add(account);
        }

        return result;
    }

    public void store(Account account) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            User user = entityManager.find(User.class, loggedUserManagementService.getId());

            account.setUser(user);

            entityManager.persist(account);

            entityTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public void deleteById(int id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            Account account = entityManager.find(Account.class, id);
            String currUserId = loggedUserManagementService.getId();
            if(account.getUser().getId().equals(currUserId))
                entityManager.remove(account);

            else throw new IncorrectAccountException();

            entityTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public void merge(Account account) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            String currUserId = loggedUserManagementService.getId();
            if(!account.getUser().getId().equals(currUserId))
                throw new IncorrectAccountException();

            entityManager.merge(account);

            entityTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }
}
