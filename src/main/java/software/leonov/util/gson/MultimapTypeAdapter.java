package software.leonov.util.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * A {@link TypeAdapter} which can serialize and deserialize of {@link Multimap} values to and from JSON.
 * <p>
 * This adapter supports complex key serialization if the provided {@code Gson} instance
 * {@link GsonBuilder#enableComplexMapKeySerialization() supports} it.
 *
 * @param <K> the type of keys
 * @param <V> the type of mapped values
 */
public final class MultimapTypeAdapter<K, V> extends TypeAdapter<Multimap<K, V>> {

    private final Type mapType = new TypeToken<Map<K, Collection<V>>>() {
    }.getType();

    private final Gson                               gson;
    private final TypeAdapter<K>                     keyAdapter;
    private final TypeAdapter<V>                     valueAdapter;
    private final Supplier<? extends Multimap<K, V>> supplier;

    /**
     * Constructs a new {@code MultimapTypeAdapter} which can serialize and deserialize {@code Multimap} values as
     * {@link ArrayListMultimap}s.
     *
     * @param gson      a {@code Gson} instance which can handle the serialization and deserialization of the type of keys
     *                  and values contained within the {@code Multimap}
     * @param keyType   the {@link Type} of keys
     * @param valueType the {@link Type} of mapped values
     */
    public MultimapTypeAdapter(final Gson gson, final Type keyType, final Type valueType) {
        this(gson, keyType, valueType, () -> ArrayListMultimap.create());
    }

    /**
     * Constructs a new {@code MultimapTypeAdapter} which can serialize and deserialize {@code Multimap} values.
     * <p>
     * While this constructor can be invoked manually, it will most commonly be invoked by
     * {@link MultimapTypeAdapterFactory}, which in turn will be registered with the provided {@link Gson} instance.
     *
     * @param gson      a {@code Gson} instance which can handle the serialization and deserialization of the type of keys
     *                  and values contained within the {@code Multimap}
     * @param keyType   the {@link Type} of keys
     * @param valueType the {@link Type} of mapped values
     * @param supplier  a {@link Supplier} which returns empty {@code Multimap} instances
     */
    @SuppressWarnings("unchecked")
    public MultimapTypeAdapter(final Gson gson, final Type keyType, final Type valueType, final Supplier<? extends Multimap<K, V>> supplier) {
        checkNotNull(gson, "gson == null");
        checkNotNull(keyType, "keyType == null");
        checkNotNull(valueType, "valueType == null");
        checkNotNull(supplier, "supplier == null");

        this.gson         = gson;
        this.keyAdapter   = (TypeAdapter<K>) gson.getAdapter(TypeToken.get(keyType));
        this.valueAdapter = (TypeAdapter<V>) gson.getAdapter(TypeToken.get(valueType));
        this.supplier     = supplier;
    }

    /**
     * Writes the specified {@code Multimap} value to the {@code JsonWriter}.
     * 
     * @param out   the specified {@code JsonWriter}
     * @param value the {@code Multimap} value to write
     */
    @Override
    public void write(final JsonWriter out, final Multimap<K, V> value) throws IOException {
        checkNotNull(out, "out == null");

        if (value == null) {
            out.nullValue();
            return;
        }

        gson.toJson(value.asMap(), mapType, out);
    }

    /**
     * Returns the next {@code Multimap} value read from the specified {@code JsonReader}.
     * 
     * @return the next {@code Multimap} value read from the specified {@code JsonReader}
     */
    @Override
    public Multimap<K, V> read(final JsonReader in) throws IOException {
        checkNotNull(in, "in == null");

        final Multimap<K, V> multimap = supplier.get();

        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else if (in.peek() == JsonToken.BEGIN_ARRAY) { // see GsonBuilder.enableComplexMapKeySerialization()
            in.beginArray(); // elements
            while (in.hasNext()) {
                in.beginArray(); // element
                final K key = keyAdapter.read(in); // key
                in.beginArray(); // values
                while (in.hasNext()) {
                    final V value = valueAdapter.read(in); // value
                    multimap.put(key, value);
                }
                in.endArray();
                in.endArray();
            }
            in.endArray();
        } else {
            in.beginObject();
            while (in.hasNext()) {
                final String keyJson = "\"" + in.nextName() + "\"";
                final K      key     = keyAdapter.fromJson(keyJson);

                in.beginArray();
                while (in.hasNext()) {
                    final V value = valueAdapter.read(in);
                    multimap.put(key, value);
                }
                in.endArray();
            }
            in.endObject();
        }

        return multimap;
    }

}