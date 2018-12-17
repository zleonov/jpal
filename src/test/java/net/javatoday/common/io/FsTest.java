package net.javatoday.common.io;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.javatoday.common.io.FileWalker.VisitResult.CONTINUE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import net.javatoday.common.base.MessageDigests;

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
        final int maxDepth = 1000;

        Fs.walkFileTree(start, maxDepth, new SimpleFileWalker<Void>() {

            @Override
            public VisitResult visitFile(final File file) throws IOException {
                checkNotNull(file, "file == null");
                actual.add(file);
                return CONTINUE;
            }
        });

        java.nio.file.Files.walkFileTree(start.toPath(), EnumSet.noneOf(FileVisitOption.class), maxDepth, new SimpleFileVisitor<Path>() {

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

//    @Test
//    void testToStringBenchmarkVsGuava() throws URISyntaxException, IOException {
//        final File fin = getResourceAsFile("War and Peace.txt");
//
//        //final CharSource guava = Files.asCharSource(fin, Charsets.UTF_8);
//        final Stopwatch watch = Stopwatch.createUnstarted();
//
//        watch.start();
//        Files.asCharSource(fin, Charsets.UTF_8).toString();
//        watch.stop();
//        final long guavaTime = watch.elapsed(TimeUnit.MILLISECONDS);
//
//        watch.reset();
//
//        watch.start();
//        Fs.toString(fin, Charsets.UTF_8);
//        watch.stop();
//        final long myTime = watch.elapsed(TimeUnit.MILLISECONDS);
//
//        System.out.println("myTime: " + myTime);
//        System.out.println("guavaTime: " + guavaTime);
//
//        System.out.println(percentageDifference(guavaTime, myTime));
//
//    }
//    
//    @Test
//    void testWriteLinesBenchmarkVsGuava() throws URISyntaxException, IOException {
//        final File fin = getResourceAsFile("War and Peace.txt");
//
//        final int numLines = 66055 * 100;
////
////        final List<String> readLines = Files.readLines(fin, Charsets.UTF_8);
//
////        
////        Lists.chara
//        
//        final String content = Files.asCharSource(fin, Charsets.UTF_8).read();
//        final List<String> characters = Lists.newArrayList();
//        
//        for(final Character c: Lists.charactersOf(content))
//            characters.add(c.toString());
//        
//        Collections.shuffle(characters);
//        
//        final Iterable<String> lines = Iterables.limit(Iterables.cycle(characters), numLines);
//         
//        final File tmp = createTempFile(new File("d:\\"));
//        final File tmp2 = createTempFile(new File("d:\\"));
//
//        final CharSink guava = Files.asCharSink(tmp, Charsets.UTF_8);
//        final Stopwatch watch = Stopwatch.createUnstarted();
//
//        watch.start();
//        guava.writeLines(lines);
//        watch.stop();
//        final long guavaTime = watch.elapsed(TimeUnit.MILLISECONDS);
//
//        watch.reset();
//
//        watch.start();
//        Fs.write(lines, tmp2);
//        watch.stop();
//        final long myTime = watch.elapsed(TimeUnit.MILLISECONDS);
//
//        System.out.println("myTime: " + myTime);
//        System.out.println("guavaTime: " + guavaTime);
//
//        System.out.println(percentageDifference(guavaTime, myTime));
//
//    }

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
    void testWriteLinesFileExistsVsGuava() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFileAndCopy(fin);
        final File tmp2 = createTempFileAndCopy(fin);

        Files.asCharSink(tmp1, Charsets.UTF_8).writeLines(lines);
        Fs.write(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testWriteLinesFileExistsVsJava7() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFileAndCopy(fin);
        final File tmp2 = createTempFileAndCopy(fin);

        java.nio.file.Files.write(tmp1.toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Fs.write(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testWriteLinesFileNotExistsVsGuava() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFile();
        final File tmp2 = createTempFile();

        Files.asCharSink(tmp1, Charsets.UTF_8).writeLines(lines);
        Fs.write(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testWriteLinesFileNotExistsVsJava7() throws IOException, URISyntaxException {
        final List<String> lines = ImmutableList.of("The quick brown fox", "jumps over the lazy dog");

        final File tmp1 = createTempFile();
        final File tmp2 = createTempFile();

        java.nio.file.Files.write(tmp1.toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        Fs.write(lines, tmp2);

        assertTrue(Files.equal(tmp1, tmp2));
    }

    @Test
    void testWriteLinesWithCharsetsFileNotExistsVsJava7() throws IOException, URISyntaxException {
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
    void testWriteLinesWithCharsetsFileNotExistsVsGuava() throws IOException, URISyntaxException {
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
            final String actual = Fs.toString(fin, charset);
            assertEquals(expected, actual);
        }
    }

    @Test
    void testToStringVsJava7() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");

        for (final Charset charset : ImmutableList.of(StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8)) {
            final String expected = new String(java.nio.file.Files.readAllBytes(fin.toPath()), charset);
            final String actual = Fs.toString(fin, charset);
            assertEquals(expected, actual);
        }
    }

    @Test
    void testDigestVsGuava() throws IOException, URISyntaxException {
        final File fin = getResourceAsFile("War and Peace.txt");

        final byte[] bytes = Fs.digest(fin, MessageDigests.md5());
        final HashCode hashCode = Files.asByteSource(fin).hash(Hashing.md5());
        assertArrayEquals(hashCode.asBytes(), bytes);
        assertEquals(hashCode.toString(), MessageDigests.toString(bytes));
    }

    private static File getResourceAsFile(final String name) throws URISyntaxException {
        final URI uri = FsTest.class.getResource(name).toURI();
        Preconditions.checkArgument(uri != null, "cannot find %s", name);
        return new File(uri);
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
