package net.javatoday.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PairTest {

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
    void testSerializeDeserialize() throws IOException, URISyntaxException, ClassNotFoundException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);

        final ImmutablePair<String, String> p1 = ImmutablePair.of("foo", "bar");

        out.writeObject(p1);

        final byte[] bytes = baos.toByteArray();

        final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        @SuppressWarnings("unchecked")
        final ImmutablePair<String, Integer> p2 = (ImmutablePair<String, Integer>) ois.readObject();

        assertEquals(p1, p2);
    }

}
