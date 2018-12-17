package net.javatoday.common.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

class MoreStringsTest {

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
    }

    @Test
    void testReplaceSingleWordMiddleVsJava() {
        final String str = "The quick brown fox";
        final String target = "brown";
        final String replacement = "blue";

        assertEquals(str.replace(target, replacement), MoreStrings.replace(str, target, replacement));
    }

    @Test
    void testReplaceSingleWordEndVsJava() {
        final String str = "The quick brown fox";
        final String target = "fox";
        final String replacement = "cat";

        assertEquals(str.replace(target, replacement), MoreStrings.replace(str, target, replacement));
    }

    @Test
    void testReplaceSingleWordStartVsJava() {
        final String str = "The quick brown fox";
        final String target = "The";
        final String replacement = "A";

        assertEquals(str.replace(target, replacement), MoreStrings.replace(str, target, replacement));
    }

    @Test
    void testReplaceMultipleWordsVsJava() {
        final String str = "The quick brown fox jumped over the brown log";
        final String target = "brown";
        final String replacement = "green";

        assertEquals(str.replace(target, replacement), MoreStrings.replace(str, target, replacement));
    }

    @Test
    void testReplaceSingleWordMiddleIgnoreCase() {
        final String str = "The quick brown fox";
        final String target = "brown";
        final String replacement = "blue";

        assertEquals("The quick blue fox", MoreStrings.replaceIgnoreCase(str, target.toUpperCase(), replacement));
    }

    @Test
    void testReplaceSingleWordEndIgnoreCase() {
        final String str = "The quick brown fox";
        final String target = "fox";
        final String replacement = "cat";

        assertEquals("The quick brown cat", MoreStrings.replaceIgnoreCase(str, target.toUpperCase(), replacement));
    }

    @Test
    void testReplaceSingleWordStartIgnoreCase() {
        final String str = "The quick brown fox";
        final String target = "The";
        final String replacement = "A";

        assertEquals("A quick brown fox", MoreStrings.replaceIgnoreCase(str, target.toUpperCase(), replacement));
    }

    @Test
    void testReplaceMultipleWordsIgnoreCase() {
        final String str = "The quick brown fox jumped over the brown log";
        final String target = "brown";
        final String replacement = "green";

        assertEquals("The quick green fox jumped over the green log", MoreStrings.replaceIgnoreCase(str, target.toUpperCase(), replacement));
    }

    @Test
    void testReplaceNotFoundVsJava() {
        final String str = "The quick brown fox jumped over the brown log";
        final String target = "blue";
        final String replacement = "green";

        assertEquals(str.replace(target, replacement), MoreStrings.replace(str, target, replacement));
    }

    @Test
    void testReplaceNotFoundIgnoreCaseVsJava() {
        final String str = "The quick brown fox jumped over the brown log";
        final String target = "blue";
        final String replacement = "green";

        assertEquals(str, MoreStrings.replaceIgnoreCase(str, target.toUpperCase(), replacement));
    }
    
    @Test
    void testEscapeEOLCharacters() {
        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog";
        final String expected = "The quick brown fox\\njumped over\\rthe lazy\\r\\ndog";
        assertEquals(MoreStrings.escapeEOLCharacters(str), expected);
    }

}
