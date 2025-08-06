package software.leonov.util.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


class TestTable {

    // @formatter:off
    private static final GsonBuilder BUILDER = Converters.registerAll(new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping())
            .registerTypeAdapterFactory(new OptionalTypeAdapterFactory())
            .registerTypeAdapterFactory(new MultisetTypeAdapterFactory())
            .registerTypeAdapterFactory(new MultimapTypeAdapterFactory())
            .registerTypeAdapterFactory(new TableTypeAdapterFactory());
    // @formatter:on;

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
    void test_multimap_complex_keys_map_string_integer_simple_values_integer() {
        final Gson gson = BUILDER.enableComplexMapKeySerialization().create();

        final Type type = new TypeToken<Table<Map<String, Integer>, Map<Integer, String>, String>>() {
        }.getType();

        final Table<Map<String, Integer>, Map<Integer, String>, String> map = ImmutableTable.of(ImmutableMap.of("one", 1), ImmutableMap.of(1, "one"), "one");

        final String json = gson.toJson(map, type);

        System.out.println(json);

        final Table<Map<String, Integer>, Map<Integer, String>, String> map2 = gson.fromJson(json, type);

        assertEquals(map, map2);
    }

    @Test
    void test_multimap_complex_keys_list_string_complex_values_map_string_integer() {
        final Gson gson = BUILDER.enableComplexMapKeySerialization().create();

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
