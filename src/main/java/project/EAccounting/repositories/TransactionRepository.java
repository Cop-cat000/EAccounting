package project.EAccounting.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import project.EAccounting.exceptions.IncorrectAccountException;
import project.EAccounting.exceptions.IncorrectTransactionException;
import project.EAccounting.model.transaction.*;
import project.EAccounting.persistence.entities.Account;
import project.EAccounting.persistence.entities.Transaction;
import project.EAccounting.persistence.entities.User;
import project.EAccounting.persistence.entities.datatypes.TransactionTypes;
import project.EAccounting.services.LoggedUserManagementService;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionRepository {

    private final EntityManagerFactory entityManagerFactory;
    private final LoggedUserManagementService loggedUserManagementService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionRepository(EntityManagerFactory entityManagerFactory, LoggedUserManagementService loggedUserManagementService,
                                 UserRepository userRepository, AccountRepository accountRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.loggedUserManagementService = loggedUserManagementService;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public List<TransactionWrapper> retrieveByCriteria(TransactionCriteria criteria) {
        List<TransactionWrapper> result;

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            List<Transaction> transactions;
            TypedQuery<Transaction> tq;

            User user = userRepository.find(loggedUserManagementService.getId());
            String jpql = "Select t FROM Transaction t WHERE t.user = :user AND t.date >= :date1 AND t.date <= :date2";

            int accountId = criteria.getAccountId();
            Account account = null;

            if(accountId != -1) {
                jpql += " AND (t.account1 = :account OR t.account2 = :account)";
                account  = accountRepository.retrieveById(List.of(accountId)).get(0);
            }
            jpql += " ORDER BY t.date";

            tq = entityManager.createQuery(jpql, Transaction.class);
            tq.setParameter("user", user);
            tq.setParameter("date1", criteria.getStartDate().atTime(0, 0));
            tq.setParameter("date2", criteria.getEndDate().atTime(0, 0));
            if(accountId != -1)
                tq.setParameter("account", account);

            transactions = tq.getResultList();
            result = convertToWrapper(transactions, entityManager);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            entityManager.close();
        }
        return result;
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

    public void store(TransactionInput input) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Transaction transaction = input.makeEntity();


        try {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            User user = userRepository.find(loggedUserManagementService.getId());
            Account account1 = accountRepository.retrieveById(List.of(input.getAccountId1())).get(0);
            if(account1 == null) throw new IncorrectAccountException();
            int availBalance1 = account1.getAvailBalance();

            transaction.setUser(user);
            transaction.setAccount1(account1);

            if(input.getType() == ClientTransactionTypes.BETWEEN) {
                Account account2 = accountRepository.retrieveById(List.of(input.getAccountId2())).get(0);
                if(account2 == null) throw new IncorrectAccountException();
                int availBalance2 = account2.getAvailBalance();

                transaction.setAccount2(account2);

                account1.setAvailBalance(availBalance1 - input.getSum());
                account2.setAvailBalance(availBalance2 + input.getSum());
                entityManager.merge(account2);
            }
            else if(input.getType() == ClientTransactionTypes.PAYMENT || input.getType() == ClientTransactionTypes.TRANSFER ||
                    input.getType() == ClientTransactionTypes.UP) {
               if(input.getType() == ClientTransactionTypes.PAYMENT ||
                       (input.getType() == ClientTransactionTypes.TRANSFER && input.getSum() < 0)) {
                   account1.setAvailBalance(availBalance1 - Math.abs(input.getSum()));
               }
               else {
                   account1.setAvailBalance(availBalance1 + Math.abs(input.getSum()));
               }
            }

            entityManager.merge(account1);
            entityManager.persist(transaction);
            entityTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public void delete(int id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            Transaction transaction = entityManager.find(Transaction.class, id);
            if(transaction == null || !transaction.getUser().getId().equals(loggedUserManagementService.getId()))
                throw new IncorrectTransactionException();
            Account account1 = transaction.getAccount1();
            int availBalance1 = account1.getAvailBalance();
            Account account2 = transaction.getAccount2();
            if(account2 != null) {
                int availBalance2 = account2.getAvailBalance();

                account1.setAvailBalance(availBalance1 + transaction.getSum());
                account2.setAvailBalance(availBalance2 - transaction.getSum());
            }
            else {
                if(transaction.getType() == TransactionTypes.PAYMENT ||
                        (transaction.getType() == TransactionTypes.TRANSFER && transaction.getSum() < 0)) {
                    account1.setAvailBalance(availBalance1 + transaction.getSum());
                } else if((transaction.getType() == TransactionTypes.TRANSFER && transaction.getSum() > 0) ||
                        (transaction.getType() == TransactionTypes.UP)) {
                    account1.setAvailBalance(availBalance1 - transaction.getSum());
                } else {
                    if(transaction.getType() == TransactionTypes.BALANCE_CHANGE || transaction.getType() == TransactionTypes.CREDIT_CARD_LIMIT_CHANGE)
                        throw new IncorrectTransactionException();
                    else {
                        account1.setAvailBalance(availBalance1 + transaction.getSum());
                    }
                }
            }

            entityManager.remove(transaction);
            entityTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    private List<TransactionWrapper> convertToWrapper(List<Transaction> transactions, EntityManager entityManager) {
        List<TransactionWrapper> result = new ArrayList<>();

        for(Transaction transaction : transactions) {
            TransactionWrapper wrapper = new TransactionWrapper();
            wrapper.setId(transaction.getId());
            wrapper.setSum(transaction.getSum());
            wrapper.setDate(transaction.getDate());
            wrapper.setType(transaction.getType());
            wrapper.setAccount1(transaction.getAccount1().getName());
            if(transaction.getAccount2() != null)
                wrapper.setAccount2(transaction.getAccount2().getName());
            wrapper.setComment(transaction.getComment());

            result.add(wrapper);
        }
        return result;
    }
}
