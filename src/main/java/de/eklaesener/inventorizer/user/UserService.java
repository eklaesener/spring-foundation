package de.eklaesener.inventorizer.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    Optional<User> get(final Long id);

    User add(final UserDTO data);

    User update(final Long id, final UserDTO data);

    User resetPassword(final Long id);

    void delete(final Long id);

    PasswordEncoder getPasswordEncoder();
}
