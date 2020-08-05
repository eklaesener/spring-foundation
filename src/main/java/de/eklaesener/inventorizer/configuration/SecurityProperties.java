package de.eklaesener.inventorizer.configuration;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@ConfigurationProperties(prefix = "security")
@Validated
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
     * Login password of the default admin. Exempt from the validation rules.
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


    private PasswordValidation validation;

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    public static class PasswordValidation {

        /**
         * The minimum length a password must have.
         */
        @Min(0)
        private int minimumLength;

        /**
         * Set to true if passwords should contain at least one English alphanumeric character.
         */
        private boolean requireAlphaNumeric;

        /**
         * Set to true if passwords should contain at least one special character.
         */
        private boolean requireSpecial;

        /**
         * If require-special is set to true, the validator checks for the presence of at least one of these
         * characters.
         */
        private String specialCharacters;

        /**
         * The maximum length of sequences in a password. Set to -1 to allow any length.
         */
        @Min(-1)
        private int rejectIfSequenceGreaterThan;

        /**
         * Set to true to reject passwords matching a dictionary entry.
         */
        private boolean rejectDictionaryEntries;

        /**
         * The location of the dictionary to use, relative to the working directory.
         */
        private String dictionaryLocation;

        /**
         * Set to true to reject passwords that contain the user's username.
         */
        private boolean rejectContainingUsername;


        public PasswordValidation() {

        }
    }


    public SecurityProperties() {

    }

}
