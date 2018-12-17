package net.javatoday.common.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;

/**
 * Static utility methods for working with {@code Collection}s.
 * 
 * @author Zhenya Leonov
 */
final public class MoreCollections {

    private MoreCollections() {
    }

    /**
     * Returns {@code true} if a collection contains all of the specified elements.
     * 
     * @param c        the collection
     * @param elements the specified elements
     * @return {@code true} if a collection contains all of the specified elements
     */
    public static boolean containsAll(final Collection<?> c, final Iterable<?> elements) {
        checkNotNull(c, "c == null");
        checkNotNull(elements, "elements == null");
        return elements instanceof Collection ? c.containsAll((Collection<?>) elements) : containsAll(c, elements.iterator());
    }

    /**
     * Returns {@code true} if a collection contains all of the specified elements.
     * <p>
     * Guava equivalent: {@code Iterators.all(Iterator, Predicates.in(Collection))}
     * 
     * @param c        the collection
     * @param elements the specified elements
     * @return {@code true} if a collection contains all of the specified elements
     */
    public static boolean containsAll(final Collection<?> c, final Iterator<?> elements) {
        checkNotNull(c, "c == null");
        checkNotNull(elements, "elements == null");

        try {
            while (elements.hasNext())
                if (!c.contains(elements.next()))
                    return false;
        } catch (final ClassCastException e) {
            return false;
        } catch (final NullPointerException e) {
            return false;
        }
        return true;
    }

}
