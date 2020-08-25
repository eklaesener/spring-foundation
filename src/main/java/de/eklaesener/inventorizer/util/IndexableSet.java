package de.eklaesener.inventorizer.util;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

@Validated
public interface IndexableSet<E extends Indexable> extends Set<E> {

    @NotNull
    E get(@Min(0) final int index);

    int indexOf(final Object obj);

    boolean add(final int index, @NotNull final E element);

    @Override
    boolean add(@NotNull final E element);

    boolean addAll(final int index, @NotNull final Collection<? extends E> collection);

    @Override
    boolean addAll(@NotNull final Collection<? extends E> collection);

    @Override
    boolean contains(final Object obj);

    E remove(final int index);

    @Override
    boolean remove(final Object obj);

    @Override
    boolean removeAll(@NotNull final Collection<?> collection);

    @Override
    boolean retainAll(@NotNull final Collection<?> collection);
}
