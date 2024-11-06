package project.EAccounting.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import project.EAccounting.persistence.entities.Transaction;
import project.EAccounting.services.LoggedUserManagementService;

@Repository
public class TransactionRepository {

    private final EntityManagerFactory entityManagerFactory;
    private final LoggedUserManagementService loggedUserManagementService;
    private final UserRepository userRepository;

    @Autowired
    public TransactionRepository(EntityManagerFactory entityManagerFactory, LoggedUserManagementService loggedUserManagementService,
                                 UserRepository userRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.loggedUserManagementService = loggedUserManagementService;
        this.userRepository = userRepository;
    }

    public void store(Transaction transaction) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        transaction.setUser(userRepository.find(loggedUserManagementService.getId()));

        try {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            entityManager.persist(transaction);

            entityTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }
}
