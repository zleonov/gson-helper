package software.leonov.util.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

class TestGsonHelper {

    @Test
    void testNextBooleanWithNull() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("null"));
        final Boolean    result = GsonHelper.nextBoolean(reader);
        assertNull(result);
    }

    @Test
    void testNextBooleanWithDefault() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("null"));
        final Boolean    result = GsonHelper.nextBoolean(reader, true);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testNextBooleanWithValue() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("true"));
        final Boolean    result = GsonHelper.nextBoolean(reader);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testNextDoubleWithNull() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("null"));
        final Double     result = GsonHelper.nextDouble(reader);
        assertNull(result);
    }

    @Test
    void testNextDoubleWithDefault() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("null"));
        final Double     result = GsonHelper.nextDouble(reader, 42.5);
        assertEquals(42.5, result);
    }

    @Test
    void testNextDoubleWithValue() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("3.14"));
        final Double     result = GsonHelper.nextDouble(reader);
        assertEquals(3.14, result);
    }

    @Test
    void testNextIntegerWithNull() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("null"));
        final Integer    result = GsonHelper.nextInteger(reader);
        assertNull(result);
    }

    @Test
    void testNextIntegerWithDefault() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("null"));
        final Integer    result = GsonHelper.nextInteger(reader, 42);
        assertEquals(Integer.valueOf(42), result);
    }

    @Test
    void testNextIntegerWithValue() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("123"));
        final Integer    result = GsonHelper.nextInteger(reader);
        assertEquals(Integer.valueOf(123), result);
    }

    @Test
    void testNextLongWithNull() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("null"));
        final Long       result = GsonHelper.nextLong(reader);
        assertNull(result);
    }

    @Test
    void testNextLongWithDefault() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("null"));
        final Long       result = GsonHelper.nextLong(reader, 42L);
        assertEquals(Long.valueOf(42L), result);
    }

    @Test
    void testNextLongWithValue() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("123456789"));
        final Long       result = GsonHelper.nextLong(reader);
        assertEquals(Long.valueOf(123456789L), result);
    }

    @Test
    void testNextStringWithNull() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("null"));
        final String     result = GsonHelper.nextString(reader);
        assertNull(result);
    }

    @Test
    void testNextStringWithDefault() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("null"));
        final String     result = GsonHelper.nextString(reader, "default");
        assertEquals("default", result);
    }

    @Test
    void testNextStringWithValue() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("\"hello\""));
        final String     result = GsonHelper.nextString(reader);
        assertEquals("hello", result);
    }

    @Test
    void testNextNull() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("null"));
        final Void       result = GsonHelper.nextNull(reader);
        assertNull(result);
    }

    @Test
    void testSkipValue() throws IOException {
        final JsonReader reader = new JsonReader(new StringReader("\"skip this\""));
        final String     result = GsonHelper.skipValue(reader, "default");
        assertEquals("default", result);
    }

    @Test
    void testNextElement() throws IOException {
        final JsonReader  reader = new JsonReader(new StringReader("{\"key\":\"value\"}"));
        final JsonElement result = GsonHelper.nextElement(reader);
        assertEquals("value", result.getAsJsonObject().get("key").getAsString());
    }

    @Test
    void testParseJsonString() {
        final String      json   = "{\"key\":\"value\"}";
        final JsonElement result = GsonHelper.parseJson(json);
        assertEquals("value", result.getAsJsonObject().get("key").getAsString());
    }

    @Test
    void testParseJsonReader() {
        final String      json   = "{\"key\":\"value\"}";
        final JsonElement result = GsonHelper.parseJson(new StringReader(json));
        assertEquals("value", result.getAsJsonObject().get("key").getAsString());
    }

    @Test
    void testParseJsonInputStream() throws IOException {
        final String               json        = "{\"key\":\"value\"}";
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        final JsonElement          result      = GsonHelper.parseJson(inputStream);
        assertEquals("value", result.getAsJsonObject().get("key").getAsString());
    }

    @Test
    void testParseJsonInputStreamWithCharset() throws IOException {
        final String               json        = "{\"key\":\"value\"}";
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        final JsonElement          result      = GsonHelper.parseJson(inputStream, StandardCharsets.UTF_8);
        assertEquals("value", result.getAsJsonObject().get("key").getAsString());
    }

    @Test
    void testParseJsonJsonReader() throws IOException {
        final String      json   = "{\"key\":\"value\"}";
        final JsonReader  reader = new JsonReader(new StringReader(json));
        final JsonElement result = GsonHelper.parseJson(reader);
        assertEquals("value", result.getAsJsonObject().get("key").getAsString());
    }

    @Test
    void testGetFirstElement() {
        final JsonArray array = new JsonArray();
        array.add(new JsonPrimitive("first"));
        array.add(new JsonPrimitive("second"));

        final JsonElement result = GsonHelper.getFirst(array);
        assertEquals("first", result.getAsString());
    }

    @Test
    void testGetLastElement() {
        final JsonArray array = new JsonArray();
        array.add(new JsonPrimitive("first"));
        array.add(new JsonPrimitive("last"));

        final JsonElement result = GsonHelper.getLast(array);
        assertEquals("last", result.getAsString());
    }

    @Test
    void testCopy() throws IOException {
        final String       json         = "{\"key\":\"value\",\"number\":42}";
        final JsonReader   reader       = new JsonReader(new StringReader(json));
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter   writer       = new JsonWriter(stringWriter);

        GsonHelper.copy(reader, writer);
        writer.close();

        final String      result   = stringWriter.toString();
        final JsonElement original = GsonHelper.parseJson(json);
        final JsonElement copied   = GsonHelper.parseJson(result);
        assertEquals(original, copied);
    }

    @Test
    void testWriteElement() throws IOException {
        final JsonObject element = new JsonObject();
        element.addProperty("key", "value");

        final StringWriter stringWriter = new StringWriter();
        final JsonWriter   writer       = new JsonWriter(stringWriter);

        GsonHelper.writeElement(element, writer);
        writer.close();

        final String      result = stringWriter.toString();
        final JsonElement parsed = GsonHelper.parseJson(result);
        assertEquals(element, parsed);
    }

    @Test
    void testPrettifyElement() {
        final JsonObject element = new JsonObject();
        element.addProperty("key", "value");

        final String result = GsonHelper.prettify(element);
        // Should be pretty printed (contains newlines and indentation)
        assertEquals("{\n  \"key\": \"value\"\n}", result);
    }

    @Test
    void testPrettifyString() {
        final String compactJson = "{\"key\":\"value\"}";
        final String result      = GsonHelper.prettify(compactJson);
        // Should be pretty printed (contains newlines and indentation)
        assertEquals("{\n  \"key\": \"value\"\n}", result);
    }

    @Test
    void testGetParser() {
        final JsonParser  parser = GsonHelper.getParser();
        final JsonElement result = parser.parse("{\"key\":\"value\"}");
        assertEquals("value", result.getAsJsonObject().get("key").getAsString());
    }

    @Test
    void testNewJsonReader() throws IOException {
        final StringReader stringReader = new StringReader("{\"key\":\"value\"}");
        final JsonReader   reader       = GsonHelper.newJsonReader(stringReader);

        reader.beginObject();
        assertEquals("key", reader.nextName());
        assertEquals("value", reader.nextString());
        reader.endObject();
        reader.close();
    }

    @Test
    void testNewJsonWriter() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter   writer       = GsonHelper.newJsonWriter(stringWriter);

        writer.beginObject();
        writer.name("key").value("value");
        writer.endObject();
        writer.close();

        final String result = stringWriter.toString();
        assertEquals("{\n  \"key\": \"value\"\n}", result);
    }
}