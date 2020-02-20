package software.leonov.common.collect;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestStringSetGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.SetFeature;

import junit.framework.TestSuite;

public class TreeBasedSetTest {

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

        // suite.addTestSuite(TreeBasedSetTest.class);

        // @formatter:off

        suite.addTest(
                SetTestSuiteBuilder
                .using(
                    new TestStringSetGenerator() {
                    
                        @Override
                        protected Set<String> create(final String[] elements) {
                            final Set<String> set = TreeBasedSet.create(Comparator.nullsFirst(Comparator.naturalOrder()));
                            set.addAll(Arrays.asList(elements));
                            return set;
                        }
                    })
                .named("TreeBasedSet -> Comparator.nullsFirst[Comparator.naturalOrder[]]")
                .withFeatures(
                        CollectionSize.ANY,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        // CollectionFeature.KNOWN_ORDER,
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
                            final Set<String> set = TreeBasedSet.create(Comparator.nullsLast(Comparator.naturalOrder()));
                            set.addAll(Arrays.asList(elements));
                            return set;
                        }
                    })
                .named("TreeBasedSet -> Comparator.nullsLast[Comparator.naturalOrder[]]")
                .withFeatures(
                        CollectionSize.ANY,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        // CollectionFeature.KNOWN_ORDER,
                        CollectionFeature.SERIALIZABLE,                        
                        SetFeature.GENERAL_PURPOSE
                    )
                .createTestSuite());  
        
        // @formatter:on

        return suite;
    }

}
