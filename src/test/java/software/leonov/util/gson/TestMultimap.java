package software.leonov.util.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


class TestMultimap {

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
    void test_multimap_simple_keys_instant_integer() {
        final Gson gson = GsonHelper.getGson();

        final Type type = new TypeToken<Multimap<Instant, Integer>>() {
        }.getType();

        final Instant now       = Instant.now();
        final Instant yesterday = now.minus(Duration.ofDays(1));

        final Multimap<Instant, Integer> map = ImmutableMultimap.of(now, 1, now, 2, now, 3, yesterday, 4, yesterday, 5);

        final String json = GsonHelper.getGson().toJson(map, type);

        final Multimap<Instant, Integer> map2 = gson.fromJson(json, type);

        assertEquals(map, map2);
    }

    @Test
    void test_multimap_simple_keys_string_integer() {
        final Gson gson = GsonHelper.getGson();

        final Type type = new TypeToken<Multimap<String, Integer>>() {
        }.getType();

        final Multimap<String, Integer> map = ImmutableMultimap.of("one", 1, "one", 2, "one", 3, "two", 4, "three", 5);

        final String json = GsonHelper.getGson().toJson(map, type);

        final Multimap<String, Integer> map2 = gson.fromJson(json, type);

        assertEquals(map, map2);
    }

    @Test
    void test_multimap_complex_keys_map_string_integer_simple_values_integer() {
        final Gson gson = GsonHelper.getGson();

        final Type type = new TypeToken<Multimap<Map<String, Integer>, Integer>>() {
        }.getType();

        final Multimap<Map<String, Integer>, Integer> map = ImmutableMultimap.of(ImmutableMap.of("one", 1), 1, ImmutableMap.of("one", 1), 1);

        final String json = GsonHelper.getGson().toJson(map, type);

        final Multimap<Map<String, Integer>, Integer> map2 = gson.fromJson(json, type);

        assertEquals(map, map2);
    }

    @Test
    void test_multimap_complex_keys_list_string_complex_values_map_string_integer() {
        final Gson gson = GsonHelper.getGson();

        final Type type = new TypeToken<Multimap<List<String>, Map<String, Integer>>>() {
        }.getType();

        // @formatter:off
        final Multimap<List<String>, Map<String, Integer>> map = ImmutableMultimap.of(ImmutableList.of("one", "two"),   ImmutableMap.of("three",  4),
                                                                                      ImmutableList.of("one", "two"),   ImmutableMap.of("five",   6),
                                                                                      ImmutableList.of("one", "two"),   ImmutableMap.of("seven",  8),
                                                                                      ImmutableList.of("two", "three"), ImmutableMap.of("nine",   10),
                                                                                      ImmutableList.of("two", "three"), ImmutableMap.of("eleven", 12));
        // @formatter:on

        final String json = GsonHelper.getGson().toJson(map, type);

        final Multimap<ImmutableMap<String, Integer>, Integer> map2 = gson.fromJson(json, type);

        assertEquals(map, map2);
    }

}
