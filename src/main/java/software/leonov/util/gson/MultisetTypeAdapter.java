package software.leonov.util.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * A {@link TypeAdapter} which can serialize and deserialize of {@link Multiset} values to and from JSON.
 *
 * @param <E> the type of elements contained in the {@code Multiset}
 */
public final class MultisetTypeAdapter<E> extends TypeAdapter<Multiset<E>> {

    private final TypeAdapter<E>                  adapter;
    private final Supplier<? extends Multiset<E>> supplier;

    /**
     * Constructs a new {@code MultisetTypeAdapter} which can serialize and deserialize {@code Multiset} values as
     * {@link LinkedHashMultiset}s.
     *
     * @param gson a {@code Gson} instance which can handle the serialization and deserialization of the type of elements
     *             contained within the {@code Multiset}
     * @param type the {@link Type} of elements
     */
    public MultisetTypeAdapter(final Gson gson, final Type type) {
        this(gson, type, () -> LinkedHashMultiset.create());
    }

    /**
     * Constructs a new {@code MultisetTypeAdapter} which can serialize and deserialize {@code Multiset} values.
     * <p>
     * While this constructor can be invoked manually, it will most commonly be invoked by
     * {@link MultisetTypeAdapterFactory}, which in turn will be registered with the provided {@link Gson} instance.
     *
     * @param gson     a {@code Gson} instance which can handle the serialization and deserialization of the type of
     *                 elements contained within the {@code Multiset}
     * @param type     the {@link Type} of elements
     * @param supplier a {@link Supplier} which returns empty {@code Multiset} instances
     */
    @SuppressWarnings("unchecked")
    public MultisetTypeAdapter(final Gson gson, final Type type, final Supplier<? extends Multiset<E>> supplier) {
        checkNotNull(gson, "gson == null");
        checkNotNull(type, "type == null");
        checkNotNull(supplier, "supplier == null");

        this.adapter  = (TypeAdapter<E>) gson.getAdapter(TypeToken.get(type));
        this.supplier = supplier;
    }

    /**
     * Writes the specified {@code Multiset} value to the {@code JsonWriter}.
     * 
     * @param out   the specified {@code JsonWriter}
     * @param value the {@code Multiset} value to write
     */
    @Override
    public void write(final JsonWriter out, final Multiset<E> value) throws IOException {
        checkNotNull(out, "out == null");

        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginArray();
        for (final Multiset.Entry<E> entry : value.entrySet()) {
            out.beginArray();
            adapter.write(out, entry.getElement());
            out.value(entry.getCount());
            out.endArray();
        }
        out.endArray();
    }

    /**
     * Returns the next {@code Multiset} value read from the specified {@code JsonReader}.
     * 
     * @return the next {@code Multiset} value read from the specified {@code JsonReader}
     */
    @Override
    public Multiset<E> read(final JsonReader in) throws IOException {
        checkNotNull(in, "in == null");

        final Multiset<E> multiset = supplier.get();

        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        in.beginArray();
        while (in.hasNext()) {
            in.beginArray();
            final E   element = adapter.read(in);
            final int count   = in.nextInt();
            multiset.add(element, count);
            in.endArray();
        }
        in.endArray();

        return multiset;
    }

}