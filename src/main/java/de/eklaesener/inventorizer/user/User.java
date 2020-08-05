package de.eklaesener.inventorizer.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(
    name = "user",
    uniqueConstraints = {
        @UniqueConstraint(name = "user_uc_username", columnNames = "username"),
        @UniqueConstraint(name = "user_uc_mail_address", columnNames = "mail_address")
    },
    indexes = {
        @Index(name = "user_idx_username", columnList = "username"),
        @Index(name = "user_idx_mail_address", columnList = "mail_address")
    }
)
@Cache(
    usage = CacheConcurrencyStrategy.READ_WRITE
)
@NaturalIdCache
@ToString
public final class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    private static final String GROUP_AUTHORIZATION = "authorization";

    private static final String GROUP_DATA = "data";


    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "user_sequence"
    )
    @GenericGenerator(
        name = "user_sequence",
        strategy = "sequence",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "user_sequence"),
            @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "3"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "pooled-lo")
        }
    )
    @Getter
    private Long id;


    @NaturalId(mutable = true)
    @Basic(fetch = FetchType.LAZY)
    @LazyGroup(GROUP_AUTHORIZATION)
    @Column(name = "username", nullable = false)
    @Setter
    private String username;

    @Basic(fetch = FetchType.LAZY)
    @LazyGroup(GROUP_DATA)
    @Column(name = "mail_address", nullable = false)
    @Getter
    @Setter
    private String mailAddress;

    @Basic(fetch = FetchType.LAZY)
    @LazyGroup(GROUP_AUTHORIZATION)
    @Column(name = "password", nullable = false)
    @Setter
    private String password;

    @ElementCollection
    @CollectionTable(
        name = "user_authorities",
        joinColumns = @JoinColumn(
            name = "user_id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(
                name = "user_authorities_fk_user",
                foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE")
        )
    )
    @Column(name = "authority", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @LazyGroup(GROUP_AUTHORIZATION)
    @Setter
    private Set<Authority> authorities = EnumSet.noneOf(Authority.class);

    @Basic(fetch = FetchType.LAZY)
    @LazyGroup(GROUP_AUTHORIZATION)
    @Column(name = "locked", nullable = false)
    @Setter
    private boolean locked = false;

    @Basic(fetch = FetchType.LAZY)
    @LazyGroup(GROUP_AUTHORIZATION)
    @Column(name = "force_change_password", nullable = false)
    @Getter
    @Setter
    private boolean forceChangePassword = false;

    @Basic(fetch = FetchType.LAZY)
    @LazyGroup(GROUP_DATA)
    @Column(name = "first_name")
    @Getter
    @Setter
    private String firstName;

    @Basic(fetch = FetchType.LAZY)
    @LazyGroup(GROUP_DATA)
    @Column(name = "last_name")
    @Getter
    @Setter
    private String lastName;

    protected User() {
    }

    public User(final String username, final String mailAddress, final String password) {
        this.username = Objects.requireNonNull(username);
        this.mailAddress = Objects.requireNonNull(mailAddress);
        this.password = Objects.requireNonNull(password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<String> roles = authorities.stream().map(Authority::asRole).collect(Collectors.toList());
        return AuthorityUtils.createAuthorityList(roles.toArray(String[]::new));
    }

    public Set<Authority> getAuthoritiesAsEnum() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        final User user = (User) obj;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}


