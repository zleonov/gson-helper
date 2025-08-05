package software.leonov.util.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Creates {@link TypeAdapter}s for serializing and deserializing {@link Multimap} values to and from JSON.
 * <p>
 * <strong>Supported {@code Multiset} Types:</strong>
 * <ul>
 * <li>{@link ArrayListMultimap} (default for deserialization)</li>
 * <li>{@link HashMultimap}</li>
 * <li>{@link LinkedHashMultimap}</li>
 * <li>{@link TreeMultimap}</li>
 * <li>{@link LinkedListMultimap}</li>
 * </ul>
 * <p>
 * <b>Serialization/Deserialization Behavior:</b>
 * <p>
 * All {@code Multimap} implementations can be serialized to JSON. Attempting to deserialize unsupported
 * {@code Multimap} implementations will result in an {@link IllegalArgumentException}.
 *
 * @author Zhenya Leonov
 */
public final class MultimapTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        checkNotNull(gson, "gson == null");
        checkNotNull(typeToken, "typeToken == null");

        final Class<? super T> clazz = typeToken.getRawType();

        if (Multimap.class.isAssignableFrom(clazz)) {
            final Type type = typeToken.getType();

            final Type keyType;
            final Type valueType;

            if (type instanceof ParameterizedType) {
                final ParameterizedType paramType = (ParameterizedType) type;
                keyType   = paramType.getActualTypeArguments()[0];
                valueType = paramType.getActualTypeArguments()[1];
            } else {
                keyType   = Object.class;
                valueType = Object.class;
            }

            if (clazz.equals(Multimap.class) || clazz.equals(ListMultimap.class) || clazz.equals(ArrayListMultimap.class))
                return (TypeAdapter<T>) new MultimapTypeAdapter<>(gson, keyType, valueType, () -> ArrayListMultimap.create());
            else if (clazz.equals(SetMultimap.class) || clazz.equals(HashMultimap.class))
                return (TypeAdapter<T>) new MultimapTypeAdapter<>(gson, keyType, valueType, () -> HashMultimap.create());
            else if (clazz.equals(LinkedHashMultimap.class))
                return (TypeAdapter<T>) new MultimapTypeAdapter<>(gson, keyType, valueType, () -> LinkedHashMultimap.create());
            else if (clazz.equals(SortedSetMultimap.class) || clazz.equals(TreeMultimap.class))
                return (TypeAdapter<T>) new MultimapTypeAdapter<>(gson, keyType, valueType, () -> TreeMultimap.create());
            else if (clazz.equals(LinkedListMultimap.class))
                return (TypeAdapter<T>) new MultimapTypeAdapter<>(gson, keyType, valueType, () -> LinkedListMultimap.create());
            else
                return (TypeAdapter<T>) new MultimapTypeAdapter<>(gson, keyType, valueType, () -> {
                    throw new IllegalArgumentException(String.format("%s is not supported; try one of [%s, %s, %s, %s, %s]", clazz.getSimpleName(), ArrayListMultimap.class.getSimpleName(), HashMultimap.class.getSimpleName(),
                            LinkedHashMultimap.class.getSimpleName(), TreeMultimap.class.getSimpleName(), LinkedListMultimap.class.getSimpleName()));
                });

        }

        return null;
    }
}