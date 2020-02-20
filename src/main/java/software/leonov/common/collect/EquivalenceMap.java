package software.leonov.common.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;

/**
 * A {@code Map} implementation which has a predictable iteration order, and allows users to specify their own strategy
 * for comparing keys by providing an {@link Equivalence}. Implements all optional map operations. Keys and values may
 * be {@code null}.
 * <p>
 * <b>Warning: This class intentionally violates the {@link Map} contract, which is defined in terms of the
 * {@link Object#hashCode() hashCode()} and {@link Object#equals(Object) equals(Object)} operations, and thus should not
 * be considered a general-purpose map implementation. Instead this class is designed for the rare cases when custom
 * equality semantics are required.</b>
 * <p>
 * This implementation uses a {@link LinkedHashMap} to store key/value pairs, using the provided {@code Equivalence} to
 * {@link Equivalence#wrap(Object) wrap} and {@link Wrapper#get() unwrap} each key as required. <i>This results in a
 * constant, albeit small, performance penalty for most operations</i>. This class has the same ordering guarantees as a
 * {@code LinkedHashMap} and provides an identical {@link #removeEldestEntry(Map.Entry)} method, which can be overridden
 * to impose a policy for automatically removing stale mappings when new mappings are added.
 * <p>
 * Providing an {@link Equivalence#equals() equals} equivalence will make this class behave identically to a
 * {@code LinkedHashMap}. An {@link Equivalence#identity() identity} equivalence will make this class roughly comparable
 * to an {@link IdentityHashMap}. Care must be taken when implementing an arbitrary {@code Equivalence} to ensure it is
 * consistent with itself. That is to say its {@link Equivalence#equivalent(Object, Object) equivalent(Object, Object)}
 * method <i>must be consistent</i> with its {@link Equivalence#hash(Object) hash(Object)} method in order for this map
 * to function correctly. This map will be serializable if its keys, values, and the provided {@code Equivalence} is
 * serializable.
 * <p>
 * This map is not <i>thread-safe</i>. If multiple threads modify this map concurrently it must be
 * {@link Collections#synchronizedMap(Map) synchronized} externally.
 * <p>
 * The iterators obtained from the {@link Map#entrySet() entrySet()}, {@link Map#keySet() keySet()}, and
 * {@link Map#values() values()} collection views are <i>fail-fast</i>. Attempts to modify the map at any time after an
 * iterator is created, in any way except through the iterator's own {@link Iterator#remove() remove} method, will
 * result in a {@code ConcurrentModificationException}.
 * 
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Zhenya Leonov
 */
