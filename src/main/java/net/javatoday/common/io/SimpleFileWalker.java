package net.javatoday.common.io;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.javatoday.common.io.FileWalker.VisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.SimpleFileVisitor;

/**
 * A basic implementation of {@code FileWalker} which visits all files and directories, rethrows any
 * {@code IOException}s, and returns a {@code null} result.
 * <p>
 * Methods in this class may be overridden to customize the implementation.
 * <p>
 * <b>Warning:</b> The {@code java.io.File class} provides no platform-independent way to detect symbolic links, as such
 * there is no way to ensure a symbolic link to a directory is not followed when traversing a file tree. In the presence
 * of symbolic links, you may encounter files outside the starting directory, or even end up in an infinite loop.
 * <p>
 * <b>Note:</b> While this class is not deprecated, Java 7+ users are highly encouraged to use {@link SimpleFileVisitor}
 * in the <a href="https://www.oracle.com/technetwork/articles/javase/nio-139333.html">The Java NIO.2 File System
 * introduced in JDK 7</a>.
 * 
 * @param <R> the type of result returned by this {@code FileWalker}
 * @author Zhenya Leonov
 */
public class SimpleFileWalker<R> implements FileWalker<R> {

    @Override
    public VisitResult preVisitDirectory(final File dir) {
        checkNotNull(dir, "dir == null");
        return CONTINUE;
    }

    @Override
    public VisitResult postVisitDirectory(final File dir) {
        checkNotNull(dir, "dir == null");
        return CONTINUE;
    }

    @Override
    public VisitResult visitFile(final File file) throws IOException {
        checkNotNull(file, "file == null");
        return CONTINUE;
    }

    @Override
    public VisitResult visitFileFailed(final File file, final IOException ex) throws IOException {
        checkNotNull(file, "file == null");
        checkNotNull(ex, "ex == null");
        throw ex;
    }

    @Override
    public R getResult() {
        return null;
    }

}
