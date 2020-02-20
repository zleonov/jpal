package software.leonov.common.collect;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.google.common.collect.Ordering;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestStringSetGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.SetFeature;

import junit.framework.TestSuite;

public class SkiplistSetTest {

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

        // suite.addTestSuite(SkiplistSetTest.class);

        // @formatter:off

        suite.addTest(
                SetTestSuiteBuilder
                .using(
                    new TestStringSetGenerator() {
                    
                        @Override
                        protected Set<String> create(final String[] elements) {
                            final Set<String> set = SkiplistSet.create(Ordering.natural().nullsLast());
                            set.addAll(Arrays.asList(elements));
                            return set;
                        }
                    })
                .named("SkiplistSet -> Ordering.natural[].nullsLast[]")
                .withFeatures(
                        CollectionSize.ANY,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionFeature.GENERAL_PURPOSE,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,                        
                        SetFeature.GENERAL_PURPOSE
                    )
                .createTestSuite());
        
        suite.addTest(
                SetTestSuiteBuilder
                .using(
                    new TestStringSetGenerator() {
                    
                        @Override
                        protected Set<String> create(final String[] elements) {
                            final Set<String> set = SkiplistSet.create(Ordering.natural().nullsFirst());
                            set.addAll(Arrays.asList(elements));
                            return set;
                        }
                    })
                .named("SkiplistSet -> Ordering.natural[].nullsFirst[]")
                .withFeatures(
                        CollectionSize.ANY,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionFeature.GENERAL_PURPOSE,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,                        
                        SetFeature.GENERAL_PURPOSE
                    )
                .createTestSuite());  

        // @formatter:on

        return suite;
    }

}
