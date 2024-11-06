package project.EAccounting.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.springframework.stereotype.Repository;

import project.EAccounting.exceptions.UserNotFoundException;
import project.EAccounting.persistence.entities.User;

@Repository
public class UserRepository {

    private final EntityManagerFactory entityManagerFactory;

    public UserRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public User find(String id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User user = entityManager.find(User.class, id);

        try {
            user = entityManager.find(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        if(user == null) throw new UserNotFoundException();
        return user;
    }

    public void store(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            entityManager.persist(user);

            entityTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }
}
