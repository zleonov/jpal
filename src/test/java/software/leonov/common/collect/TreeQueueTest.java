package software.leonov.common.collect;

import java.util.Arrays;
import java.util.Queue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.google.common.collect.Ordering;
import com.google.common.collect.testing.QueueTestSuiteBuilder;
import com.google.common.collect.testing.TestStringQueueGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;

import junit.framework.TestSuite;

public class TreeQueueTest {

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

        // suite.addTestSuite(TreeQueueTest.class);

        // @formatter:off

        suite.addTest(
                QueueTestSuiteBuilder
                .using(
                    new TestStringQueueGenerator() {
                    
                        @Override
                        protected Queue<String> create(final String[] elements) {
                            return TreeQueue.orderedBy(Ordering.natural().nullsLast()).create(Arrays.asList(elements));
                        }
                    })
                .named("TreeQueue -> Ordering.natural[].nullsLast[]")
                .withFeatures(
                        CollectionSize.ANY,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionFeature.GENERAL_PURPOSE,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE
                    )
                .createTestSuite());

        suite.addTest(
                QueueTestSuiteBuilder
                .using(
                    new TestStringQueueGenerator() {
                    
                        @Override
                        protected Queue<String> create(final String[] elements) {
                            return TreeQueue.orderedBy(Ordering.natural().nullsFirst()).create(Arrays.asList(elements));
                        }
                    })
                .named("TreeQueue -> Ordering.natural[].nullsFirst[]")
                .withFeatures(
                        CollectionSize.ANY,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionFeature.GENERAL_PURPOSE,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE
                    )
                .createTestSuite());          

        // @formatter:on

        return suite;
    }

}
