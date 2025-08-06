package software.leonov.util.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.TreeMultimap;
import com.google.common.collect.TreeMultiset;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class TestCollectionVariations {

    private final Gson gson = GsonHelper.getGson();

    @Test
    void testHashMultisetRoundTrip() {
        final HashMultiset<String> original = HashMultiset.create();
        original.add("apple", 3);
        original.add("banana", 2);
        original.add("cherry", 1);
        
        final Type type = new TypeToken<HashMultiset<String>>(){}.getType();
        final String json = gson.toJson(original, type);
        final HashMultiset<String> deserialized = gson.fromJson(json, type);
        
        assertEquals(original, deserialized);
    }

    @Test
    void testLinkedHashMultisetRoundTrip() {
        final LinkedHashMultiset<String> original = LinkedHashMultiset.create();
        original.add("apple", 3);
        original.add("banana", 2);
        original.add("cherry", 1);
        
        final Type type = new TypeToken<LinkedHashMultiset<String>>(){}.getType();
        final String json = gson.toJson(original, type);
        final LinkedHashMultiset<String> deserialized = gson.fromJson(json, type);
        
        assertEquals(original, deserialized);
    }

    @Test
    void testTreeMultisetRoundTrip() {
        final TreeMultiset<String> original = TreeMultiset.create();
        original.add("apple", 3);
        original.add("banana", 2);
        original.add("cherry", 1);
        
        final Type type = new TypeToken<TreeMultiset<String>>(){}.getType();
        final String json = gson.toJson(original, type);
        final TreeMultiset<String> deserialized = gson.fromJson(json, type);
        
        assertEquals(original, deserialized);
    }

    @Test
    void testHashMultimapRoundTrip() {
        final HashMultimap<String, Integer> original = HashMultimap.create();
        original.put("numbers", 1);
        original.put("numbers", 2);
        original.put("letters", 65);
        original.put("letters", 66);
        
        final Type type = new TypeToken<HashMultimap<String, Integer>>(){}.getType();
        final String json = gson.toJson(original, type);
        final HashMultimap<String, Integer> deserialized = gson.fromJson(json, type);
        
        assertEquals(original, deserialized);
    }

    @Test
    void testLinkedHashMultimapRoundTrip() {
        final LinkedHashMultimap<String, Integer> original = LinkedHashMultimap.create();
        original.put("numbers", 1);
        original.put("numbers", 2);
        original.put("letters", 65);
        original.put("letters", 66);
        
        final Type type = new TypeToken<LinkedHashMultimap<String, Integer>>(){}.getType();
        final String json = gson.toJson(original, type);
        final LinkedHashMultimap<String, Integer> deserialized = gson.fromJson(json, type);
        
        assertEquals(original, deserialized);
    }

    @Test
    void testTreeMultimapRoundTrip() {
        final TreeMultimap<String, Integer> original = TreeMultimap.create();
        original.put("numbers", 1);
        original.put("numbers", 2);
        original.put("letters", 65);
        original.put("letters", 66);
        
        final Type type = new TypeToken<TreeMultimap<String, Integer>>(){}.getType();
        final String json = gson.toJson(original, type);
        final TreeMultimap<String, Integer> deserialized = gson.fromJson(json, type);
        
        assertEquals(original, deserialized);
    }

    @Test
    void testArrayListMultimapRoundTrip() {
        final ArrayListMultimap<String, Integer> original = ArrayListMultimap.create();
        original.put("numbers", 1);
        original.put("numbers", 2);
        original.put("letters", 65);
        original.put("letters", 66);
        
        final Type type = new TypeToken<ArrayListMultimap<String, Integer>>(){}.getType();
        final String json = gson.toJson(original, type);
        final ArrayListMultimap<String, Integer> deserialized = gson.fromJson(json, type);
        
        assertEquals(original, deserialized);
    }

    @Test
    void testHashBasedTableRoundTrip() {
        final HashBasedTable<String, String, Integer> original = HashBasedTable.create();
        original.put("row1", "col1", 11);
        original.put("row1", "col2", 12);
        original.put("row2", "col1", 21);
        original.put("row2", "col2", 22);
        
        final Type type = new TypeToken<HashBasedTable<String, String, Integer>>(){}.getType();
        final String json = gson.toJson(original, type);
        final HashBasedTable<String, String, Integer> deserialized = gson.fromJson(json, type);
        
        assertEquals(original, deserialized);
    }

    @Test
    void testTreeBasedTableRoundTrip() {
        final TreeBasedTable<String, String, Integer> original = TreeBasedTable.create();
        original.put("row1", "col1", 11);
        original.put("row1", "col2", 12);
        original.put("row2", "col1", 21);
        original.put("row2", "col2", 22);
        
        final Type type = new TypeToken<TreeBasedTable<String, String, Integer>>(){}.getType();
        final String json = gson.toJson(original, type);
        final TreeBasedTable<String, String, Integer> deserialized = gson.fromJson(json, type);
        
        assertEquals(original, deserialized);
    }

    @Test
    void testOptionalWithComplexValue() {
        final Optional<Multiset<String>> original = Optional.of(HashMultiset.create(Arrays.asList("a", "b", "b", "c")));
        
        final Type type = new TypeToken<Optional<Multiset<String>>>(){}.getType();
        final String json = gson.toJson(original, type);
        final Optional<Multiset<String>> deserialized = gson.fromJson(json, type);
        
        assertEquals(original, deserialized);
    }

    @Test
    void testNestedCollections() {
        final Multimap<String, Table<String, String, Integer>> original = HashMultimap.create();
        
        final HashBasedTable<String, String, Integer> table1 = HashBasedTable.create();
        table1.put("r1", "c1", 1);
        table1.put("r1", "c2", 2);
        
        final HashBasedTable<String, String, Integer> table2 = HashBasedTable.create();
        table2.put("r2", "c1", 3);
        table2.put("r2", "c2", 4);
        
        original.put("group1", table1);
        original.put("group2", table2);
        
        final Type type = new TypeToken<HashMultimap<String, HashBasedTable<String, String, Integer>>>(){}.getType();
        final String json = gson.toJson(original, type);
        final Multimap<String, Table<String, String, Integer>> deserialized = gson.fromJson(json, type);
        
        assertEquals(original, deserialized);
    }
}