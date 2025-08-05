package software.leonov.util.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * A {@link TypeAdapter} which can serialize and deserialize of {@link Optional} values to and from JSON.
 * <p>
 * {@link Optional#empty() Empty} {@code Optional}s are serialized as JSON-encoded {@link JsonToken#NULL NULL}s while
 * {@link Optional#isPresent() present} values are serialized using an {@link Gson#getAdapter(TypeToken) adapter
 * obtained} from the provided {@code Gson} instance.
 *
 * @param <E> the type of the element contained within the {@code Optional}
 */
public final class OptionalTypeAdapter<E> extends TypeAdapter<Optional<E>> {

    private final TypeAdapter<E> adapter;

    /**
     * Constructs a new {@code OptionalTypeAdapter}.
     * <p>
     * While this constructor can be invoked manually, it will most commonly be invoked by
     * {@link OptionalTypeAdapterFactory}, which in turn will be registered with the provided {@link Gson} instance.
     *
     * @param gson a {@code Gson} instance which can handle the serialization and deserialization of the type of element
     *             contained within the {@code Optional}
     * @param type the {@link Type} of the element contained in the {@code Optional}
     */
    @SuppressWarnings("unchecked")
    public OptionalTypeAdapter(final Gson gson, final Type type) {
        checkNotNull(gson, "gson == null");
        checkNotNull(type, "type == null");

        this.adapter = (TypeAdapter<E>) gson.getAdapter(TypeToken.get(type));
    }

//    /**
//     * Writes the specified {@code Optional} value to the {@code JsonWriter}.
//     * <p>
//     * <b>Warning:</b> Unlike the {@code write} method in most adapters which accepts {@code null} values an
//     * {@code Optional} value cannot itself be {@code null}.
//     * 
//     * @param out   the specified {@code JsonWriter}
//     * @param value the {@code Optional} value to write
//     * @throws NullPointerException if the {@code Optional} value itself is {@code null}
//     */
    @Override
    public void write(final JsonWriter out, final Optional<E> value) throws IOException {
        checkNotNull(out, "out == null");
        checkNotNull(value, "optional == null"); // an Optional itself should never be null

        if (value.isPresent())
            adapter.write(out, value.get());
        else
            out.nullValue();
    }

    @Override
    public Optional<E> read(final JsonReader in) throws IOException {
        checkNotNull(in, "in == null");

        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return Optional.empty();
        }
        return Optional.ofNullable(adapter.read(in));
    }

}