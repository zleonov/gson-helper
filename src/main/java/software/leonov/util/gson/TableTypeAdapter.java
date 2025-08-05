package software.leonov.util.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * A {@link TypeAdapter} which can serialize and deserialize of {@link Table} values to and from JSON.
 * <p>
 * This adapter supports complex key serialization if the provided {@code Gson} instance
 * {@link GsonBuilder#enableComplexMapKeySerialization() supports} it.
 *
 * @param <R> the type of row keys
 * @param <C> the type of column keys
 * @param <V> the type of mapped values
 */
public final class TableTypeAdapter<R, C, V> extends TypeAdapter<Table<R, C, V>> {

    private final Type mapType = new TypeToken<Map<R, Map<C, V>>>() {
    }.getType();

    private final Gson                               gson;
    private final TypeAdapter<R>                     rowKeyAdapter;
    private final TypeAdapter<C>                     columnKeyAdapter;
    private final TypeAdapter<V>                     valueAdapter;
    private final Supplier<? extends Table<R, C, V>> supplier;

    /**
     * Constructs a new {@code TableTypeAdapter} which can serialize and deserialize {@code Table} values.
     * 
     * @param gson       a {@code Gson} instance which can handle the serialization and deserialization of the type of keys
     *                   and values contained within the {@code Table}
     * @param rowType    the {@link Type} of row keys
     * @param columnType the {@link Type} of column keys
     * @param valueType  the {@link Type} of mapped values
     */
    public TableTypeAdapter(final Gson gson, final Type rowType, final Type columnType, final Type valueType) {
        this(gson, rowType, columnType, valueType, () -> HashBasedTable.create());
    }

    /**
     * Constructs a new {@code TableTypeAdapter} which can serialize and deserialize {@code Table} values.
     * <p>
     * While this constructor can be invoked manually, it will most commonly be invoked by {@link TableTypeAdapterFactory},
     * which in turn will be registered with the provided {@link Gson} instance.
     * 
     * @param gson       a {@code Gson} instance which can handle the serialization and deserialization of the type of keys
     *                   and values contained within the {@code Table}
     * @param rowType    the {@link Type} of row keys
     * @param columnType the {@link Type} of column keys
     * @param valueType  the {@link Type} of mapped values
     * @param supplier   a {@link Supplier} which returns empty {@code Table} instances
     */
    @SuppressWarnings("unchecked")
    public TableTypeAdapter(final Gson gson, final Type rowType, final Type columnType, final Type valueType, final Supplier<? extends Table<R, C, V>> supplier) {
        checkNotNull(gson, "gson == null");
        checkNotNull(rowType, "rowType == null");
        checkNotNull(columnType, "columnType == null");
        checkNotNull(valueType, "valueType == null");
        checkNotNull(supplier, "supplier == null");

        this.gson             = gson;
        this.rowKeyAdapter    = (TypeAdapter<R>) gson.getAdapter(TypeToken.get(rowType));
        this.columnKeyAdapter = (TypeAdapter<C>) gson.getAdapter(TypeToken.get(columnType));
        this.valueAdapter     = (TypeAdapter<V>) gson.getAdapter(TypeToken.get(valueType));
        this.supplier         = supplier;
    }

    @Override
    public void write(final JsonWriter out, final Table<R, C, V> table) throws IOException {
        checkNotNull(out, "out == null");

        if (table == null) {
            out.nullValue();
            return;
        }

        gson.toJson(table.rowMap(), mapType, out);
    }

    @Override
    public Table<R, C, V> read(final JsonReader in) throws IOException {
        checkNotNull(in, "in == null");

        final Table<R, C, V> table = supplier.get();

        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else if (in.peek() == JsonToken.BEGIN_ARRAY) { // see GsonBuilder.enableComplexMapKeySerialization()
            in.beginArray(); // elements
            while (in.hasNext()) {
                in.beginArray(); // element
                final R rowKey = rowKeyAdapter.read(in); // row key

                if (in.peek() == JsonToken.BEGIN_ARRAY) { // see GsonBuilder.enableComplexMapKeySerialization()
                    in.beginArray(); // elements
                    while (in.hasNext()) {
                        in.beginArray(); // element
                        final C colKey = columnKeyAdapter.read(in); // key
                        final V value  = valueAdapter.read(in);     // value
                        in.endArray();
                        table.put(rowKey, colKey, value);
                    }
                    in.endArray();
                } else {
                    in.beginObject();
                    final String colKeyJson = "\"" + in.nextName() + "\"";          // row key
                    final C      colKey     = columnKeyAdapter.fromJson(colKeyJson);
                    final V      value      = valueAdapter.read(in);
                    in.endObject();
                    table.put(rowKey, colKey, value);
                }

                in.endArray();
            }
            in.endArray();
        } else {
            in.beginObject(); // row map
            while (in.hasNext()) {

                final String rowKeyJson = "\"" + in.nextName() + "\"";       // row key
                final R      rowKey     = rowKeyAdapter.fromJson(rowKeyJson);

                in.beginObject(); // column map
                while (in.hasNext()) {
                    final String columnKeyJson = "\"" + in.nextName() + "\"";             // column key
                    final C      columnKey     = columnKeyAdapter.fromJson(columnKeyJson);

                    final V value = valueAdapter.read(in); // value

                    table.put(rowKey, columnKey, value);
                }
                in.endObject(); // end of column map
            }
            in.endObject(); // end of row map
        }
        return table;
    }
}