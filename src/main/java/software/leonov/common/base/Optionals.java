package software.leonov.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.Iterables;

/**
 * Static utility methods for working with {@link Optional}s.
 * 
 * @author Zhenya Leonov
 */
public final class Optionals {

//    /**
//     * Return the {@code first} optional if it is present or the {@code other} optional if it is not.
//     * <p>
//     * <b>Java 9 equivalent:</b> {@link Optional#or(Supplier) Optional.or(Supplier&lt;? extends Optional&lt;? extends
//     * T&gt;&gt;)}
//     * 
//     * @param <T>   the type of optional value
//     * @param first the first optional
//     * @param other the second choice optional
//     * @return the {@code first} optional if it is present or the {@code other} optional if it is not
//     */
//    @SuppressWarnings("unchecked")
//    public static <T> Optional<T> or(final Optional<? extends T> first, final Optional<? extends T> other) {
//        checkNotNull(first, "first == null");
//        checkNotNull(other, "other == null");
//        if (first.isPresent()) {
//            return (Optional<T>) first;
//        } else {
//            return (Optional<T>) other;
//        }
//
//    }

    /**
     * Return the {@code first} optional if it is present or the optional provided by the {@code other} supplier if it is
     * not.
     * <p>
     * <b>Java 9 equivalent:</b> {@link Optional#or(Supplier) Optional.or(Supplier&lt;? extends Optional&lt;? extends
     * T&gt;&gt;)}
     * 
     * @param <T>   the type of optional value
     * @param first the first optional
     * @param other the supplier that produces the second choice optional
     * @return the {@code first} optional if it is present or the optional provided by the {@code other} supplier if it is
     *         not
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> or(final Optional<? extends T> first, final Supplier<? extends Optional<? extends T>> other) {
        checkNotNull(first, "first == null");
        checkNotNull(other, "other == null");
        if (first.isPresent()) {
            return (Optional<T>) first;
        } else
            return (Optional<T>) checkNotNull(other.get(), "other.get() == null");
    }

    /**
     * Returns the first {@link Optional#isPresent() present} {@code Optional} from the specified {@code Optional}s or
     * {@link Optional#empty()} otherwise.
     * 
     * @param <T>    the type of values held by the {@code Optional}s
     * @param first  the {@code Optional}
     * @param second the second {@code Optional}
     * @param rest   additional {@code Optional}s
     * @return the first {@link Optional#isPresent() present} {@code Optional} from the specified {@code Optional}s or
     *         {@link Optional#empty()} otherwise
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> Optional<T> firstPresent(final Optional<? extends T> first, final Optional<? extends T> second, final Optional<? extends T>... rest) {
        checkNotNull(first, "first == null");
        checkNotNull(second, "second == null");
        checkNotNull(rest, "rest == null");

        // return Streams.concat(Stream.of(first, second), Stream.of(rest)).filter(Optional::isPresent).findFirst();

        if (first.isPresent())
            return (Optional<T>) first;

        if (second.isPresent())
            return (Optional<T>) second;

        for (final Optional<? extends T> opt : rest)
            if (opt.isPresent())
                return (Optional<T>) opt;

        return Optional.empty();
    }

    /**
     * Returns all {@link Optional#isPresent() present} values of the specified {@code Optional} instances.
     * 
     * @param <T>       the type of optional value
     * @param optionals the specified {@code Optional} instances
     * @return all {@link Optional#isPresent() present} values of the specified {@code Optional} instances
     */
    public static <T> Iterable<T> present(final Iterable<? extends Optional<? extends T>> optionals) {
        checkNotNull(optionals, "optionals == null");
        return Iterables.transform(Iterables.filter(optionals, Optional::isPresent), Optional::get);
    }

    /**
     * Performs the given action if the value is present, otherwise performs the given empty-based action.
     * <p>
     * <b>Java 9 equivalent:</b> {@link Optional#ifPresentOrElse(Consumer, Runnable) Optional.ifPresentOrElse(Consumer&lt;?
     * super T&gt;, Runnable)}
     *
     * @param <T>           the type of the value
     * @param optional      the {@code Optional} to check
     * @param presentAction the action to perform if the value is present
     * @param emptyAction   the action to perform if the value is absent
     */
    public static <T> void ifPresentOrElse(final Optional<? extends T> optional, Consumer<? super T> presentAction, final Runnable emptyAction) {
        if (optional.isPresent())
            presentAction.accept(optional.get());
        else
            emptyAction.run();
    }

}