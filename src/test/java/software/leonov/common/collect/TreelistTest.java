package software.leonov.common.collect;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.google.common.collect.Ordering;
import com.google.common.collect.testing.CollectionTestSuiteBuilder;
import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestStringCollectionGenerator;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.SampleElements.Strings;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;

import junit.framework.TestSuite;

public class TreelistTest {

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
                CollectionTestSuiteBuilder
                .using(
                    new TestStringCollectionGenerator() {
                    
                        @Override
                        protected Sortedlist<String> create(final String[] elements) {
                            return Treelist.orderedBy(Ordering.natural().nullsLast()).create(Arrays.asList(elements));
                        }
                    })
                .named("Treelist -> Ordering.natural[].nullsLast[]")
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
                ListTestSuiteBuilder
                .using(
                    new TestStringListGenerator() {
                        
                        @Override
                        public SampleElements<String> samples() {
                          return new Strings();
                        }
                    
                        @Override
                        protected List<String> create(final String[] elements) {
                            return Treelist.orderedBy(Ordering.natural().nullsLast()).create(Arrays.asList(elements)).asList();
                        }
                    })
                .named("Treelist.asList[] -> Ordering.natural[].nullsLast[]")
                .withFeatures(
                        CollectionSize.ANY,
                        CollectionFeature.ALLOWS_NULL_QUERIES,
                        ListFeature.REMOVE_OPERATIONS

                        //CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        //CollectionFeature.GENERAL_PURPOSE,
                        //CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        //CollectionFeature.SERIALIZABLE
                    )
                .createTestSuite());        
        
        
//        suite.addTest(
//                QueueTestSuiteBuilder
//                .using(
//                    new TestStringQueueGenerator() {
//                    
//                        @Override
//                        protected Queue<String> create(final String[] elements) {
//                            return TreeQueue.orderedBy(Ordering.natural().nullsFirst()).create(Arrays.asList(elements));
//                        }
//                    })
//                .named("TreeQueue -> Ordering.natural[].nullsFirst[]")
//                .withFeatures(
//                        CollectionSize.ANY,
//                        CollectionFeature.ALLOWS_NULL_VALUES,
//                        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
//                        CollectionFeature.GENERAL_PURPOSE,
//                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
//                        CollectionFeature.SERIALIZABLE
//                    )
//                .createTestSuite());          

        // @formatter:on

        return suite;
    }
    
    public static class Strings extends SampleElements<String> {
        public Strings() {
          // elements aren't sorted, to better test SortedSet iteration ordering
          //super("b", "a", "c", "d", "e");
            super("a", "b", "c", "d", "e");
        }

        // for testing SortedSet and SortedMap methods
        public static final String BEFORE_FIRST = "\0";
        public static final String BEFORE_FIRST_2 = "\0\0";
        public static final String MIN_ELEMENT = "a";
        public static final String AFTER_LAST = "z";
        public static final String AFTER_LAST_2 = "zz";
      }

}
