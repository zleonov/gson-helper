package software.leonov.util.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


class TestOptional {

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

    static class Pair<K, V> {

        final K first;
        final V second;

        Pair(final K first, final V second) {
            this.first  = first;
            this.second = second;
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            final Pair<?, ?> other = (Pair<?, ?>) obj;
            return Objects.equals(first, other.first) && Objects.equals(second, other.second);
        }

    }

    @Test
    void test_Map_String_Optional_simple_type_Integer() {
        final Gson gson = GsonHelper.getGson();

        // @formatter:off
        final Map<String, Optional<Integer>> expected = ImmutableMap.of("one",   Optional.of(1),
                                                                        "two",   Optional.empty(),
                                                                        "three", Optional.of(3));
        // @formatter:on

        final String text = gson.toJson(expected);

        System.out.println(text);

        final Type type = new TypeToken<Map<String, Optional<Integer>>>() {
        }.getType();

        final Map<String, Optional<?>> actual = gson.fromJson(text, type);

        assertEquals(expected, actual);
    }

    @Test
    void test_Map_String_Optional_complex_type_Pair_String_Integer() {
        final Gson gson = GsonHelper.getGson();

        // @formatter:off
        final Map<String, Optional<Pair<String, Integer>>> expected = ImmutableMap.of("one",   Optional.of(new Pair<>("one", 1)),
                                                                                      "two",   Optional.empty(),
                                                                                      "three", Optional.of(new Pair<>("three", 3)));
        // @formatter:on        

        final String text = gson.toJson(expected);

        System.out.println(text);

        final Type type = new TypeToken<Map<String, Optional<Pair<String, Integer>>>>() {
        }.getType();

        final Map<String, Optional<?>> actual = gson.fromJson(text, type);

        assertEquals(expected, actual);
    }
}
