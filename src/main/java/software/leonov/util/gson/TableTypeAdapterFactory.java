package software.leonov.util.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.RowSortedTable;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Creates {@link TypeAdapter}s for serializing and deserializing {@link Table} values to and from JSON.
 * <p>
 * <strong>Supported {@code Table} Types:</strong>
 * <ul>
 * <li>{@link HashBasedTable} (default for deserialization)</li>
 * <li>{@link TreeBasedTable}</li>
 * </ul>
 * <p>
 * <b>Serialization/Deserialization Behavior:</b>
 * <p>
 * All {@code Table} implementations can be serialized to JSON. Attempting to deserialize unsupported {@code Table}
 * implementations will result in an {@link IllegalArgumentException}.
 *
 * @author Zhenya Leonov
 */
public final class TableTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        checkNotNull(gson, "gson == null");
        checkNotNull(typeToken, "typeToken == null");

        final Class<? super T> clazz = typeToken.getRawType();

        if (Table.class.isAssignableFrom(clazz)) {
            final Type type = typeToken.getType();

            final Type rowKeyType;
            final Type columnKeyType;
            final Type valueType;

            if (type instanceof ParameterizedType) {
                final ParameterizedType paramType = (ParameterizedType) type;
                rowKeyType    = paramType.getActualTypeArguments()[0];
                columnKeyType = paramType.getActualTypeArguments()[1];
                valueType     = paramType.getActualTypeArguments()[2];
            } else {
                rowKeyType    = Object.class;
                columnKeyType = Object.class;
                valueType     = Object.class;
            }

            if (clazz.equals(Table.class) || clazz.equals(HashBasedTable.class))
                return (TypeAdapter<T>) new TableTypeAdapter<>(gson, rowKeyType, columnKeyType, valueType, () -> HashBasedTable.create());
            else if (clazz.equals(RowSortedTable.class) || clazz.equals(TreeBasedTable.class))
                return (TypeAdapter<T>) new TableTypeAdapter<>(gson, rowKeyType, columnKeyType, valueType, () -> TreeBasedTable.create());
            else
                return (TypeAdapter<T>) new TableTypeAdapter<>(gson, rowKeyType, columnKeyType, valueType, () -> {
                    throw new IllegalArgumentException(String.format("%s is not supported; try one of [%s, %s]", clazz.getSimpleName(), HashBasedTable.class.getSimpleName(), TreeBasedTable.class.getSimpleName()));
                });

        }

        return null;
    }
}