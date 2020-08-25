package de.eklaesener.inventorizer.util;

public interface Indexable {

    int NO_INDEX = -1;

    default boolean hasIndex() {
        return getIndex() != NO_INDEX;
    }

    int getIndex();

    void setIndex(final int index);
}
