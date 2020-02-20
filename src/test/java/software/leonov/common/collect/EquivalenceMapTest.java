package software.leonov.common.collect;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.google.common.base.Equivalence;
import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;

import junit.framework.TestSuite;

public class EquivalenceMapTest {

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

        // suite.addTestSuite(EquivalenceMapTest.class);

        // @formatter:off

        suite.addTest(
            MapTestSuiteBuilder
                .using(
                    new TestStringMapGenerator() {
                    
                        @Override
                        protected Map<String, String> create(final Entry<String, String>[] entries) {
                            final Map<String, String> map = new EquivalenceMap<>(Equivalence.equals());
                            
                            for (final Entry<String, String> entry : entries)
                                map.put(entry.getKey(), entry.getValue());
                            
                            return map;
                        }
                    })
                .named("EquivalenceMap -> Equivalence.equals")
                .withFeatures(
                        CollectionSize.ANY,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.KNOWN_ORDER,
                        CollectionFeature.SERIALIZABLE
                    )
                .createTestSuite());  

        // @formatter:on

        return suite;
    }

}
