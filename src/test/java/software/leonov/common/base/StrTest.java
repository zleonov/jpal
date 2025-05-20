//package software.leonov.common.base;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//class StrTest {
//
//    // private final static List<File> TEMP_FILES = Lists.newLinkedList();
//
//    @BeforeAll
//    static void setUpBeforeClass() throws Exception {
//
//    }
//
//    @AfterAll
//    static void tearDownAfterClass() throws Exception {
//    }
//
//    @BeforeEach
//    void setUp() throws Exception {
//    }
//
//    @AfterEach
//    void tearDown() throws Exception {
//    }
//
//    @Test
//    void testReplaceSingleWordMiddleVsJava() {
//        final String str = "The quick brown fox";
//        final String target = "brown";
//        final String replacement = "blue";
//
//        assertEquals(str.replace(target, replacement), Str.replace(str, target, replacement));
//    }
//
//    @Test
//    void testReplaceSingleWordEndVsJava() {
//        final String str = "The quick brown fox";
//        final String target = "fox";
//        final String replacement = "cat";
//
//        assertEquals(str.replace(target, replacement), Str.replace(str, target, replacement));
//    }
//
//    @Test
//    void testReplaceSingleWordStartVsJava() {
//        final String str = "The quick brown fox";
//        final String target = "The";
//        final String replacement = "A";
//
//        assertEquals(str.replace(target, replacement), Str.replace(str, target, replacement));
//    }
//
//    @Test
//    void testReplaceMultipleWordsVsJava() {
//        final String str = "The quick brown fox jumped over the brown log";
//        final String target = "brown";
//        final String replacement = "green";
//
//        assertEquals(str.replace(target, replacement), Str.replace(str, target, replacement));
//    }
//
//    @Test
//    void testReplaceSingleWordMiddleIgnoreCase() {
//        final String str = "The quick brown fox";
//        final String target = "brown";
//        final String replacement = "blue";
//
//        assertEquals("The quick blue fox", Str.replaceIgnoreCase(str, target.toUpperCase(), replacement));
//    }
//
//    @Test
//    void testReplaceSingleWordEndIgnoreCase() {
//        final String str = "The quick brown fox";
//        final String target = "fox";
//        final String replacement = "cat";
//
//        assertEquals("The quick brown cat", Str.replaceIgnoreCase(str, target.toUpperCase(), replacement));
//    }
//
//    @Test
//    void testReplaceSingleWordStartIgnoreCase() {
//        final String str = "The quick brown fox";
//        final String target = "The";
//        final String replacement = "A";
//
//        assertEquals("A quick brown fox", Str.replaceIgnoreCase(str, target.toUpperCase(), replacement));
//    }
//
//    @Test
//    void testReplaceMultipleWordsIgnoreCase() {
//        final String str = "The quick brown fox jumped over the brown log";
//        final String target = "brown";
//        final String replacement = "green";
//
//        assertEquals("The quick green fox jumped over the green log", Str.replaceIgnoreCase(str, target.toUpperCase(), replacement));
//    }
//
//    @Test
//    void testReplaceNotFoundVsJava() {
//        final String str = "The quick brown fox jumped over the brown log";
//        final String target = "blue";
//        final String replacement = "green";
//
//        assertEquals(str.replace(target, replacement), Str.replace(str, target, replacement));
//    }
//
//    @Test
//    void testReplaceNotFoundIgnoreCaseVsJava() {
//        final String str = "The quick brown fox jumped over the brown log";
//        final String target = "blue";
//        final String replacement = "green";
//
//        assertEquals(str, Str.replaceIgnoreCase(str, target.toUpperCase(), replacement));
//    }
//
//    @Test
//    void testEscapeEOLCharacters() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog";
//        final String expected = "The quick brown fox\\njumped over\\rthe lazy\\r\\ndog";
//        assertEquals(expected, Str.escapeEOLCharacters(str));
//    }
//
//    @Test
//    void testIndentEmpty() {
//        final String str = "";
//        final String expected = "";
//        assertEquals(expected, Str.indent(str, 5));
//    }
//
//    @Test
//    void testIndentZero() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog";
//        assertEquals(str, Str.indent(str, 0));
//    }
//
//    @Test
//    void testIndent() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog";
//        final String actual = Str.indent(str, 3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentEndInNewline() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog\n";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n";
//        final String actual = Str.indent(str, 3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentEndsIn_LF_LF() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog\n\n";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n   \n";
//        final String actual = Str.indent(str, 3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentEndsIn_CR_CR() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog\r\r";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n   \n";
//        final String actual = Str.indent(str, 3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentEndsIn_CRLF() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog\r\n";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n";
//        final String actual = Str.indent(str, 3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentEndsIn_CRLF_CRLF() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog\r\n\r\n";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n   \n";
//        final String actual = Str.indent(str, 3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndent_JDK() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n";
//        final String actual = str.indent(3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentEndsIn_LF_JDK() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog\n";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n";
//        final String actual = str.indent(3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentEndsIn_CR_JDK() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog\r";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n";
//        final String actual = str.indent(3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentEndsIn_LF_LF_JDK() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog\n\n";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n   \n";
//        final String actual = str.indent(3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentEndsIn_CR_CR_JDK() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog\r\r";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n   \n";
//        final String actual = str.indent(3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentEndsIn_CRLF_JDK() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog\r\n";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n";
//        final String actual = str.indent(3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentEndsIn_CRLF_CRLF_JDK() {
//        final String str = "The quick brown fox\njumped over\rthe lazy\r\ndog\r\n\r\n";
//        final String expected = "   The quick brown fox\n   jumped over\n   the lazy\n   dog\n   \n";
//        final String actual = str.indent(3);
//        System.out.println(Str.showWhitespace(str));
//        System.out.println(Str.showWhitespace(actual));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testIndentZeroJava() {
//        final String str = "The quick brown fox\njumped over\nthe lazy\ndog\n";
//        assertEquals(str, str.indent(0));
//    }
//
//    @Test
//    void testIndentEmptyJava() {
//        final String str = "";
//        assertEquals("", str.indent(1));
//    }
//
//    @Test
//    void testIndentWhitespaceJava() {
//        final String str = " ";
//        assertEquals("  \n", str.indent(1));
//    }
//
//    @Test
//    void testIndentWhitespace() {
//        final String str = " ";
//        assertEquals("    ", Str.indent(str, 3));
//    }
//
//    @Test
//    void testVisualizeWhitespace() {
//        final String str = "\tThe quick brown\t fox\njumped over\r   the lazy\r\ndog\r\n\r\n";
//        final String expected = "»   The·quick·brown»   ·fox↓\n" + "jumped·over←\n" + "···the·lazy←↓\n" + "dog←↓\n" + "←↓\n";
//        final String actual = Str.showWhitespace(str);
//        System.out.println(expected);
//        System.out.println(actual);
//        assertEquals(actual, expected);
//    }
//
//}
