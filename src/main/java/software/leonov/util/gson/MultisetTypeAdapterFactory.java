package software.leonov.util.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Creates {@link TypeAdapter}s for serializing and deserializing {@link Multiset} values to and from JSON.
 * <p>
 * <strong>Supported {@code Multiset} Types:</strong>
 * <ul>
 * <li>{@link LinkedHashMultiset} (default for deserialization)</li>
 * <li>{@link HashMultiset}</li>
 * <li>{@link TreeMultiset}</li>
 * <li>{@link ConcurrentHashMultiset}</li>
 * </ul>
 * <p>
 * <b>Serialization/Deserialization Behavior:</b>
 * <p>
 * All {@code Multiset} implementations can be serialized to JSON. Attempting to deserialize unsupported
 * {@code Multiset} implementations will result in an {@link IllegalArgumentException}.
 *
 * @author Zhenya Leonov
 */
public final class MultisetTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        checkNotNull(gson, "gson == null");
        checkNotNull(typeToken, "typeToken == null");

        final Class<? super T> clazz = typeToken.getRawType();

        if (Multiset.class.isAssignableFrom(clazz)) {
            final Type type = typeToken.getType();

            final Type elementType = type instanceof ParameterizedType ? ((ParameterizedType) type).getActualTypeArguments()[0] : Object.class;

            if (clazz.equals(HashMultiset.class))
                return (TypeAdapter<T>) new MultisetTypeAdapter<>(gson, elementType, () -> HashMultiset.create());
            else if (clazz.equals(Multiset.class) || clazz.equals(LinkedHashMultiset.class))
                return (TypeAdapter<T>) new MultisetTypeAdapter<>(gson, elementType, () -> LinkedHashMultiset.create());
            else if (clazz.equals(SortedMultiset.class) || clazz.equals(TreeMultiset.class))
                return (TypeAdapter<T>) new MultisetTypeAdapter<>(gson, elementType, () -> TreeMultiset.create());
            else if (clazz.equals(ConcurrentHashMultiset.class))
                return (TypeAdapter<T>) new MultisetTypeAdapter<>(gson, elementType, () -> ConcurrentHashMultiset.create());
            else
                return (TypeAdapter<T>) new MultisetTypeAdapter<>(gson, elementType, () -> {
                    throw new IllegalArgumentException(String.format("%s is not supported; try one of [%s, %s, %s, %s]", clazz.getSimpleName(), HashMultiset.class.getSimpleName(), LinkedHashMultiset.class.getSimpleName(),
                            TreeMultiset.class.getSimpleName(), ConcurrentHashMultiset.class.getSimpleName()));
                });
        }
        return null;
    }
}
