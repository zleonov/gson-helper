package software.leonov.util.gson;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    TestMultimap.class,
    TestMultiset.class,
    TestOptional.class,
    TestTable.class,
    TestGsonHelper.class,
    TestErrorHandling.class,
    TestCollectionVariations.class
})
class AllTests {
}