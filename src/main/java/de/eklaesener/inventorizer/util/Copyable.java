package de.eklaesener.inventorizer.util;

public interface Copyable<E> {

    E copy();

    boolean isCopyOf(final E source);
}
