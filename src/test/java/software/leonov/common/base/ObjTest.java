package software.leonov.common.base;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ObjTest {

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
    void test_coalesce_second_null() {
        final String first  = "first";
        final String second = null;
        assertThat(Obj.coalesce(first, second), is("first"));
    }

    @Test
    void test_coalesce_null_null() {
        final String first  = null;
        final String second = null;
        assertNull(Obj.coalesce(first, second));
    }

    @Test
    void test_coalesce_first_null() {
        final String first  = null;
        final String second = "second";
        assertThat(Obj.coalesce(first, second), is(equalTo("second")));
    }


}
