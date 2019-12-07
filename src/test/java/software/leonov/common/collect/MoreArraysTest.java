package software.leonov.common.collect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

class MoreArraysTest {

    private static String[] array = new String[] { "The", "quick", "brown", "fox", "jumped", null, "over", "the", null, "lazy", "dog", "1", "2", "the", "3" };

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
    void testContains() {
        final String obj = "the";
        assertTrue(MoreArrays.contains(array, obj));
        assertTrue(MoreArrays.contains(array, Predicates.equalTo(obj)));
        assertTrue(Arrays.asList(array).contains(obj)); // Java
        assertTrue(Arrays.stream(array).anyMatch(Predicates.equalTo(obj))); // Java 8+
        assertTrue(Iterators.contains(Iterators.forArray(array), obj)); // Guava
        assertTrue(Iterators.any(Iterators.forArray(array), Predicates.equalTo(obj))); // Guava
    }

    @Test
    void testContainsFalse() {
        final String obj = "cat";
        assertFalse(MoreArrays.contains(array, obj));
        assertFalse(MoreArrays.contains(array, Predicates.equalTo(obj)));
        assertFalse(Arrays.asList(array).contains(obj)); // Java
        assertFalse(Arrays.stream(array).anyMatch(Predicates.equalTo(obj))); // Java 8+
        assertFalse(Iterators.contains(Iterators.forArray(array), obj)); // Guava
        assertFalse(Iterators.any(Iterators.forArray(array), Predicates.equalTo(obj))); // Guava
    }

    @Test
    void testContainsFirstElement() {
        final String obj = "The";
        assertTrue(MoreArrays.contains(array, obj));
        assertTrue(MoreArrays.contains(array, Predicates.equalTo(obj)));
        assertTrue(Arrays.asList(array).contains(obj)); // Java
        assertTrue(Arrays.stream(array).anyMatch(Predicates.equalTo(obj))); // Java 8+
        assertTrue(Iterators.contains(Iterators.forArray(array), obj)); // Guava
        assertTrue(Iterators.any(Iterators.forArray(array), Predicates.equalTo(obj))); // Guava
    }

    @Test
    void testContainsLastElement() {
        final String obj = "3";
        assertTrue(MoreArrays.contains(array, obj));
        assertTrue(MoreArrays.contains(array, Predicates.equalTo(obj)));
        assertTrue(Arrays.asList(array).contains(obj)); // Java
        assertTrue(Arrays.stream(array).anyMatch(Predicates.equalTo(obj))); // Java 8+
        assertTrue(Iterators.contains(Iterators.forArray(array), obj)); // Guava
        assertTrue(Iterators.any(Iterators.forArray(array), Predicates.equalTo(obj))); // Guava
    }

    @Test
    void testContainsNull() {
        final String obj = null;
        assertTrue(MoreArrays.contains(array, obj));
        assertTrue(MoreArrays.contains(array, Predicates.equalTo(obj)));
        assertTrue(Arrays.asList(array).contains(obj)); // Java
        assertTrue(Arrays.stream(array).anyMatch(Predicates.equalTo(obj))); // Java 8+
        assertTrue(Iterators.contains(Iterators.forArray(array), obj)); // Guava
        assertTrue(Iterators.any(Iterators.forArray(array), Predicates.equalTo(obj))); // Guava
    }

    @Test
    void testContainsNullArrayWithObject() {
        try {
            MoreArrays.contains(null, "fox");
        } catch (final Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
            assertEquals("array == null", e.getMessage());
            return;
        }
        fail("expected: <" + NullPointerException.class.getName() + ">");
    }

    @Test
    void testContainsNullArrayWithPredicate() {
        try {
            MoreArrays.contains(null, Predicates.equalTo("fox"));
        } catch (final Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
            assertEquals("array == null", e.getMessage());
            return;
        }
        fail("expected: <" + NullPointerException.class.getName() + ">");
    }

    @Test
    void testIndexOf() {
        final String obj = "the";
        assertEquals(Arrays.asList(array).indexOf(obj), MoreArrays.indexOf(array, obj)); // Java
        assertEquals(7, MoreArrays.indexOf(array, obj));
        assertEquals(7, Iterators.indexOf(Iterators.forArray(array), Predicates.equalTo(obj))); // Guava
    }

    @Test
    void testIndexOfFirstElement() {
        final String obj = "The";
        assertEquals(Arrays.asList(array).indexOf(obj), MoreArrays.indexOf(array, obj)); // Java
        assertEquals(0, MoreArrays.indexOf(array, obj));
        assertEquals(0, Iterators.indexOf(Iterators.forArray(array), Predicates.equalTo(obj))); // Guava
    }

    @Test
    void testIndexOfFromIndex() {
        final String obj = "the";
        assertEquals(13, MoreArrays.indexOf(array, obj, 8));
        assertEquals(13, MoreArrays.indexOf(array, Predicates.equalTo(obj), 8));
    }

    @Test
    void testIndexOfFromIndex0() {
        final String obj = "the";
        assertEquals(7, MoreArrays.indexOf(array, obj, 0));
        assertEquals(7, MoreArrays.indexOf(array, Predicates.equalTo(obj), 0));
    }

