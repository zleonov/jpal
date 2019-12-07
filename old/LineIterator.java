package software.leonov.common.io;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import software.leonov.common.util.Exceptions;

final public class LineIterator implements Iterable<String>, Iterator<String>, AutoCloseable {

    private final BufferedReader buffr;
    private String next;
    private boolean first = true;

    LineIterator(final Reader r) throws IOException {
        checkNotNull(r, "r == null");
        buffr = r instanceof BufferedReader ? (BufferedReader) r : new BufferedReader(r);
        next = buffr.readLine();
    }

    @Override
    public void close() throws Exception {
        buffr.close();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public String next() {
        if (next == null)
            throw new NoSuchElementException();

        final String line = next;
        
        try {
            next = buffr.readLine();
        } catch (final IOException e) {
            Exceptions.throwUnchecked(e);
        }
        
        return line;
    }

    public static void main(String[] args) throws Exception {
        final StringReader r = new StringReader("line1\nline2\nline3");

        try (final LineIterator li = new LineIterator(r)) {
            li.forEach(System.out::println);
        }
    }

    @Override
    public Iterator<String> iterator() {
        checkState(first);
        first = false;
        return this;
    }

}
