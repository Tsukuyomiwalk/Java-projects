package info.kgeorgiy.ja.latanov.arrayset;

import java.util.*;

import static java.util.Collections.binarySearch;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {
    private final List<T> collection;
    private final Comparator<T> compare;

    public ArraySet(Collection<T> collection, Comparator<T> compare) {
        Set<T> set = new TreeSet<>(compare);
        set.addAll(collection);
        this.collection = new ArrayList<>(set);
        this.compare = compare;
    }

    public ArraySet() {
        this.collection = Collections.emptyList();
        this.compare = null;
    }

    public ArraySet(Comparator<T> compare) {
        this.collection = Collections.emptyList();
        this.compare = compare;
    }

    public ArraySet(Collection<T> collection) {
        this.collection = new ArrayList<>(new TreeSet<>(collection));
        this.compare = null;
    }

    @Override
    public Comparator<T> comparator() {
        return compare;
    }


    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        assert fromElement != null;
        assert toElement != null;
        if (compare != null) {
            if (compare.compare(fromElement, toElement) <= 0) {
                return tailSet(fromElement).headSet(toElement);
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            return tailSet(toElement).headSet(fromElement);
        }
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        assert toElement != null;
        return new ArraySet<>(Collections.unmodifiableList(collection.subList(0, getMyObject(toElement, compare))), compare);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        assert fromElement != null;
        return new ArraySet<>(Collections.unmodifiableList(collection.subList(getMyObject(fromElement, compare), collection.size())), compare);
    }

    private int getMyObject(T fromElement, Comparator<T> compare) {
        int myObject = binarySearch(collection, fromElement, compare);
        if (myObject < 0) {
            myObject = -myObject - 1;
        }
        return myObject;
    }

    @Override
    public int size() {
        return collection.size();
    }

    @Override
    public T first() throws NoSuchElementException {
        if (checkSize()) return collection.get(0);
        throw new NoSuchElementException();
    }

    private boolean checkSize() {
        return collection.size() != 0;
    }

    @Override
    public T last() {
        if (checkSize()) return collection.get(collection.size() - 1);
        throw new NoSuchElementException();
    }


    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        int low = 0;
        int high = collection.size() - 1;
        if (compare == null) {
            return true;
        }
        while (low <= high) {
            int mid = (low + high) / 2;
            T midVal = collection.get(mid);
            int cmp = compare.compare(midVal, (T) o);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return true;
            }
        }
        return false;
    }


    @Override
    public Iterator<T> iterator() {
        return collection.iterator();
    }
}
