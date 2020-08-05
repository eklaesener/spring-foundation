package de.eklaesener.inventorizer.user;

import de.eklaesener.inventorizer.configuration.SecurityProperties;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.passay.CharacterData;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final String QUERY_NOT_POSSIBLE = "Cannot execute query.";

    private static final int MINIMUM_GENERATED_CHARACTERS = 20;

    private final PasswordEncoder passwordEncoder;

    private final PasswordValidator passwordValidator;

    private final int generatedPasswordLength;

    private final PasswordGenerator passwordGenerator;

    private final List<CharacterRule> characterRules;

    private final EntityManagerFactory entityManagerFactory;


    @Autowired
    public UserServiceImpl(final SecurityProperties securityProperties,
                           final EntityManagerFactory entityManagerFactory) {
        this.passwordEncoder = new BCryptPasswordEncoder(securityProperties.getHashRounds());
        this.entityManagerFactory = entityManagerFactory;
        this.generatedPasswordLength = securityProperties.getValidation().getMinimumLength()
            + MINIMUM_GENERATED_CHARACTERS;
        this.passwordValidator = initializePasswordValidator(securityProperties.getValidation());
        this.characterRules = passwordValidator.getRules().stream()
            .filter(rule -> rule instanceof CharacterRule).map(rule -> (CharacterRule) rule)
            .collect(Collectors.toList());
        this.passwordGenerator = new PasswordGenerator();
    }

    private PasswordValidator initializePasswordValidator(final SecurityProperties.PasswordValidation validationRules) {
        final List<Rule> rules = new ArrayList<>(8);
        rules.add(new LengthRule(validationRules.getMinimumLength(), Integer.MAX_VALUE));
        if (validationRules.isRequireAlphaNumeric()) {
            rules.add(new CharacterRule(new CharacterData() {
                @Override
                public String getErrorCode() {
                    return "INSUFFICIENT_ALPHABETICAL";
                }

                @Override
                public String getCharacters() {
                    return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                }
            }, 1));
        }
        if (validationRules.isRequireSpecial()) {
            rules.add(new CharacterRule(new CharacterData() {
                @Override
                public String getErrorCode() {
                    return "INSUFFICIENT_SPECIAL";
                }

                @Override
                public String getCharacters() {
                    return validationRules.getSpecialCharacters();
                }
            }, 1));
        }
        if (validationRules.getRejectIfSequenceGreaterThan() != -1) {
            rules.add(new IllegalSequenceRule(EnglishSequenceData.Alphabetical,
                validationRules.getRejectIfSequenceGreaterThan(), false));
            rules.add(new IllegalSequenceRule(EnglishSequenceData.Numerical,
                validationRules.getRejectIfSequenceGreaterThan(), false));
            rules.add(new IllegalSequenceRule(EnglishSequenceData.USQwerty,
                validationRules.getRejectIfSequenceGreaterThan(), false));
        }
        if (validationRules.isRejectDictionaryEntries()) {
            initializeDictionary(new File(validationRules.getDictionaryLocation()));
            rules.add(new DictionaryRule(new PasswordDictionary(entityManagerFactory)));
        }
        if (validationRules.isRejectContainingUsername()) {
            rules.add(new UsernameRule());
        }
        return new PasswordValidator(rules);
    }

    private void initializeDictionary(final File file) {
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new IllegalArgumentException("File " + file.getName() + " can't be read!");
        }
        final CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        final CriteriaQuery<String> criteriaQuery =
            criteriaBuilder.createQuery(String.class);
        criteriaQuery.select(criteriaQuery.from(PasswordDictionaryEntry.class).get("password"));
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            final TypedQuery<String> typedQuery = entityManager.createQuery(criteriaQuery);
            transaction.begin();
            final Set<String> savedPasswords = new TreeSet<>(typedQuery.getResultList());
            final List<String> unsavedPasswords = Collections.synchronizedList(new LinkedList<>());
            // First go through the file and check if each entry is present
            reader.lines().parallel().forEach(password -> {
                if (!savedPasswords.contains(password)) {
                    unsavedPasswords.add(password);
                }
            });
            // Then remove duplicates and persist the entries that aren't present yet
            new HashSet<>(unsavedPasswords).forEach(password -> {
                final PasswordDictionaryEntry entry = new PasswordDictionaryEntry(password);
                entityManager.persist(entry);
            });
            transaction.commit();
        } catch (final RollbackException e) {
            transaction.setRollbackOnly();
            transaction.rollback();
            throw new IllegalStateException(QUERY_NOT_POSSIBLE, e);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        } finally {
            entityManager.close();
        }
    }


    @Override
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public PasswordValidator getPasswordValidator() {
        return passwordValidator;
    }

    @Override
    public Optional<User> get(final Long id) {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            final User user = entityManager.find(User.class, id);
            transaction.commit();
            return Optional.ofNullable(user);
        } catch (final RollbackException e) {
            transaction.setRollbackOnly();
            transaction.rollback();
            throw new IllegalStateException(QUERY_NOT_POSSIBLE, e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public User add(final UserDTO data) {
        final RuleResult validationResult = passwordValidator
            .validate(new PasswordData(data.getUsername(), data.getPassword()));
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException();
        }
        final User user = new User(data.getUsername(), data.getMailAddress(),
            passwordEncoder.encode(data.getPassword()));
        if (data.getAuthorities() != null) {
            user.getAuthoritiesAsEnum().addAll(data.getAuthorities());
        }
        user.setFirstName(data.getFirstName());
        user.setLastName(data.getLastName());
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
            return user;
        } catch (final RollbackException e) {
            transaction.setRollbackOnly();
            transaction.rollback();
            throw new IllegalStateException(QUERY_NOT_POSSIBLE, e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public User update(final Long id, final UserDTO data) {
        return null;
    }

    @Override
    public User resetPassword(final Long id) {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            final User user = entityManager.find(User.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (user == null) {
                throw new IllegalArgumentException();
            }
            user.setForceChangePassword(true);
            final String newPassword = passwordGenerator.generatePassword(generatedPasswordLength, characterRules);
            user.setPassword(passwordEncoder.encode(newPassword));
            transaction.commit();
            // TODO: Once mailing works, send the new password to the user
            return user;
        } catch (final RollbackException e) {
            transaction.setRollbackOnly();
            transaction.rollback();
            throw new IllegalStateException(QUERY_NOT_POSSIBLE, e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void delete(final Long id) {

    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        final Session session = entityManagerFactory.createEntityManager().unwrap(Session.class);
        final Transaction transaction = session.beginTransaction();
        try (session) {
            final User user = session.bySimpleNaturalId(User.class).load(username);
            transaction.commit();
            if (user == null) {
                throw new UsernameNotFoundException("User with username " + username + " not found.");
            }
            return user;
        } catch (final RollbackException e) {
            transaction.markRollbackOnly();
            transaction.rollback();
            throw new IllegalStateException(QUERY_NOT_POSSIBLE, e);
        }
    }
}
