package net.javatoday.common.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * More static utility methods which operate on or return {@link Map}s.
 * 
 * @author Zhenya Leonov
 * @see Maps
 */
public class MoreMaps {

    private MoreMaps() {
    }

    /**
     * Creates an empty {@code LinkedHashMap} with the specified ordering mode.
     * 
     * @param accessOrder the ordering mode - {@code true} for access-order, {@code false} for insertion-order
     * @return an empty {@code LinkedHashMap} with the specified ordering mode
     * @see LinkedHashMap
     */
    public static <K, V> Map<K, V> newLinkedHashMap(final boolean accessOrder) {
        return new LinkedHashMap<K, V>(16, .75F, accessOrder);
    }

    /**
     * Creates a {@code LinkedHashMap} with the specified ordering mode, containing the same mappings as the specified map.
     * 
     * @param accessOrder the ordering mode - {@code true} for access-order, {@code false} for insertion-order
     * @param m           the map whose mappings this map should contain
     * @return a {@code LinkedHashMap} with the specified ordering mode, containing the same mappings as the specified map
     * @see LinkedHashMap
     */
    public static <K, V> Map<K, V> newLinkedHashMap(final boolean accessOrder, final Map<? extends K, ? extends V> m) {
        checkNotNull(m, "m == null");
        final Map<K, V> map = new LinkedHashMap<K, V>(Math.max((int) (m.size() / .75F) + 1, 16), .75F, accessOrder);
        map.putAll(m);
        return map;
    }

    /**
     * Creates an empty {@code LinkedHashMap} with the specified ordering mode and enough capacity to hold the specified
     * number of entries without rehashing.
     * 
     * @param expectedSize the expected size
     * @param accessOrder  the ordering mode - {@code true} for access-order, {@code false} for insertion-order
     * @return an empty {@code LinkedHashMap} with the specified ordering mode and enough capacity to hold the specified
     *         number of entries without rehashing
     * @see LinkedHashMap
     */
    public static <K, V> Map<K, V> newLinkedHashMapWithExpectedSize(final int expectedSize, final boolean accessOrder) {
        checkArgument(expectedSize >= 0);
        return new LinkedHashMap<K, V>(Math.max(expectedSize * 2, 16), .75F, accessOrder);
    }

}
