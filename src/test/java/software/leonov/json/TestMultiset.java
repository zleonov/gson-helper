package software.leonov.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.reflect.TypeToken;

import software.leonov.util.gson.GsonHelper;

class TestMultiset {

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp(final TestInfo test) throws Exception {
        System.out.println("Executing " + test.getTestMethod().get().getName() + ":");
    }

    @AfterEach
    void tearDown() throws Exception {
        System.out.println();
    }

    @Test
    void test_empty_Multiset() {
        final Multiset<Integer> expected = ImmutableMultiset.of();

        final Type type = new TypeToken<Multiset<Integer>>() {
        }.getType();

        final String text = GsonHelper.getGson().toJson(expected);

        System.out.println(text);

        final Multiset<Integer> actual = GsonHelper.getGson().<Multiset<Integer>>fromJson(text, type);

        assertEquals(expected, actual);
    }

    @Test
    void test_Multiset_of_simple_type_Integer() {
        final Type type = new TypeToken<Multiset<Integer>>() {
        }.getType();

        final Multiset<Integer> expected = ImmutableMultiset.of(1, 1, 1, 2, 2, 3, 3, 3, 3, 4, 5, 5);

        final String text = GsonHelper.getGson().toJson(expected, type);

        System.out.println(text);

        final Multiset<ImmutableList<String>> actual = GsonHelper.getGson().fromJson(text, type);

        assertEquals(expected, actual);
    }

    @Test
    void test_Multiset_of_complex_type_List_String() {

        final Type type = new TypeToken<Multiset<List<String>>>() {
        }.getType();

        @SuppressWarnings("unchecked")
        final Multiset<ImmutableList<String>> expected = ImmutableMultiset.of(ImmutableList.of("one", "two"), ImmutableList.of("one", "two"), ImmutableList.of("2"), ImmutableList.of("3", "4", "5"), ImmutableList.of("3", "4", "5"),
                ImmutableList.of("3", "4", "5"));

        final String text = GsonHelper.getGson().toJson(expected, type);

        System.out.println(text);

        final Multiset<ImmutableList<String>> actual = GsonHelper.getGson().fromJson(text, type);

        assertEquals(expected, actual);
    }

    @Test
    void test_Multiset_wrong_type() {
        final Type type = new TypeToken<ImmutableMultiset<Integer>>() {
        }.getType();

        final Multiset<Integer> expected = ImmutableMultiset.of(1, 1, 1, 2, 2, 3, 3, 3, 3, 4, 5, 5);

        final String text = GsonHelper.getGson().toJson(expected, type);

        System.out.println(text);

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            GsonHelper.getGson().fromJson(text, type);
        });

        assertEquals("ImmutableMultiset is not supported; try one of [HashMultiset, LinkedHashMultiset, TreeMultiset, ConcurrentHashMultiset]", exception.getMessage());
    }

}