    @Test
    void testIndexOfFromIndexNotFound() {
        final String obj = "brown";
        assertEquals(-1, MoreArrays.indexOf(array, obj, 10));
        assertEquals(-1, MoreArrays.indexOf(array, Predicates.equalTo(obj), 10));
    }

    @Test
    void testIndexOfLastElement() {
        final String obj = "3";
        assertEquals(Arrays.asList(array).indexOf(obj), MoreArrays.indexOf(array, obj)); // Java
        assertEquals(14, MoreArrays.indexOf(array, obj));
        assertEquals(14, Iterators.indexOf(Iterators.forArray(array), Predicates.equalTo(obj))); // Guava
    }

    @Test
    void testIndexOfNegativeIndex() {
        try {
            MoreArrays.indexOf(array, "fox", -1);
        } catch (final Exception e) {
            assertEquals(IndexOutOfBoundsException.class, e.getClass());
            return;
        }
        fail("expected: <" + IndexOutOfBoundsException.class.getName() + ">");
    }

    @Test
    void testIndexOfNotFound() {
        final String obj = "jack";
        assertEquals(Arrays.asList(array).indexOf(obj), MoreArrays.indexOf(array, obj)); // Java
        assertEquals(-1, MoreArrays.indexOf(array, obj));
        assertEquals(-1, Iterators.indexOf(Iterators.forArray(array), Predicates.equalTo(obj))); // Guava
    }

    @Test
    void testIndexOfNullArrayWithObject() {
        try {
            MoreArrays.indexOf(null, "fox");
        } catch (final Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
            assertEquals("array == null", e.getMessage());
            return;
        }
        fail("expected: <" + NullPointerException.class.getName() + ">");
    }

    @Test
    void testIndexOfNullArrayWithPredicate() {
        try {
            MoreArrays.indexOf(null, Predicates.equalTo("fox"));
        } catch (final Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
            assertEquals("array == null", e.getMessage());
            return;
        }
        fail("expected: <" + NullPointerException.class.getName() + ">");
    }

    @Test
    void testIndexOfOutOfRangeIndex() {
        try {
            MoreArrays.indexOf(array, "fox", array.length);
        } catch (final Exception e) {
            assertEquals(IndexOutOfBoundsException.class, e.getClass());
            return;
        }
        fail("expected: <" + IndexOutOfBoundsException.class.getName() + ">");
    }
    
    @Test
    void testLastIndexOf() {
        final String obj = "the";
        assertEquals(Arrays.asList(array).lastIndexOf(obj), MoreArrays.lastIndexOf(array, obj)); // Java
        assertEquals(13, MoreArrays.lastIndexOf(array, obj));
        assertEquals(13, MoreArrays.lastIndexOf(array, Predicates.equalTo(obj)));
    }
    
    @Test
    void testLastIndexOfFromIndex() {
        final String obj = "the";
        assertEquals(7, MoreArrays.lastIndexOf(array, obj, 12));
        assertEquals(7, MoreArrays.lastIndexOf(array, Predicates.equalTo(obj), 12));
    }
    
    @Test
    void testLastIndexOfFromIndexArraySizeMinusOne() {
        final String obj = "3";
        final int index = array.length - 1;
        assertEquals(index, MoreArrays.lastIndexOf(array, obj, index));
        assertEquals(index, MoreArrays.lastIndexOf(array, Predicates.equalTo(obj), index));
    }
    
    @Test
    void testLastIndexOfFromIndexNotFound() {
        final String obj = "the";
        assertEquals(-1, MoreArrays.lastIndexOf(array, obj, 5));
        assertEquals(-1, MoreArrays.lastIndexOf(array, Predicates.equalTo(obj), 5));
    }

    @Test
    void testLastIndexOfNegativeIndex() {
        try {
            System.out.println(MoreArrays.lastIndexOf(array, "fox", -1));
        } catch (final Exception e) {
            assertEquals(IndexOutOfBoundsException.class, e.getClass());
            return;
        }
        fail("expected: <" + IndexOutOfBoundsException.class.getName() + ">");
    }

    @Test
    void testLastIndexOfNullArrayWithObject() {
        try {
            MoreArrays.lastIndexOf(null, "fox");
        } catch (final Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
            assertEquals("array == null", e.getMessage());
            return;
        }
        fail("expected: <" + NullPointerException.class.getName() + ">");
    }
    
    @Test
    void testLastIndexOfNullArrayWithPredicate() {
        try {
            MoreArrays.lastIndexOf(null, Predicates.equalTo("fox"));
        } catch (final Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
            assertEquals("array == null", e.getMessage());
            return;
        }
        fail("expected: <" + NullPointerException.class.getName() + ">");
    }

    @Test
    void testLastIndexOfOutOfRangeIndex() {
        try {
            MoreArrays.lastIndexOf(array, "fox", array.length);
        } catch (final Exception e) {
            assertEquals(IndexOutOfBoundsException.class, e.getClass());
            return;
        }
        fail("expected: <" + IndexOutOfBoundsException.class.getName() + ">");
    }

}
