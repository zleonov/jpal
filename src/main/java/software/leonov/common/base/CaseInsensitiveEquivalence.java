package software.leonov.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Locale;

import com.google.common.base.Equivalence;

/**
 * An {@code Equivalence} which determines if two {@code CharSequence}s are equivalent ignoring case differences.
 * <p>
 * This {@code Equivalence} can be used to construct a {@code Map} which compares its keys in a case-insensitive manner:
 * 
 * <pre>
 * final Map&lt;String, ...&gt; m = new EquivalenceMap<>(CaseInsensitiveEquivalence.usingDefaultLocale());
 * </pre>
 * 
 * 
 * @author Zhenya Leonov
 */
final public class CaseInsensitiveEquivalence extends Equivalence<CharSequence> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Locale locale;

    private CaseInsensitiveEquivalence(final Locale locale) {
        checkNotNull(locale, "locale == null");
        this.locale = locale;
    }

    /**
     * Returns a new {@code CaseInsensitiveEquivalence} which uses the default {@code Locale} to determine if two
     * {@code CharSequence}s are equivalent ignoring case differences.
     * 
     * @return a new {@code CaseInsensitiveEquivalence} which uses the default {@code Locale} to determine if two
     *         {@code CharSequence}s are equivalent ignoring case differences
     */
    public static CaseInsensitiveEquivalence usingDefaultLocale() {
        return new CaseInsensitiveEquivalence(Locale.getDefault());
    }

    /**
     * Returns a new {@code CaseInsensitiveEquivalence} which uses the specified {@code Locale} to determine if two
     * {@code CharSequence}s are equivalent ignoring case differences.
     * 
     * @param locale the locale to use to determine if two {@code CharSequence}s are equivalent ignoring case differences
     * @return a new {@code CaseInsensitiveEquivalence} which uses the specified {@code Locale} to determine if two
     *         {@code CharSequence}s are equivalent ignoring case differences
     */
    public static CaseInsensitiveEquivalence using(final Locale locale) {
        checkNotNull(locale, "locale == null");
        return new CaseInsensitiveEquivalence(locale);
    }

    @Override
    protected boolean doEquivalent(final CharSequence left, final CharSequence right) {
        return left.toString().toLowerCase(locale).equals(right.toString().toLowerCase(locale));
    }

    @Override
    protected int doHash(final CharSequence s) {
        return s.toString().toLowerCase(locale).hashCode();
    }

}
