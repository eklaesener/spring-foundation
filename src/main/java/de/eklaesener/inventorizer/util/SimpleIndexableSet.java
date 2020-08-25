package de.eklaesener.inventorizer.util;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;

@Validated
public final class SimpleIndexableSet<E extends Indexable> implements IndexableSet<E> {

    private static final String UNCHECKED = "unchecked";

    private static final int DEFAULT_CAPACITY = 16;

    private int size;

    private Object[] data;


    private class SimpleIterator implements Iterator<E> {

        int index = 0;

        boolean removed = true;

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public E next() {
            if (index >= size) {
                throw new NoSuchElementException();
            }
            final E result = getInternal(index);
            index++;
            removed = false;
            return result;
        }

        @Override
        public void remove() {
            if (removed) {
                throw new IllegalStateException();
            }
            removed = true;
            index--;
            SimpleIndexableSet.this.remove(index);
        }
    }


    public SimpleIndexableSet() {
        size = 0;
        data = new Object[DEFAULT_CAPACITY];
    }

    public SimpleIndexableSet(final int startingCapacity) {
        size = 0;
        data = new Object[startingCapacity];
    }

    public SimpleIndexableSet(final Collection<? extends E> collection) {
        addAll(collection);
    }

    @NotNull
    @Override
    public E get(@Min(0) final int index) {
        Objects.checkIndex(index, size);
        return getInternal(index);
    }

    @SuppressWarnings(UNCHECKED)
    private E getInternal(final int index) {
        return (E) data[index];
    }

    @Override
    public int indexOf(final Object obj) {
        if (!(obj instanceof Indexable)) {
            return Indexable.NO_INDEX;
        }
        final Indexable element = (Indexable) obj;
        if (containsIndexable(element)) {
            return element.getIndex();
        } else {
            return Indexable.NO_INDEX;
        }
    }

    @Override
    public boolean add(final int index, @NotNull final E element) {
        Objects.checkIndex(index, size + 1);
        if (containsIndexable(element)) {
            return false;
        }
        ensureCapacity(1);
        for (int i = size; i > index; i--) {
            // Change indices while moving
            data[i] = data[i - 1];
            getInternal(i).setIndex(i);
        }
        data[index] = element;
        element.setIndex(index);
        size++;
        return true;
    }

    private void ensureCapacity(final int needed) {
        if (needed + size >= data.length) {
            final Object[] newData = new Object[needed + 2 * size];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
        }
    }

    @Override
    public boolean add(@NotNull final E element) {
        return add(size, element);
    }

    @Override
    public boolean addAll(final int index, @NotNull final Collection<? extends E> collection) {
        int currentIndex = index;
        boolean changed = false;
        ensureCapacity(collection.size());
        for (final E element : collection) {
            if (add(currentIndex, element)) {
                changed = true;
            }
            currentIndex++;
        }
        return changed;
    }

    @Override
    public boolean addAll(@NotNull final Collection<? extends E> collection) {
        return addAll(size, collection);
    }

    @Override
    public boolean contains(final Object obj) {
        if (!(obj instanceof Indexable)) {
            return false;
        }
        final Indexable element = (Indexable) obj;
        return containsIndexable(element);
    }

    private boolean containsIndexable(final Indexable indexable) {
        final int index = indexable.getIndex();
        return index != Indexable.NO_INDEX && get(index).equals(indexable);
    }

    @Override
    public E remove(final int index) {
        Objects.checkIndex(index, size);
        final E result = getInternal(index);
        for (int i = index; i < size - 1; i++) {
            // Change indices while moving
            data[i] = data[i + 1];
            getInternal(i).setIndex(i);
        }
        data[size - 1] = null;
        result.setIndex(Indexable.NO_INDEX);
        size--;
        return result;
    }

    @Override
    public boolean remove(final Object obj) {
        if (!(obj instanceof Indexable)) {
            return false;
        }
        final Indexable element = (Indexable) obj;
        if (containsIndexable(element)) {
            remove(element.getIndex());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAll(@NotNull final Collection<?> collection) {
        boolean changed = false;
        for (final Object obj : collection) {
            if (remove(obj)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(@NotNull final Collection<?> collection) {
        boolean changed = false;
        for (final Object obj : this) {
            if (!collection.contains(obj)) {
                remove(obj);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size > 0;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new SimpleIterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @SuppressWarnings(UNCHECKED)
    @NotNull
    @Override
    public <T> T[] toArray(@NotNull final T[] array) {
        if (array.length < size) {
            // Make a new array of array's runtime type, but my contents:
            return (T[]) Arrays.copyOf(data, size, array.getClass());
        }
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(data, 0, array, 0, size);
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    @Override
    public boolean containsAll(@NotNull final Collection<?> collection) {
        for (final Object obj : collection) {
            if (!contains(obj)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            getInternal(i).setIndex(Indexable.NO_INDEX);
            data[i] = null;
        }
        size = 0;
    }
}
