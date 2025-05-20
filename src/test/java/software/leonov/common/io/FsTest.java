package software.leonov.common.io;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static software.leonov.common.io.FileWalker.VisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import software.leonov.common.base.MessageDigests;

class FsTest {

    private final static List<File> TEMP_FILES = Lists.newLinkedList();

    @BeforeAll
    static void setUpBeforeClass() throws Exception {

    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
        for (final File f : TEMP_FILES)
            f.delete();
    }

    @Test
    void testFileWalkerVsFileVisitor() throws IOException {
        final ArrayList<File> actual = Lists.newArrayList();
        final ArrayList<File> expected = Lists.newArrayList();

        final File start = new File(".");
        final int maxDepth = 3;

        Fs.walkFileTree(start, maxDepth, new SimpleFileWalker<Void>() {

            @Override
            public VisitResult visitFile(final File file) throws IOException {
                checkNotNull(file, "file == null");
                actual.add(file);
                return CONTINUE;
            }
        });

        java.nio.file.Files.walkFileTree(start.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), maxDepth, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Objects.requireNonNull(file);
                Objects.requireNonNull(attrs);
                expected.add(file.toFile());
                return FileVisitResult.CONTINUE;
            }

        });

        assertEquals(expected, actual);
    }

    @Test
    void testToStringBenchmarkVsGuavaAndJava() throws URISyntaxException, IOException {
        final File fin = getResourceAsFile("War and Peace.txt");

        final File tmp = createTempFile(new File("d:\\"));
        Files.asCharSink(tmp, Charsets.UTF_8).write(Strings.repeat(Files.asCharSource(fin, Charsets.UTF_8).read(), 200));

        final Stopwatch watch = Stopwatch.createUnstarted();

        watch.start();
        Files.asCharSource(tmp, Charsets.UTF_8).read();
        watch.stop();
        final long guava = watch.elapsed(TimeUnit.MILLISECONDS);

        watch.reset();

        watch.start();
        Fs.read(tmp, Charsets.UTF_8);
        watch.stop();
        final long jpal = watch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println("Fs.read(File) vs Files.asCharSource(File, Charsets.UTF_8).read():");
        System.out.println("jpal : " + jpal);
        System.out.println("guava: " + guava);
        System.out.println("Percentage Difference: " + percentageDifference(guava, jpal) * 100 + "%");
        System.out.println();
    }

    @Test
    void testwriteBenchmarkVsGuava() throws URISyntaxException, IOException {
        final File fin = getResourceAsFile("War and Peace.txt");

        final int numLines = 66055 * 100;

        final String content = Files.asCharSource(fin, Charsets.UTF_8).read();
        final List<String> characters = Lists.newArrayList();

        for (final Character c : Lists.charactersOf(content))
            characters.add(c.toString());

        Collections.shuffle(characters);

        final Iterable<String> lines = Iterables.limit(Iterables.cycle(characters), numLines);

        final File tmp = createTempFile(new File("d:\\"));
        final File tmp2 = createTempFile(new File("d:\\"));

        final Stopwatch watch = Stopwatch.createUnstarted();

        watch.start();
        Files.asCharSink(tmp, Charsets.UTF_8).writeLines(lines);
        watch.stop();
        final long guava = watch.elapsed(TimeUnit.MILLISECONDS);

        watch.reset();

        watch.start();
        Fs.write(lines, tmp2);
        watch.stop();
        final long jpal = watch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println("Fs.write(Iterable, File) vs Files.asCharSink(File, Charsets.UTF_8).write(lines):");
        System.out.println("jpal : " + jpal);
        System.out.println("guava: " + guava);
        System.out.println("Percentage Difference: " + percentageDifference(guava, jpal) * 100 + "%");
        System.out.println();
    }

    public float percentageDifference(final long t1, final long t2) {
        return Math.abs(t1 - t2) / ((t1 + t2) / 2f);
    }

    @Test
    void testAppendCharSequenceFileExistsVsGuava() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");
        final String content = "The quick brown fox jumps over the lazy dog";

        final File tmp1 = createTempFileAndCopy(fin);
        final File tmp2 = createTempFileAndCopy(fin);

        Files.asCharSink(tmp1, Charsets.UTF_8, FileWriteMode.APPEND).write(content);
        Fs.append(content, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testAppendCharSequenceFileExistsVsJava7() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");
        final String content = "The quick brown fox jumps over the lazy dog";

        final File tmp1 = createTempFileAndCopy(fin);
        final File tmp2 = createTempFileAndCopy(fin);

        java.nio.file.Files.write(tmp1.toPath(), content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Fs.append(content, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testAppendCharSequenceFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        final File tmp1 = createTempFile();
        final File tmp2 = createTempFile();

        Files.asCharSink(tmp1, Charsets.UTF_8, FileWriteMode.APPEND).write(content);
        Fs.append(content, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testAppendCharSequenceFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        final File tmp1 = createTempFile();
        final File tmp2 = createTempFile();

        java.nio.file.Files.write(tmp1.toPath(), content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Fs.append(content, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testAppendCharSequenceWithCharsetsFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final File tmp1 = createTempFile();
            final File tmp2 = createTempFile();

            java.nio.file.Files.write(tmp1.toPath(), content.getBytes(charset), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Fs.append(content, tmp2, charset);

            assertTrue(Files.equal(tmp1, tmp2));
        }
    }

    @Test
    void testAppendCharSequenceWithCharsetsFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final File tmp1 = createTempFile();
            final File tmp2 = createTempFile();

            Files.asCharSink(tmp1, charset, FileWriteMode.APPEND).write(content);
            Fs.append(content, tmp2, charset);

            assertTrue(Files.equal(tmp1, tmp2));
        }
    }

    @Test
    void testAppendLinesFileExistsVsGuava() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFileAndCopy(fin);
        final File tmp2 = createTempFileAndCopy(fin);

        Files.asCharSink(tmp1, Charsets.UTF_8, FileWriteMode.APPEND).writeLines(lines);
        Fs.append(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testAppendLinesFileExistsVsJava7() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFileAndCopy(fin);
        final File tmp2 = createTempFileAndCopy(fin);

        java.nio.file.Files.write(tmp1.toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Fs.append(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testAppendLinesFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFile();
        final File tmp2 = createTempFile();

        Files.asCharSink(tmp1, Charsets.UTF_8, FileWriteMode.APPEND).writeLines(lines);
        Fs.append(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testAppendLinesFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFile();
        final File tmp2 = createTempFile();

        java.nio.file.Files.write(tmp1.toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Fs.append(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testAppendLinesWithCharsetsFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final File tmp1 = createTempFile();
            final File tmp2 = createTempFile();

            java.nio.file.Files.write(tmp1.toPath(), lines, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Fs.append(lines, tmp2, charset);

            assertTrue(Files.equal(tmp1, tmp2));
        }
    }

    @Test
    void testAppendLinesWithCharsetsFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final File tmp1 = createTempFile();
            final File tmp2 = createTempFile();

            Files.asCharSink(tmp1, charset, FileWriteMode.APPEND).writeLines(lines);
            Fs.append(lines, tmp2, charset);

            assertTrue(Files.equal(tmp1, tmp2));
        }
    }

    @Test
    void testWriteCharSequenceFileExistsVsGuava() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");
        final String content = "The quick brown fox jumps over the lazy dog";

        final File tmp1 = createTempFileAndCopy(fin);
        final File tmp2 = createTempFileAndCopy(fin);

        Files.asCharSink(tmp1, Charsets.UTF_8).write(content);
        Fs.write(content, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testWriteCharSequenceFileExistsVsJava7() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");
        final String content = "The quick brown fox jumps over the lazy dog";

        final File tmp1 = createTempFileAndCopy(fin);
        final File tmp2 = createTempFileAndCopy(fin);

        java.nio.file.Files.write(tmp1.toPath(), content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Fs.write(content, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testWriteCharSequenceFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        final File tmp1 = createTempFile();
        final File tmp2 = createTempFile();

        Files.asCharSink(tmp1, Charsets.UTF_8).write(content);
        Fs.write(content, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testWriteCharSequenceFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        final File tmp1 = createTempFile();
        final File tmp2 = createTempFile();

        java.nio.file.Files.write(tmp1.toPath(), content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Fs.write(content, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testWriteCharSequenceWithCharsetsFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final File tmp1 = createTempFile();
            final File tmp2 = createTempFile();

            java.nio.file.Files.write(tmp1.toPath(), content.getBytes(charset), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            Fs.write(content, tmp2, charset);

            assertTrue(Files.equal(tmp1, tmp2));
        }
    }

    @Test
    void testWriteCharSequenceWithCharsetsFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final File tmp1 = createTempFile();
            final File tmp2 = createTempFile();

            Files.asCharSink(tmp1, charset).write(content);
            Fs.write(content, tmp2, charset);

            assertTrue(Files.equal(tmp1, tmp2));
        }
    }

    @Test
    void testwriteFileExistsVsGuava() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFileAndCopy(fin);
        final File tmp2 = createTempFileAndCopy(fin);

        Files.asCharSink(tmp1, Charsets.UTF_8).writeLines(lines);
        Fs.write(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testwriteFileExistsVsJava7() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFileAndCopy(fin);
        final File tmp2 = createTempFileAndCopy(fin);

        java.nio.file.Files.write(tmp1.toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Fs.write(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testwriteFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFile();
        final File tmp2 = createTempFile();

        Files.asCharSink(tmp1, Charsets.UTF_8).writeLines(lines);
        Fs.write(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testwriteFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFile();
        final File tmp2 = createTempFile();

        java.nio.file.Files.write(tmp1.toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Fs.write(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testwriteWithCharsetsFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final File tmp1 = createTempFile();
            final File tmp2 = createTempFile();

            java.nio.file.Files.write(tmp1.toPath(), lines, charset, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            Fs.write(lines, tmp2, charset);

            assertTrue(Files.equal(tmp1, tmp2));
        }
    }

    @Test
    void testwriteWithCharsetsFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final File tmp1 = createTempFile();
            final File tmp2 = createTempFile();

            Files.asCharSink(tmp1, charset).writeLines(lines);
            Fs.write(lines, tmp2, charset);

            assertTrue(Files.equal(tmp1, tmp2));
        }
    }

    @Test
    void testToStringVsGuava() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final String expected = Files.asCharSource(fin, charset).read();
            final String actual = Fs.read(fin, charset);
            assertEquals(expected, actual);
        }
    }

    @Test
    void testToStringVsJava7() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final String expected = new String(java.nio.file.Files.readAllBytes(fin.toPath()), charset);
            final String actual = Fs.read(fin, charset);
            assertEquals(expected, actual);
        }
    }

    @Test
    void testDigestVsGuava() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");

        final byte[] bytes = Fs.getDigest(fin, MessageDigests.md5());
        final HashCode hashCode = Files.asByteSource(fin).hash(Hashing.md5());
        assertArrayEquals(hashCode.asBytes(), bytes);
        assertEquals(hashCode.toString(), MessageDigests.toString(bytes));
    }

    private static File getResourceAsFile(final String name) throws URISyntaxException {
        final URL url = FsTest.class.getResource(name);
        Preconditions.checkArgument(url != null, "cannot find %s", name);
        return new File(url.toURI());
    }

    private static File createTempFileAndCopy(final File from) throws IOException {
        final File tmp = createTempFile();
        Files.copy(from, tmp);
        return tmp;
    }

    private static File createTempFile() throws IOException {
        return createTempFile(TEMP_DIR);
    }

    private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));

    private static File createTempFile(final File tempDir) throws IOException {
        final File f = File.createTempFile("tmp", null, tempDir);
        f.deleteOnExit();
        TEMP_FILES.add(f);
        return f;
    }

}
