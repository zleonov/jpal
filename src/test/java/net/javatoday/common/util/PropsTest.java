package net.javatoday.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import net.javatoday.common.io.Fs;

class PropsTest {

    private final static Properties EXPECTED = new Properties();

    private static File utf8_properties;
    private static File latin1_properties;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        EXPECTED.setProperty("foo", "bar");
        EXPECTED.setProperty("hasUnicodeEscapes", "\u019C\u01C2");

        utf8_properties = getResourceAsFile("utf8.properties");
        latin1_properties = getResourceAsFile("latin1.properties");

    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void testLoadLatin1Latin1() throws IOException, URISyntaxException {
        assertEquals(EXPECTED, Props.load(latin1_properties));
        assertEquals(EXPECTED, Props.load(null, latin1_properties));
        assertEquals(EXPECTED, Props.load(null, latin1_properties, Charsets.ISO_8859_1));
    }

    @Test
    void testLoadLatin1Utf8() throws IOException, URISyntaxException {
        assertEquals(EXPECTED, Props.load(latin1_properties));
        assertEquals(EXPECTED, Props.load(null, latin1_properties, Charsets.UTF_8));
    }

    @Test
    void testLoadUtf8Latin1() throws IOException, URISyntaxException {
        assertNotEquals(EXPECTED, Props.load(utf8_properties));
        assertNotEquals(EXPECTED, Props.load(null, utf8_properties, Charsets.ISO_8859_1));
    }

    @Test
    void testLoadUtf8Utf8() throws IOException, URISyntaxException {
        assertEquals(EXPECTED, Props.load(null, utf8_properties, Charsets.UTF_8));
    }

    @Test
    void testSaveUtf8Utf8() throws IOException, URISyntaxException {
        final File temp = File.createTempFile("tempUtf8", "properties");
        temp.deleteOnExit();

        Props.save(EXPECTED, null, temp, Charsets.UTF_8);

        final List<String> actual = Fs.readLines(temp);
        final List<String> expected = Fs.readLines(utf8_properties);

        assertTrue(Iterables.elementsEqual(expected.subList(1, expected.size() - 1), actual.subList(1, actual.size() - 1)));

        assertEquals(EXPECTED, Props.load(null, temp, Charsets.UTF_8));

        temp.delete();
    }

    @Test
    void testSaveLatin1Latin1() throws IOException, URISyntaxException {
        final File temp = File.createTempFile("tempLatin1", "properties");
        temp.deleteOnExit();

        Props.save(EXPECTED, null, temp, Charsets.ISO_8859_1);

        final List<String> actual = Fs.readLines(temp);
        final List<String> expected = Fs.readLines(latin1_properties);

        assertTrue(Iterables.elementsEqual(expected.subList(1, expected.size() - 1), actual.subList(1, actual.size() - 1)));

        assertEquals(EXPECTED, Props.load(null, temp, Charsets.ISO_8859_1));

        temp.delete();
    }

    private static File getResourceAsFile(final String name) throws URISyntaxException {
        final URI uri = PropsTest.class.getResource(name).toURI();
        Preconditions.checkArgument(uri != null, "cannot find %s", name);
        return new File(uri);
    }

}
