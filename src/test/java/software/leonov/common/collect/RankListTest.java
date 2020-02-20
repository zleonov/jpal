package software.leonov.common.collect;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;

import junit.framework.TestSuite;

public class RankListTest {

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

    public static junit.framework.Test suite() {
        final TestSuite suite = new TestSuite();

        // suite.addTestSuite(RankListTest.class);

        // @formatter:off

        suite.addTest(
                ListTestSuiteBuilder
                .using(
                    new TestStringListGenerator() {
                    
                        @Override
                        protected List<String> create(final String[] elements) {
                            return RankList.create(Arrays.asList(elements));
                        }
                    })
                .named("RankList")
                .withFeatures(
                        CollectionSize.ANY,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionFeature.SERIALIZABLE,
                        ListFeature.GENERAL_PURPOSE
                    )
                .createTestSuite());  

        // @formatter:on

        return suite;
    }

}
