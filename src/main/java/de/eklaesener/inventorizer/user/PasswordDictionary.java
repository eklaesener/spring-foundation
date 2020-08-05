package de.eklaesener.inventorizer.user;

import org.passay.dictionary.Dictionary;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Objects;

public final class PasswordDictionary implements Dictionary {

    private static final String QUERY_NOT_POSSIBLE = "Cannot execute query.";

    private final EntityManagerFactory entityManagerFactory;


    public PasswordDictionary(final EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = Objects.requireNonNull(entityManagerFactory);
    }


    @Override
    public boolean search(final String word) {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            final PasswordDictionaryEntry entry = entityManager.find(PasswordDictionaryEntry.class, word);
            return entry != null;
        } catch (final RollbackException e) {
            transaction.setRollbackOnly();
            transaction.rollback();
            throw new IllegalStateException(QUERY_NOT_POSSIBLE, e);
        }
    }

    @Override
    public long size() {
        final CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(PasswordDictionaryEntry.class)));
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            final long result = entityManager.createQuery(criteriaQuery).getSingleResult();
            transaction.commit();
            return result;
        } catch (final RollbackException e) {
            transaction.setRollbackOnly();
            transaction.rollback();
            throw new IllegalStateException(QUERY_NOT_POSSIBLE, e);
        }
    }
}
