package software.leonov.common.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
import com.google.common.io.MoreFiles;

class MorePathsTest {

    private final static List<Path> TEMP_PATHS = new LinkedList<>();

    @BeforeAll
    static void setUpBeforeClass() throws Exception {

    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {

        for (final Path p : TEMP_PATHS)
            java.nio.file.Files.delete(p);
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void testToStringBenchmarkVsGuava() throws URISyntaxException, IOException {
        final Path fin = getResourceAsFile("War and Peace.txt");

        final Path tmp = createTempPath(Paths.get("d:"));
        MoreFiles.asCharSink(tmp, Charsets.UTF_8).write(Strings.repeat(MoreFiles.asCharSource(fin, Charsets.UTF_8).read(), 200));

        final Path path = tmp;

        final Stopwatch watch = Stopwatch.createUnstarted();

        watch.start();
        MoreFiles.asCharSource(path, Charsets.UTF_8).read();
        watch.stop();
        final long guava = watch.elapsed(TimeUnit.MILLISECONDS);

        watch.reset();

        watch.start();
        Fs.read(path);
        watch.stop();
        final long jpal = watch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println("Fs.read(Path) vs MoreFiles.asCharSource(Path, Charsets.UTF_8).read():");
        System.out.println("jpal : " + jpal);
        System.out.println("guava: " + guava);
        System.out.println("Percentage Difference: " + percentageDifference(guava, jpal) * 100 + "%");
        System.out.println();

    }

    @Test
    void testwriteBenchmarkVsGuava() throws URISyntaxException, IOException {
        final Path fin = getResourceAsFile("War and Peace.txt");
        final int numLines = 66055 * 100;

        final String content = MoreFiles.asCharSource(fin, Charsets.UTF_8).read();
        final List<String> characters = Lists.newArrayList();

        for (final Character c : Lists.charactersOf(content))
            characters.add(c.toString());

        Collections.shuffle(characters);

        final Iterable<String> lines = Iterables.limit(Iterables.cycle(characters), numLines);

        final Path tmp1 = createTempPath(Paths.get("d:"));
        final Path tmp2 = createTempPath(Paths.get("d:"));

        final Stopwatch watch = Stopwatch.createUnstarted();

        watch.start();
        MoreFiles.asCharSink(tmp1, Charsets.UTF_8).writeLines(lines);
        watch.stop();
        final long guava = watch.elapsed(TimeUnit.MILLISECONDS);

        watch.reset();

        watch.start();
        Fs.write(lines, tmp2);
        watch.stop();
        final long jpal = watch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println("Fs.write(Iterable, Path) vs MoreFiles.asCharSink(Path, Charsets.UTF_8).write(lines):");
        System.out.println("jpal : " + jpal);
        System.out.println("guava: " + guava);
        System.out.println("Percentage Difference: " + percentageDifference(guava, jpal) * 100 + "%");
        System.out.println();
    }

    @Test
    void testwriteBenchmarkVsJava() throws URISyntaxException, IOException {
        final Path fin = getResourceAsFile("War and Peace.txt");
        final int numLines = 66055 * 100;

        final String content = MoreFiles.asCharSource(fin, Charsets.UTF_8).read();
        final List<String> characters = Lists.newArrayList();

        for (final Character c : Lists.charactersOf(content))
            characters.add(c.toString());

        Collections.shuffle(characters);

        final Iterable<String> lines = Iterables.limit(Iterables.cycle(characters), numLines);

        final Path tmp1 = createTempPath(Paths.get("d:"));
        final Path tmp2 = createTempPath(Paths.get("d:"));

        final Stopwatch watch = Stopwatch.createUnstarted();

        watch.start();
        java.nio.file.Files.write(tmp1, lines);
        watch.stop();
        final long java = watch.elapsed(TimeUnit.MILLISECONDS);

        watch.reset();

        watch.start();
        Fs.write(lines, tmp2);
        watch.stop();
        final long jpal = watch.elapsed(TimeUnit.MILLISECONDS);

        System.out.println("Fs.write(Iterable, Path) vs java.nio.file.Files.write(Path, Iterable):");
        System.out.println("jpal : " + jpal);
        System.out.println("guava: " + java);
        System.out.println("Percentage Difference: " + percentageDifference(java, jpal) * 100 + "%");
        System.out.println();
    }

    public float percentageDifference(final long t1, final long t2) {
        return Math.abs(t1 - t2) / ((t1 + t2) / 2f);
    }

    @Test
    void testAppendCharSequenceFileExistsVsGuava() throws IOException, URISyntaxException {
        final Path fin = getResourceAsFile("War and Peace.txt");
        final String content = "The quick brown fox jumps over the lazy dog";

        final Path tmp1 = createTempPathAndCopy(fin);
        final Path tmp2 = createTempPathAndCopy(fin);

        MoreFiles.asCharSink(tmp1, Charsets.UTF_8, StandardOpenOption.APPEND).write(content);
        Fs.append(content, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testAppendCharSequenceFileExistsVsJava7() throws IOException, URISyntaxException {
        final Path fin = getResourceAsFile("War and Peace.txt");
        final String content = "The quick brown fox jumps over the lazy dog";

        final Path tmp1 = createTempPathAndCopy(fin);
        final Path tmp2 = createTempPathAndCopy(fin);

        java.nio.file.Files.write(tmp1, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Fs.append(content, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testAppendCharSequenceFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        final Path tmp1 = createTempPath();
        final Path tmp2 = createTempPath();

        MoreFiles.asCharSink(tmp1, Charsets.UTF_8, StandardOpenOption.APPEND).write(content);
        Fs.append(content, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testAppendCharSequenceFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        final Path tmp1 = createTempPath();
        final Path tmp2 = createTempPath();

        java.nio.file.Files.write(tmp1, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Fs.append(content, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testAppendCharSequenceWithCharsetsFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final Path tmp1 = createTempPath();
            final Path tmp2 = createTempPath();

            java.nio.file.Files.write(tmp1, content.getBytes(charset), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Fs.append(content, tmp2, charset);

            assertTrue(MoreFiles.equal(tmp1, tmp2));
        }
    }

    @Test
    void testAppendCharSequenceWithCharsetsFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final Path tmp1 = createTempPath();
            final Path tmp2 = createTempPath();

            MoreFiles.asCharSink(tmp1, charset, StandardOpenOption.APPEND).write(content);
            Fs.append(content, tmp2, charset);

            assertTrue(MoreFiles.equal(tmp1, tmp2));
        }
    }

    @Test
    void testAppendLinesFileExistsVsGuava() throws IOException, URISyntaxException {
        final Path fin = getResourceAsFile("War and Peace.txt");
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final Path tmp1 = createTempPathAndCopy(fin);
        final Path tmp2 = createTempPathAndCopy(fin);

        MoreFiles.asCharSink(tmp1, Charsets.UTF_8, StandardOpenOption.APPEND).writeLines(lines);
        Fs.append(lines, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testAppendLinesFileExistsVsJava7() throws IOException, URISyntaxException {
        final Path fin = getResourceAsFile("War and Peace.txt");
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final Path tmp1 = createTempPathAndCopy(fin);
        final Path tmp2 = createTempPathAndCopy(fin);

        java.nio.file.Files.write(tmp1, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Fs.append(lines, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testAppendLinesFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final Path tmp1 = createTempPath();
        final Path tmp2 = createTempPath();

        MoreFiles.asCharSink(tmp1, Charsets.UTF_8, StandardOpenOption.APPEND).writeLines(lines);
        Fs.append(lines, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testAppendLinesFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final Path tmp1 = createTempPath();
        final Path tmp2 = createTempPath();

        java.nio.file.Files.write(tmp1, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Fs.append(lines, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testAppendLinesWithCharsetsFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final Path tmp1 = createTempPath();
            final Path tmp2 = createTempPath();

            java.nio.file.Files.write(tmp1, lines, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Fs.append(lines, tmp2, charset);

            assertTrue(MoreFiles.equal(tmp1, tmp2));
        }
    }

    @Test
    void testAppendLinesWithCharsetsFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final Path tmp1 = createTempPath();
            final Path tmp2 = createTempPath();

            MoreFiles.asCharSink(tmp1, charset, StandardOpenOption.APPEND).writeLines(lines);
            Fs.append(lines, tmp2, charset);

            assertTrue(MoreFiles.equal(tmp1, tmp2));
        }
    }

    @Test
    void testWriteCharSequenceFileExistsVsGuava() throws IOException, URISyntaxException {
        final Path fin = getResourceAsFile("War and Peace.txt");
        final String content = "The quick brown fox jumps over the lazy dog";

        final Path tmp1 = createTempPathAndCopy(fin);
        final Path tmp2 = createTempPathAndCopy(fin);

        MoreFiles.asCharSink(tmp1, Charsets.UTF_8).write(content);
        Fs.write(content, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testWriteCharSequenceFileExistsVsJava7() throws IOException, URISyntaxException {
        final Path fin = getResourceAsFile("War and Peace.txt");
        final String content = "The quick brown fox jumps over the lazy dog";

        final Path tmp1 = createTempPathAndCopy(fin);
        final Path tmp2 = createTempPathAndCopy(fin);

        java.nio.file.Files.write(tmp1, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Fs.write(content, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testWriteCharSequenceFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        final Path tmp1 = createTempPath();
        final Path tmp2 = createTempPath();

        MoreFiles.asCharSink(tmp1, Charsets.UTF_8).write(content);
        Fs.write(content, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testWriteCharSequenceFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        final Path tmp1 = createTempPath();
        final Path tmp2 = createTempPath();

        java.nio.file.Files.write(tmp1, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Fs.write(content, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testWriteCharSequenceWithCharsetsFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final Path tmp1 = createTempPath();
            final Path tmp2 = createTempPath();

            java.nio.file.Files.write(tmp1, content.getBytes(charset), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            Fs.write(content, tmp2, charset);

            assertTrue(MoreFiles.equal(tmp1, tmp2));
        }
    }

    @Test
    void testWriteCharSequenceWithCharsetsFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final String content = "The quick brown fox jumps over the lazy dog";

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final Path tmp1 = createTempPath();
            final Path tmp2 = createTempPath();

            MoreFiles.asCharSink(tmp1, charset).write(content);
            Fs.write(content, tmp2, charset);

            assertTrue(MoreFiles.equal(tmp1, tmp2));
        }
    }

    @Test
    void testwriteFileExistsVsGuava() throws IOException, URISyntaxException {
        final Path fin = getResourceAsFile("War and Peace.txt");
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final Path tmp1 = createTempPathAndCopy(fin);
        final Path tmp2 = createTempPathAndCopy(fin);

        MoreFiles.asCharSink(tmp1, Charsets.UTF_8).writeLines(lines);
        Fs.write(lines, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testwriteFileExistsVsJava7() throws IOException, URISyntaxException {
        final Path fin = getResourceAsFile("War and Peace.txt");
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final Path tmp1 = createTempPathAndCopy(fin);
        final Path tmp2 = createTempPathAndCopy(fin);

        java.nio.file.Files.write(tmp1, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Fs.write(lines, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testwriteFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final Path tmp1 = createTempPath();
        final Path tmp2 = createTempPath();

        MoreFiles.asCharSink(tmp1, Charsets.UTF_8).writeLines(lines);
        Fs.write(lines, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testwriteFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final Path tmp1 = createTempPath();
        final Path tmp2 = createTempPath();

        java.nio.file.Files.write(tmp1, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Fs.write(lines, tmp2);

        assertTrue(MoreFiles.equal(tmp1, tmp2));
    }

    @Test
    void testwriteWithCharsetsFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final Path tmp1 = createTempPath();
            final Path tmp2 = createTempPath();

            java.nio.file.Files.write(tmp1, lines, charset, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            Fs.write(lines, tmp2, charset);

            assertTrue(MoreFiles.equal(tmp1, tmp2));
        }
    }

    @Test
    void testwriteWithCharsetsFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final Path tmp1 = createTempPath();
            final Path tmp2 = createTempPath();

            MoreFiles.asCharSink(tmp1, charset).writeLines(lines);
            Fs.write(lines, tmp2, charset);

            assertTrue(MoreFiles.equal(tmp1, tmp2));
        }
    }

    @Test
    void testToStringVsGuava() throws IOException, URISyntaxException {
        final Path fin = getResourceAsFile("War and Peace.txt");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final String expected = MoreFiles.asCharSource(fin, charset).read();
            final String actual = Fs.read(fin, charset);
            assertEquals(expected, actual);
        }
    }

    @Test
    void testToStringVsJava7() throws IOException, URISyntaxException {
        final Path fin = getResourceAsFile("War and Peace.txt");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final String expected = new String(java.nio.file.Files.readAllBytes(fin), charset);
            final String actual = Fs.read(fin, charset);
            assertEquals(expected, actual);
        }
    }

    private static Path getResourceAsFile(final String name) throws URISyntaxException {
        final URI uri = Fs.class.getResource(name).toURI();
        Preconditions.checkArgument(uri != null, "cannot find %s", name);
        return Paths.get(uri);
    }

    private static Path createTempPathAndCopy(final Path from) throws IOException {
        final Path tmp = createTempPath();
        java.nio.file.Files.copy(from, tmp, StandardCopyOption.REPLACE_EXISTING);
        return tmp;
    }

    private static Path createTempPath() throws IOException {
        return createTempPath(Paths.get("d:\\"));
    }

    private static Path createTempPath(final Path directory) throws IOException {
        final Path p = java.nio.file.Files.createTempFile(directory, "tmp", null);
        TEMP_PATHS.add(p);
        return p;
    }

}
