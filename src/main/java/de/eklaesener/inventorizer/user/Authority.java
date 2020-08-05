package de.eklaesener.inventorizer.user;

public enum Authority {

    ADMIN,
    USER,
    ANONYMOUS;

    private static final String ROLE = "ROLE_";

    public String asRole() {
        return ROLE + toString();
    }
}
