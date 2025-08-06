package software.leonov.util.gson;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.HashBasedTable;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

class TestErrorHandling {

    @Test
    void testOptionalAdapterRejectsNullOptional() {
        final Gson gson = GsonHelper.getGson();
        
        assertThrows(NullPointerException.class, () -> {
            gson.toJson(null, Optional.class);
        });
    }

    @Test
    void testInvalidJsonParsing() {
        assertThrows(JsonSyntaxException.class, () -> {
            GsonHelper.parseJson("{invalid json}");
        });
    }

    @Test
    void testEmptyArrayAccess() {
        final JsonArray emptyArray = new JsonArray();
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            GsonHelper.getFirst(emptyArray);
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            GsonHelper.getLast(emptyArray);
        });
    }

    @Test
    void testNullParameterChecks() {
        assertThrows(NullPointerException.class, () -> {
            GsonHelper.parseJson((String) null);
        });
        
        assertThrows(NullPointerException.class, () -> {
            GsonHelper.parseJson((StringReader) null);
        });
        
        assertThrows(NullPointerException.class, () -> {
            GsonHelper.prettify((String) null);
        });
        
        assertThrows(NullPointerException.class, () -> {
            GsonHelper.getFirst(null);
        });
        
        assertThrows(NullPointerException.class, () -> {
            GsonHelper.getLast(null);
        });
    }

    @Test
    void testEmptyCollections() {
        final Gson gson = GsonHelper.getGson();
        
        // Test empty multiset
        final HashMultiset<String> emptyMultiset = HashMultiset.create();
        final String multisetJson = gson.toJson(emptyMultiset);
        final HashMultiset<String> deserializedMultiset = gson.fromJson(multisetJson, 
            new TypeToken<HashMultiset<String>>(){}.getType());
        assertNotNull(deserializedMultiset);
        
        // Test empty multimap
        final HashMultimap<String, String> emptyMultimap = HashMultimap.create();
        final String multimapJson = gson.toJson(emptyMultimap);
        final HashMultimap<String, String> deserializedMultimap = gson.fromJson(multimapJson, 
            new TypeToken<HashMultimap<String, String>>(){}.getType());
        assertNotNull(deserializedMultimap);
        
        // Test empty table
        final HashBasedTable<String, String, String> emptyTable = HashBasedTable.create();
        final String tableJson = gson.toJson(emptyTable);
        final HashBasedTable<String, String, String> deserializedTable = gson.fromJson(tableJson, 
            new TypeToken<HashBasedTable<String, String, String>>(){}.getType());
        assertNotNull(deserializedTable);
        
        // Test empty optional
        final Optional<String> emptyOptional = Optional.empty();
        final String optionalJson = gson.toJson(emptyOptional);
        final Optional<String> deserializedOptional = gson.fromJson(optionalJson, 
            new TypeToken<Optional<String>>(){}.getType());
        assertNotNull(deserializedOptional);
    }

    @Test
    void testNullValuesInCollections() {
        final Gson gson = GsonHelper.getGson();
        
        // Test multiset with null handling
        final HashMultiset<String> multiset = HashMultiset.create();
        multiset.add("test");
        final String json = gson.toJson(multiset);
        
        final HashMultiset<String> deserialized = gson.fromJson(json, 
            new TypeToken<HashMultiset<String>>(){}.getType());
        assertNotNull(deserialized);
    }

    @Test
    void testRegisterAllWithNullBuilder() {
        assertThrows(NullPointerException.class, () -> {
            GsonHelper.registerAll(null);
        });
    }
}