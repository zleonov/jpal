package software.leonov.common.io;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.Closeables;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

class ZipTest {

    private static Path testDataZip;
    private static Path absoluteDataZip;
    private static Path temp;
    private static ZipFile zipFile;
    private static Path bibleTxt;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        testDataZip = getResourceAsPath("test-data.zip");
        absoluteDataZip = getResourceAsPath("absolute-test-data.zip");
        zipFile = Zip.open(testDataZip);

        temp = Files.createTempDirectory(null);

        bibleTxt = getResourceAsPath("bible.txt");
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        Closeables.close(zipFile, true);
        Files.delete(temp);
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
        MoreFiles.deleteDirectoryContents(temp, RecursiveDeleteOption.ALLOW_INSECURE);
    }

    @Test
    void readLines() throws ZipException, IOException {
        final ZipEntry entry = zipFile.getEntry("bible.txt");

        final List<String> expectedDefaultUTF_8 = Fs.readLines(bibleTxt);
        final List<String> linesDefaultUTF_8 = Zip.readLines(zipFile, entry);
        assertEquals(expectedDefaultUTF_8, linesDefaultUTF_8);

        for (final Charset charset : ImmutableList.of(ISO_8859_1, US_ASCII, UTF_16, UTF_16BE, UTF_16LE, UTF_8)) {
            final List<String> expected = Fs.readLines(bibleTxt, charset);
            final List<String> lines = Zip.readLines(zipFile, entry, charset);
            assertEquals(expected, lines);
        }
    }

    @Test
    void toByteArray() throws ZipException, IOException {
        final ZipEntry entry = zipFile.getEntry("bible.txt");
        final byte[] expected = Fs.toByteArray(bibleTxt);
        final byte[] bytes = Zip.toByteArray(zipFile, entry);
        assertArrayEquals(expected, bytes);
    }

    @Test
    void read() throws ZipException, IOException, URISyntaxException {
        final ZipEntry entry = zipFile.getEntry("bible.txt");

        final String expectedDefaultUTF_8 = Fs.read(bibleTxt);
        final String strDefaultUTF_8 = Zip.read(zipFile, entry);
        assertEquals(expectedDefaultUTF_8, strDefaultUTF_8);

        for (final Charset charset : ImmutableList.of(ISO_8859_1, US_ASCII, UTF_16, UTF_16BE, UTF_16LE, UTF_8)) {
            final String expected = Fs.read(bibleTxt, charset);
            final String str = Zip.read(zipFile, entry, charset);
            assertEquals(expected, str);
        }
    }

    @Test
    void attemptReadFromEmptyFolder() throws ZipException, IOException, URISyntaxException {

        final ZipEntry entry = zipFile.getEntry("empty folder/");

        assertThrows(IllegalArgumentException.class, () -> Zip.read(zipFile, entry));
        assertThrows(IllegalArgumentException.class, () -> Zip.readLines(zipFile, entry));
        assertThrows(IllegalArgumentException.class, () -> Zip.read(zipFile, entry, UTF_8));
        assertThrows(IllegalArgumentException.class, () -> Zip.readLines(zipFile, entry, UTF_8));
        assertThrows(IllegalArgumentException.class, () -> Zip.toByteArray(zipFile, entry));
    }

    @Test
    void unzip() throws IOException {
        Zip.unzip(testDataZip, temp);

        assertEquals(8, Iterables.size(MoreFiles.fileTraverser().breadthFirst(temp)));

        assertTrue(Files.isDirectory(temp.resolve("bible")));
        assertTrue(Files.isDirectory(temp.resolve("empty folder")));
        assertTrue(Files.isDirectory(temp.resolve("war and peace")));

        assertTrue(Files.isRegularFile(temp.resolve("bible.txt")));
        assertTrue(Files.isRegularFile(temp.resolve("bible\\bible.txt")));
        assertTrue(Files.isRegularFile(temp.resolve("War and Peace.txt")));
        assertTrue(Files.isRegularFile(temp.resolve("war and peace\\War and Peace.txt")));
    }

    @Test
    void unzipAbslolutePaths() throws IOException {
        assertThrows(ZipException.class, () -> Zip.unzip(absoluteDataZip, temp));
    }

    private static Path getResourceAsPath(final String name) throws URISyntaxException {
        final URI uri = Fs.class.getResource(name).toURI();
        checkArgument(uri != null, "cannot find %s", name);
        return Paths.get(uri);
    }

}
