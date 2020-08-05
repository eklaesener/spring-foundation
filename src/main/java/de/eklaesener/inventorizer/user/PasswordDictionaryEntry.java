package de.eklaesener.inventorizer.user;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "PasswordDictionaryEntry")
@Table(
    name = "password_dictionary"
)
@ToString
public final class PasswordDictionaryEntry {

    @Id
    @Getter
    @Column(name = "password", nullable = false)
    private String password;

    protected PasswordDictionaryEntry() {

    }

    public PasswordDictionaryEntry(final String password) {
        this.password = Objects.requireNonNull(password);
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PasswordDictionaryEntry)) {
            return false;
        }
        final PasswordDictionaryEntry that = (PasswordDictionaryEntry) obj;
        return password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }
}
