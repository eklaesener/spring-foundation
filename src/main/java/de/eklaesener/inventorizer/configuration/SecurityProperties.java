package de.eklaesener.inventorizer.configuration;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SecurityProperties {

    /**
     * Login username of the default admin.
     */
    private String adminUsername;
    /**
     * Login password of the default admin.
     */
    private String adminPassword;

    /**
     * URL used for fetching tokens.
     */
    private String authLoginURL;

    /**
     * Private secret used for generation of tokens.
     */
    private String jwtSecret;

    /**
     * Number of hashing cycles used on passwords. A higher number is generally more secure at the expense of time.
     */
    private int hashRounds;

    /**
     * Name of the header used for transmitting tokens.
     */
    private String tokenHeader;
    /**
     * Prefix prepended to each token.
     */
    private String tokenPrefix;
    /**
     * Type of token to be generated.
     */
    private String tokenType;

    public SecurityProperties() {

    }

}