public class EquivalenceMap<K, V> extends AbstractMap<K, V> implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    // delegate and equivalence are not final because they are set by clone() and readObject()
    private LinkedHashMap<Equivalence.Wrapper<? super K>, V> delegate;
    private Equivalence<? super K> equivalence;

    private Set<Map.Entry<K, V>> entrySet;

    /**
     * Creates a new insertion-ordered {@code EquivalenceMap} with the default initial capacity (16) and load factor (0.75),
     * which will use the provided {@code Equivalence} to compare keys.
     * 
     * @param equivalence the {@code Equivalence} to use to compare keys
     */
    public EquivalenceMap(final Equivalence<? super K> equivalence) {
        this(equivalence, 16);
    }

    /**
     * Creates a new insertion-ordered {@code EquivalenceMap} with the specified initial capacity and default load factor
     * (0.75), which will use the provided {@code Equivalence} to compare keys.
     * 
     * @param equivalence     the {@code Equivalence} to use to compare keys
     * @param initialCapacity the initial capacity
     */
    public EquivalenceMap(final Equivalence<? super K> equivalence, final int initialCapacity) {
        this(equivalence, initialCapacity, .75f);
    }

    /**
     * Creates a new insertion-ordered {@code EquivalenceMap} with the specified initial capacity and load factor, which
     * will use the provided {@code Equivalence} to compare keys.
     * 
     * @param equivalence     the {@code Equivalence} to use to compare keys
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     */
    public EquivalenceMap(final Equivalence<? super K> equivalence, final int initialCapacity, final float loadFactor) {
        this(equivalence, initialCapacity, .75f, false);
    }

    /**
     * Creates a new {@code EquivalenceMap} with the specified initial capacity, load factor, and ordering mode, which will
     * use the provided {@code Equivalence} to compare keys.
     * 
     * @param equivalence     the {@code Equivalence} to use to compare keys
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @param accessOrder     the ordering mode: {@code true} for access-order or {@code false} for insertion-order
     */
    public EquivalenceMap(final Equivalence<? super K> equivalence, final int initialCapacity, final float loadFactor, final boolean accessOrder) {
        checkNotNull(equivalence, "equivalence == null");

        delegate = new LinkedHashMap<Wrapper<? super K>, V>(initialCapacity, loadFactor, accessOrder) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(final Map.Entry<Wrapper<? super K>, V> eldest) {
                return EquivalenceMap.this.removeEldestEntry(unwrapEntry(eldest));
            }
        };

        this.equivalence = equivalence;
    }

    /**
     * Creates a new insertion-ordered {@code EquivalenceMap} with the same mappings as the specified map. The map will be
     * created with the default load factor (0.75) and use the provided {@code Equivalence} to compare keys.
     * 
     * @param equivalence the {@code Equivalence} to use to compare keys
     * @param m           the map whose mappings are to be placed in this map
     */
    public EquivalenceMap(final Equivalence<? super K> equivalence, final Map<? extends K, ? extends V> m) {
        this(equivalence, checkNotNull(m, "m == null").size(), .75f, false);
        putAll(m);
    }

    /**
     * Returns the {@code Equivalence} this map uses to compare keys.
     * 
     * @return the {@code Equivalence} this map uses to compare keys
     */
    public Equivalence<? super K> getEquivalence() {
        return equivalence;
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean containsKey(final Object o) {
        try {
            return delegate.containsKey(wrap(o));
        } catch (final ClassCastException | NullPointerException ex) { // NullPointerException cannot happen?
            return false;
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet == null ? entrySet = new EntrySet() : entrySet;
    }

    @Override
    public V get(final Object o) {
        try {
            return delegate.get(wrap(o));
        } catch (final ClassCastException | NullPointerException ex) { // NullPointerException cannot happen?
            return null;
        }
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public V put(final K key, final V value) {
        return delegate.put(wrap(key), value);
    }

    @Override
    public V remove(final Object o) {
        try {
            return delegate.remove(wrap(o));
        } catch (final ClassCastException | NullPointerException ex) { // NullPointerException cannot happen?
            return null;
        }
    }

    /**
     * Returns {@code true} if this map should remove its eldest entry. Default implementation always returns {@code false}.
     * See <a href=
     * "https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html#removeEldestEntry-java.util.Map.Entry-"
     * target="_blank">LinkedHashMap.removeEldestEntry(Map.Entry)</a>
     * 
     * for details.
     *
     * @param eldest the least recently inserted or least recently accessed entry in the map (depending on the ordering
     *               mode)
     * 
     * @return {@code true} if the {@code eldest} entry should be removed or {@code false} if it should be retained
     */
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return false;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public EquivalenceMap<K, V> clone() {
        final EquivalenceMap<K, V> m;
        try {
            m = (EquivalenceMap<K, V>) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new InternalError();
        }
        m.delegate = (LinkedHashMap<Wrapper<? super K>, V>) delegate.clone();
        m.entrySet = null;
        return m;
    }

    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(delegate);
        oos.writeObject(equivalence);
    }

    @SuppressWarnings("unchecked")
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        delegate = (LinkedHashMap<Wrapper<? super K>, V>) ois.readObject();
        equivalence = (Equivalence<? super K>) ois.readObject();
        entrySet = null;
    }

    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public void clear() {
            EquivalenceMap.this.clear();
        }

        @Override
        public boolean contains(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry<?, ?> e = (Entry<?, ?>) o;

                try {
                    final Wrapper<K> key = wrap(e.getKey());
                    return delegate.containsKey(key) && Objects.equals(delegate.get(key), e.getValue());
                } catch (final ClassCastException | NullPointerException ex) { // NullPointerException cannot happen?
                }
            }

            return false;
        }

        @Override
        public boolean remove(final Object o) {
            return o instanceof Map.Entry ? super.remove(o) : false;
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            checkNotNull(c, "c == null");
            boolean modified = false;

            for (final Iterator<?> i = c.iterator(); i.hasNext();)
                modified |= remove(i.next());

            return modified;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean retainAll(final Collection<?> c) {
            checkNotNull(c, "c == null");
            final Map<K, V> m = new EquivalenceMap<>(equivalence);

            for (final Object o : c)
                if (o instanceof Map.Entry<?, ?> && o != null)
                    try {
                        final Map.Entry<K, V> e = (Map.Entry<K, V>) o;
                        m.put(e.getKey(), e.getValue());
                    } catch (final ClassCastException | NullPointerException e) { // NullPointerException cannot happen?
                    }

            return super.retainAll(m.entrySet());
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            final Iterator<Entry<Equivalence.Wrapper<? super K>, V>> itor = delegate.entrySet().iterator();
            return new Iterator<Map.Entry<K, V>>() {

                @Override
                public boolean hasNext() {
                    return itor.hasNext();
                }

                @Override
                public Entry<K, V> next() {
                    return unwrapEntry(itor.next());
                }

                @Override
                public void remove() {
                    itor.remove();
                }
            };
        }

        @Override
        public int size() {
            return EquivalenceMap.this.size();
        }

    }

    @SuppressWarnings("unchecked")
    private Wrapper<K> wrap(final Object key) {
        return equivalence.wrap((K) key);
    }

    @SuppressWarnings("unchecked")
    private K unwrap(final Wrapper<? super K> key) {
        return (K) key.get();
    }

    private Map.Entry<K, V> unwrapEntry(final Map.Entry<Wrapper<? super K>, V> e) {

        return new Map.Entry<K, V>() {

            @Override
            public K getKey() {
                return unwrap(e.getKey());
            }

            @Override
            public V getValue() {
                return e.getValue();
            }

            @Override
            public V setValue(V value) {
                return e.setValue(value);
            }

            @Override
            public String toString() {
                return getKey() + "=" + getValue();
            }

            @Override
            public int hashCode() {
                return equivalence.hash(getKey()) ^ Objects.hashCode(getValue());
            }

            @SuppressWarnings("unchecked")
            @Override
            public boolean equals(final Object o) {
                if (o instanceof Map.Entry) {
                    final Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                    return equivalence.equivalent(getKey(), (K) e.getKey()) && Objects.equals(getValue(), e.getValue());
                }

                return false;
            }
        };

    }

}
