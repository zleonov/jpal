package net.javatoday.common.io;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.base.Throwables;
import com.google.common.io.Closer;

final public class AutoCloser implements AutoCloseable {
    Closer c;
    private Deque<AutoCloseable> stack = new ArrayDeque<>(4);

    @Override
    public void close() throws Exception {
        Throwable thrown = null;

        while (!stack.isEmpty()) {
            try {
                stack.removeFirst().close();
            } catch (final Throwable t) {
                if (thrown == null)
                    thrown = t;
                else
                    thrown.addSuppressed(t);
            }
        }

        if (thrown != null) {
            Throwables.throwIfUnchecked(thrown);
            throw (Exception) thrown;
        }
    }

    /**
     * Registers the given {@code resource} to be closed when this {@code AutoCloser} is {@link #close closed}.
     *
     * @return the given {@code resource}
     */
    public <T extends AutoCloseable> T register(final T resource) {
        if (resource != null) // is it correct to ignore null resources?
            stack.addFirst(resource);
        return resource;
    }

}
