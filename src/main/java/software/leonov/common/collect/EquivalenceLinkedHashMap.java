package software.leonov.common.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;

/**
 * 
 * @author Zhenya Leonov
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class EquivalenceLinkedHashMap<K, V> extends AbstractMap<K, V> {

    private final Map<Equivalence.Wrapper<? super K>, V> delegate;
    private final Equivalence<? super K> equivalence;

    private Set<Map.Entry<K, V>> entrySet;

    private final class DelegateMap extends java.util.LinkedHashMap<Wrapper<? super K>, V> {
        private static final long serialVersionUID = 1L;

        public DelegateMap(final int initialCapacity, final float loadFactor, final boolean accessOrder) {
            super(initialCapacity, loadFactor, accessOrder);
        }

        @Override
        protected boolean removeEldestEntry(final Map.Entry<Wrapper<? super K>, V> eldest) {
            return EquivalenceLinkedHashMap.this.removeEldestEntry(createEntry(eldest));
        }
    }

    public EquivalenceLinkedHashMap(final Equivalence<? super K> equivalence) {
        this(equivalence, 16);
    }

    public EquivalenceLinkedHashMap(final Equivalence<? super K> equivalence, final int initialCapacity) {
        this(equivalence, initialCapacity, .75f);
    }

    public EquivalenceLinkedHashMap(final Equivalence<? super K> equivalence, final int initialCapacity, final float loadFactor) {
        this(equivalence, initialCapacity, .75f, false);
    }

    public EquivalenceLinkedHashMap(final Equivalence<? super K> equivalence, final int initialCapacity, final float loadFactor, final boolean accessOrder) {
        checkNotNull(equivalence, "equivalence == null");
        delegate = new DelegateMap(initialCapacity, loadFactor, accessOrder);
        this.equivalence = equivalence;
    }

    public EquivalenceLinkedHashMap(final Equivalence<? super K> equivalence, final Map<? extends K, ? extends V> m) {
        this(equivalence, checkNotNull(m, "m == null").size(), .75f, false);
        putAll(m);
    }

    public Equivalence<? super K> getEquivalence() {
        return equivalence;
    }

    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean containsKey(final Object o) {
        return delegate.containsKey(createKey(o));
    }

    public boolean containsValue(final Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet == null ? entrySet = new EntrySet() : entrySet;
    }

    @Override
    public V get(final Object o) {
        return delegate.get(createKey(o));
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public V put(final K key, final V value) {
        return delegate.put(createKey(key), value);
    }

    @Override
    public V remove(final Object o) {
        return delegate.remove(createKey(o));
    }

    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return false;
    }

    public int size() {
        return delegate.size();
    }

    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public void clear() {
            EquivalenceLinkedHashMap.this.clear();
        }

        @Override
        public boolean contains(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry<?, ?> e = (Entry<?, ?>) o;
                final Wrapper<K> key = createKey(e.getKey());
                return delegate.containsKey(key) && Objects.equals(delegate.get(key), e.getValue());
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
            final Set<K> set = Collections.newSetFromMap(new EquivalenceLinkedHashMap<>(getEquivalence()));

            for (final Object o : c)
                set.add((K) o);

            return super.retainAll(set);
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
                    return createEntry(itor.next());
                }

                @Override
                public void remove() {
                    itor.remove();
                }
            };
        }

        @Override
        public int size() {
            return EquivalenceLinkedHashMap.this.size();
        }

    }

    @SuppressWarnings("unchecked")
    private Wrapper<K> createKey(final Object key) {
        return equivalence.wrap((K) key);
    }

    private Map.Entry<K, V> createEntry(final Map.Entry<Wrapper<? super K>, V> e) {

        return new Map.Entry<K, V>() {

            @Override
            public K getKey() {
                return get(e.getKey());
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

    @SuppressWarnings("unchecked")
    private K get(final Wrapper<? super K> key) {
        return (K) key.get();
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new EquivalenceLinkedHashMap<>(Equivalence.equals());
        map.put(null,null);
        System.out.println(map.keySet());
        System.out.println(map.entrySet());

    }

}
