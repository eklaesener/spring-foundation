package de.eklaesener.inventorizer.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public final class UserDTO {

    private final String username;

    private final String mailAddress;

    private final String password;

    private final List<Authority> authorities;

    private final boolean locked;

    private final String firstName;

    private final String lastName;

    @JsonCreator
    public UserDTO(@JsonProperty final String username, @JsonProperty final String mailAddress,
                   @JsonProperty final String password, @JsonProperty final List<Authority> authorities,
                   @JsonProperty final String firstName, @JsonProperty final String lastName,
                   @JsonProperty final boolean locked) {
        this.username = username;
        this.mailAddress = mailAddress;
        this.password = password;
        this.authorities = authorities;
        this.locked = locked;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
