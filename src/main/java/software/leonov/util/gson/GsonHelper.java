package software.leonov.util.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * A collection of static utility methods that simplify common serialization and deserialization operations using
 * <a href="https://github.com/google/gson">Google Gson</a>.
 * <p>
 * At the core of {@code GsonHelper} is a {@link #getGson() default} {@code Gson} instance (suitable for general use
 * cases) which supports Java 8+
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html">java.time</a> classes,
 * {@link Optional}s, Guava's {@link Multiset}, {@link Multimap}, and {@link Table} collection types,
 * {@link GsonBuilder#serializeNulls() serializes nulls}, {@link GsonBuilder#disableHtmlEscaping() does not escape HTML
 * characters}, and outputs {@link GsonBuilder#setPrettyPrinting() <i>pretty</i> printed} JSON text.
 * <p>
 * If you would like to use your own {@code Gson} instance you can register adapters for
 * {@link #registerAll(GsonBuilder) Guava's collection types} along with {@link Converters#registerAll(GsonBuilder)
 * gson-javatime-serialisers} manually.
 * <p>
 * For example: <pre><code class="line-numbers match-braces language-java">
 *   private static final Gson GSON = GsonHelper.registerAll(Converters.registerAll(new GsonBuilder())
 *           .serializeNulls()
 *           .setPrettyPrinting()
 *           .disableHtmlEscaping())
 *           .enableComplexMapKeySerialization()
 *           .create();
 * </code></pre>
 * 
 * @author Zhenya Leonov
 */
public final class GsonHelper {

    // @formatter:off
    private static final Gson GSON = registerAll(Converters.registerAll(new GsonBuilder())
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping())
            .enableComplexMapKeySerialization()
            .create();
    // @formatter:on;

    private static final JsonParser JSON_PARSER = new JsonParser();

    private GsonHelper() {
    }

    /**
     * Returns a new JSON reader configured with the settings of the {@link #getGson() default Gson instance}.
     * 
     * @param reader the specified reader
     * @return a new JSON reader configured with the settings of the {@link #getGson() default Gson instance}.
     * @throws IOException if an I/O error occurs
     */
    public static JsonReader newJsonReader(final Reader reader) throws IOException {
        return GSON.newJsonReader(reader);
    }

    /**
     * Returns a new JSON writer configured with the settings of the {@link #getGson() default Gson instance}.
     * 
     * @param writer the underlying writer
     * @return a new JSON writer configured with the settings of the {@link #getGson() default Gson instance}.
     * @throws IOException if an I/O error occurs
     */
    public static JsonWriter newJsonWriter(final Writer writer) throws IOException {
        return GSON.newJsonWriter(writer);
    }

    /**
     * Registers {@link OptionalTypeAdapterFactory}, {@link MultisetTypeAdapterFactory}, {@link MultimapTypeAdapterFactory},
     * and {@link TableTypeAdapterFactory} with the specified {@link GsonBuilder}.
     * 
     * @param builder the specified GSON builder
     * @return the specified GSON builder
     */
    public static GsonBuilder registerAll(final GsonBuilder builder) {
        checkNotNull(builder, "builder == null");

        // @formatter:off
        builder.registerTypeAdapterFactory(new OptionalTypeAdapterFactory())
               .registerTypeAdapterFactory(new MultisetTypeAdapterFactory())
               .registerTypeAdapterFactory(new MultimapTypeAdapterFactory())
               .registerTypeAdapterFactory(new TableTypeAdapterFactory());
        // @formatter:on

        return builder;
    }

    /**
     * Returns a {@link Gson} instance (suitable for general use cases) which supports Java 8+
     * <a href="https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html">java.time</a> classes,
     * {@link Optional}s, Guava's {@link Multiset}, {@link Multimap}, and {@link Table} collection types,
     * {@link GsonBuilder#serializeNulls() serializes nulls}, {@link GsonBuilder#disableHtmlEscaping() does not escape HTML
     * characters}, and outputs {@link GsonBuilder#setPrettyPrinting() <i>pretty</i> printed} JSON text.
     * 
     * @return a {@link Gson} instance (suitable for general use cases) which supports Java 8+
     *         <a href="https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html">java.time</a> classes,
     *         {@link Optional}s, Guava's {@link Multiset}, {@link Multimap}, and {@link Table} collection types,
     *         {@link GsonBuilder#serializeNulls() serializes nulls}, {@link GsonBuilder#disableHtmlEscaping() does not
     *         escape HTML characters}, and outputs {@link GsonBuilder#setPrettyPrinting() <i>pretty</i> printed} JSON text
     */
    public static Gson getGson() {
        return GSON;
    }

    /**
     * Consumes and returns the next {@link JsonToken} as a {@code Boolean} value or {@code null} if the next token is
     * {@link JsonToken#NULL}.
     * 
     * @param in the {@code JsonReader} to read from
     * @return the next {@link JsonToken} as a {@code Boolean} value or {@code null} if the next token is
     *         {@link JsonToken#NULL}
     * @throws IOException if an I/O error occurs
     */
    public static Boolean nextBoolean(final JsonReader in) throws IOException {
        return nextBoolean(in, null);
    }

    /**
     * Consumes and returns the next {@link JsonToken} as a {@code Boolean} value or the default value if the next token is
     * {@link JsonToken#NULL}.
     * 
     * @param in           the {@code JsonReader} to read from
     * @param defaultValue the value to return if the next token is {@link JsonToken#NULL}
     * @return the next {@link JsonToken} as a {@code Boolean} value or the default value if the next token is
     *         {@link JsonToken#NULL}
     * @throws IOException if an I/O error occurs
     */
    public static Boolean nextBoolean(final JsonReader in, final Boolean defaultValue) throws IOException {
        checkNotNull(in, "in == null");
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return defaultValue;
        } else
            return in.nextBoolean();
    }

    /**
     * Consumes and returns the next {@link JsonToken} as a {@code Double} value or {@code null} if the next token is
     * {@link JsonToken#NULL}.
     * 
     * @param in the {@code JsonReader} to read from
     * @return the next {@link JsonToken} as a {@code Double} value or {@code null} if the next token is
     *         {@link JsonToken#NULL}
     * @throws IOException if an I/O error occurs
     */
    public static Double nextDouble(final JsonReader in) throws IOException {
        return nextDouble(in, null);
    }

    /**
     * Consumes and returns the next {@link JsonToken} as a {@code Double} value or the default value if the next token is
     * {@link JsonToken#NULL}.
     * 
     * @param in           the {@code JsonReader} to read from
     * @param defaultValue the value to return if the next token is {@link JsonToken#NULL}
     * @return the next {@link JsonToken} as a {@code Double} value or the default value if the next token is
     *         {@link JsonToken#NULL}
     * @throws IOException if an I/O error occurs
     */
    public static Double nextDouble(final JsonReader in, final Double defaultValue) throws IOException {
        checkNotNull(in, "in == null");
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return defaultValue;
        } else
            return in.nextDouble();
    }

    /**
     * Consumes and returns the next {@code JsonElement} parsed from the specified {@code JsonReader}. The element is either
     * a {@link JsonObject}, a {@link JsonArray}, a {@link JsonPrimitive} or a {@link JsonNull}.
     * 
     * @param in the {@code JsonReader} to read from
     * @return returns the next {@code JsonElement} parsed from the specified {@code JsonReader}
     */
    public static JsonElement nextElement(final JsonReader in) {
        checkNotNull(in, "in == null");
        return JSON_PARSER.parse(in);
    }

    /**
     * Consumes and returns the next {@link JsonToken} as an {@code Integer} value or {@code null} if the next token is
     * {@link JsonToken#NULL}.
     * 
     * @param in the {@code JsonReader} to read from
     * @return the next {@link JsonToken} as an {@code Integer} value or {@code null} if the next token is
     *         {@link JsonToken#NULL}
     * @throws IOException if an I/O error occurs
     */
    public static Integer nextInteger(final JsonReader in) throws IOException {
        return nextInteger(in, null);
    }

    /**
     * Consumes and returns the next {@link JsonToken} as an {@code Integer} value or the default value if the next token is
     * {@link JsonToken#NULL}.
     * 
     * @param in           the {@code JsonReader} to read from
     * @param defaultValue the value to return if the next token is {@link JsonToken#NULL}
     * @return the next {@link JsonToken} as an {@code Integer} value or the default value if the next token is
     *         {@link JsonToken#NULL}
     * @throws IOException if an I/O error occurs
     */
    public static Integer nextInteger(final JsonReader in, final Integer defaultValue) throws IOException {
        checkNotNull(in, "in == null");
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return defaultValue;
        } else
            return in.nextInt();
    }

    /**
     * Consumes and returns the next {@link JsonToken} as a {@code Long} value or {@code null} if the next token is
     * {@link JsonToken#NULL}.
     * 
     * @param in the {@code JsonReader} to read from
     * @return the next {@link JsonToken} as a {@code Long} value or {@code null} if the next token is
     *         {@link JsonToken#NULL}
     * @throws IOException if an I/O error occurs
     */
    public static Long nextLong(final JsonReader in) throws IOException {
        return nextLong(in, null);
    }

    /**
     * Consumes and returns the next {@link JsonToken} as a {@code Long} value or the default value if the next token is
     * {@link JsonToken#NULL}.
     * 
     * @param in           the {@code JsonReader} to read from
     * @param defaultValue the value to return if the next token is {@link JsonToken#NULL}
     * @return the next {@link JsonToken} as a {@code Long} value or the default value if the next token is
     *         {@link JsonToken#NULL}
     * @throws IOException if an I/O error occurs
     */
    public static Long nextLong(final JsonReader in, final Long defaultValue) throws IOException {
        checkNotNull(in, "in == null");
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return defaultValue;
        } else
            return in.nextLong();
    }

    /**
     * Consumes and returns the next {@link JsonToken} as a {@code String} or {@code null} if the next token is
     * {@link JsonToken#NULL}.
     * 
     * @param in the {@code JsonReader} to read from
     * @return the next {@link JsonToken} as a {@code String} or {@code null} if the next token is {@link JsonToken#NULL}
     * @throws IOException if an I/O error occurs
     */
    public static String nextString(final JsonReader in) throws IOException {
        return nextString(in, null);
    }

    /**
     * Consumes and returns the next {@link JsonToken} as a {@code String} or the default value if the next token is
     * {@link JsonToken#NULL}.
     * 
     * @param in           the {@code JsonReader} to read from
     * @param defaultValue the value to return if the next token is {@link JsonToken#NULL}
     * @return the next {@link JsonToken} as a {@code String} or the default value if the next token is
     *         {@link JsonToken#NULL}
     * @throws IOException if an I/O error occurs
     */
    public static String nextString(final JsonReader in, final String defaultValue) throws IOException {
        checkNotNull(in, "in == null");
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return defaultValue;
        } else
            return in.nextString();
    }

    /**
     * Consumes and returns the next {@link JsonToken} and asserts that it is a literal {@code null}.
     * <p>
     * This method behaves identically to the {@code void} method {@link JsonReader#nextNull()} but returns a {@code null}
     * reference to allow for more streamlined code when assigning the {@code null} value in a one-liner.
     * 
     * @param in the {@code JsonReader} to read from
     * @return {@code null}
     * @throws IOException if an I/O error occurs
     */
    public static Void nextNull(final JsonReader in) throws IOException {
        checkNotNull(in, "in == null");
        in.nextNull();
        return null;
    }

    /**
     * Consumes and skips the next value and returns the default value.
     * 
     * @param <T>          the type of {@code defaultValue}
     * @param in           the {@code JsonReader} to read from
     * @param defaultValue the value to return
     * @return the default value
     * @throws IOException if an I/O error occurs
     */
    public static <T> T skipValue(final JsonReader in, final T defaultValue) throws IOException {
        checkNotNull(in, "in == null");
        in.skipValue();
        return defaultValue;
    }

    /**
     * Returns the first element in the {@code JsonArray}.
     * 
     * @param array the specified array
     * @return the first element in the {@code JsonArray}
     */
    public static JsonElement getFirst(final JsonArray array) {
        checkNotNull(array, "array == null");
        return array.get(0);
    }

    /**
     * Returns the last element in the {@code JsonArray}.
     * 
     * @param array the specified array
     * @return the last element in the {@code JsonArray}
     */
    public static JsonElement getLast(final JsonArray array) {
        checkNotNull(array, "array == null");
        return array.get(array.size() - 1);
    }

    /**
     * Parses the specified JSON text.
     * 
     * @param text the JSON text to parse
     * @return a parse tree of {@link JsonElement}s corresponding to the specified JSON text
     */
    public static JsonElement parseJson(final String text) {
        checkNotNull(text, "text == null");
        return JSON_PARSER.parse(text);
    }

    /**
     * Parses JSON content from the specified reader.
     * 
     * @param reader the specified reader
     * @return a tree of {@link JsonElement}s parsed from the specified reader
     */
    public static JsonElement parseJson(final Reader reader) {
        checkNotNull(reader, "reader == null");
        return JSON_PARSER.parse(reader);
    }

    /**
     * Consumes and returns the next {@code JsonElement} parsed from the specified {@code JsonReader}. The element is either
     * a {@link JsonObject}, a {@link JsonArray}, a {@link JsonPrimitive} or a {@link JsonNull}.
     * <p>
     * This method is identical to {@link #nextElement(JsonReader)}.
     * 
     * @param in the {@code JsonReader} to read from
     * @return returns the next {@code JsonElement} parsed from the specified {@code JsonReader}
     */
    public static JsonElement parseJson(final JsonReader in) {
        checkNotNull(in, "in == null");
        return nextElement(in);
    }

    /**
     * Parses JSON content from an {@code InputStream} using the {@link StandardCharsets#UTF_8 UTF-8} charset.
     * <p>
     * <b>Note:</b> Buffering the input stream is superfluous.
     * 
     * @param in the {@code InputStream} to read from
     * @return a tree of {@link JsonElement}s parsed from the specified {@code InputStream}
     * @throws IOException if an I/O error occurs
     */
    public static JsonElement parseJson(final InputStream in) throws IOException {
        checkNotNull(in, "in == null");
        return parseJson(in, StandardCharsets.UTF_8);
    }

    /**
     * Parses JSON content from the {@code InputStream} using the specified {@code Charset}.
     * 
     * @param in      the {@code InputStream} to read from
     * @param charset the specified {@code Charset}
     * @return a parse tree of {@link JsonElement}s parsed from the specified {@code InputStream}
     * @throws IOException if an I/O error occurs
     */
    public static JsonElement parseJson(final InputStream in, final Charset charset) throws IOException {
        checkNotNull(in, "in == null");
        checkNotNull(charset, "charset == null");
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset))) {
            return JSON_PARSER.parse(reader);
        }
    }

    /**
     * Copies all {@code JsonToken}s from the specified {@code JsonReader} to the given {@code JsonWriter}.
     * <p>
     * Does not close the reader or the writer.
     * 
     * @param <W>  the type of {@code JsonWriter}
     * @param from the specified {@code JsonReader}
     * @param to   the given {@code JsonWriter}
     * @return the given {@code JsonWriter}
     * @throws IOException if an I/O error occurs
     */
    public static <W extends JsonWriter> W copy(final JsonReader from, final W to) throws IOException {
        checkNotNull(from, "from == null");
        checkNotNull(to, "to == null");

        for (JsonToken peek = from.peek(); peek != JsonToken.END_DOCUMENT; peek = from.peek()) {
            if (peek == JsonToken.BEGIN_ARRAY) {
                from.beginArray();
                to.beginArray();
            } else if (peek == JsonToken.BEGIN_OBJECT) {
                from.beginObject();
                to.beginObject();
            } else if (peek == JsonToken.BOOLEAN)
                to.value(from.nextBoolean());
            else if (peek == JsonToken.END_ARRAY) {
                from.endArray();
                to.endArray();
            } else if (peek == JsonToken.END_OBJECT) {
                from.endObject();
                to.endObject();
            } else if (peek == JsonToken.NAME)
                to.name(from.nextName());
            else if (peek == JsonToken.NULL) {
                from.nextNull();
                to.nullValue();
            } else if (peek == JsonToken.NUMBER)
                to.value(new BigDecimal(from.nextString()));
            else if (peek == JsonToken.STRING)
                to.value(from.nextString());
        }

        to.flush();
        return to;
    }

    /**
     * Returns a singleton {@link JsonParser} instance which can parse JSON text into a parse tree of {@link JsonElement}s.
     * 
     * @return a singleton {@link JsonParser} instance which can parse JSON text into a parse tree of {@link JsonElement}s
     */
    public static JsonParser getParser() {
        return JSON_PARSER;
    }

    /**
     * Writes the specified {@code JsonElement} to the given {@code JsonWriter}.
     * 
     * @param <W>     the type of {@code JsonWriter}
     * @param element the specified {@code JsonElement}
     * @param out     the given {@code JsonWriter}
     * @return the given {@code JsonWriter}
     * @throws IOException if an I/O error occurs
     */
    public static <W extends JsonWriter> W writeElement(final JsonElement element, final W out) throws IOException {
        checkNotNull(element, "element == null");
        checkNotNull(out, "out == null");
        return copy(new JsonTreeReader(element), out);
    }

    /**
     * Parses and returns the specified {@code JsonElement} as <i>pretty printed</i> JSON text.
     * 
     * @param element the specified {@code JsonElement}
     * @return the specified {@code JsonElement} as <i>pretty printed</i> JSON text
     */
    public static String prettify(final JsonElement element) {
        checkNotNull(element, "element == null");
        return getGson().toJson(element);
    }

    /**
     * Returns <i>pretty printed</i> JSON text from the original unformatted string.
     * <p>
     * The JSON string must be valid according to <a href="http://www.ietf.org/rfc/rfc4627.txt">RFC 4627</a>.
     * 
     * @param text the JSON {@code String} to format
     * @return <i>pretty printed</i> JSON text from the original unformatted string
     */
    public static String prettify(final String text) {
        checkNotNull(text, "text == null");
        return getGson().toJson(parseJson(text));
    }

}
