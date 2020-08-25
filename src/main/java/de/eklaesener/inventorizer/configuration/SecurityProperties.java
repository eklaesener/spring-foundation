package de.eklaesener.inventorizer.configuration;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@ConfigurationProperties(prefix = "security")
@Validated
@Getter
@ToString
@EqualsAndHashCode
public class SecurityProperties {

    /**
     * Login username of the default admin.
     */
    private final String adminUsername;

    /**
     * Login password of the default admin. Exempt from the validation rules.
     */
    private final String adminPassword;


    /**
     * URL used for fetching tokens.
     */
    private final String authLoginURL;


    /**
     * Private secret used for generation of tokens.
     */
    private final String jwtSecret;


    /**
     * Number of hashing cycles used on passwords. A higher number is generally more secure at the expense of time.
     */
    private final int hashRounds;


    /**
     * Name of the header used for transmitting tokens.
     */
    private final String tokenHeader;

    /**
     * Prefix prepended to each token.
     */
    private final String tokenPrefix;

    /**
     * Type of token to be generated.
     */
    private final String tokenType;


    private final PasswordValidation validation;

    @Getter
    @ToString
    @EqualsAndHashCode
    public static class PasswordValidation {

        /**
         * The minimum length a password must have.
         */
        @Min(0)
        private final int minimumLength;

        /**
         * Set to true if passwords should contain at least one English alphanumeric character.
         */
        private final boolean requireAlphaNumeric;

        /**
         * Set to true if passwords should contain at least one special character.
         */
        private final boolean requireSpecial;

        /**
         * If require-special is set to true, the validator checks for the presence of at least one of these
         * characters.
         */
        private final String specialCharacters;

        /**
         * The maximum length of sequences in a password. Set to -1 to allow any length.
         */
        @Min(-1)
        private final int rejectIfSequenceGreaterThan;

        /**
         * Set to true to reject passwords matching a dictionary entry.
         */
        private final boolean rejectDictionaryEntries;

        /**
         * The location of the dictionary to use, relative to the working directory.
         */
        private final String dictionaryLocation;

        /**
         * Set to true to reject passwords that contain the user's username.
         */
        private final boolean rejectContainingUsername;


        @ConstructorBinding
        public PasswordValidation(@Min(0) final int minimumLength, final boolean requireAlphaNumeric,
                                  final boolean requireSpecial, final String specialCharacters,
                                  final @Min(-1) int rejectIfSequenceGreaterThan, final boolean rejectDictionaryEntries,
                                  final String dictionaryLocation, final boolean rejectContainingUsername) {

            this.minimumLength = minimumLength;
            this.requireAlphaNumeric = requireAlphaNumeric;
            this.requireSpecial = requireSpecial;
            this.specialCharacters = specialCharacters;
            this.rejectIfSequenceGreaterThan = rejectIfSequenceGreaterThan;
            this.rejectDictionaryEntries = rejectDictionaryEntries;
            this.dictionaryLocation = dictionaryLocation;
            this.rejectContainingUsername = rejectContainingUsername;
        }
    }


    @ConstructorBinding
    public SecurityProperties(final String adminUsername, final String adminPassword, final String authLoginURL,
                              final String jwtSecret, final int hashRounds,
                              final String tokenHeader, final String tokenPrefix, final String tokenType,
                              final PasswordValidation validation) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.authLoginURL = authLoginURL;

        this.jwtSecret = jwtSecret;
        this.hashRounds = hashRounds;
        this.tokenHeader = tokenHeader;
        this.tokenPrefix = tokenPrefix;
        this.tokenType = tokenType;
        this.validation = validation;
    }

}
