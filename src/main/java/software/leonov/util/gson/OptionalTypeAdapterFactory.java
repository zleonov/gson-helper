package software.leonov.util.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Creates {@link TypeAdapter}s for serializing and deserializing {@link Optional} values to and from JSON.
 * 
 * @author Zhenya Leonov
 */
public final class OptionalTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        checkNotNull(gson, "gson == null");
        checkNotNull(typeToken, "typeToken == null");

        final Class<? super T> clazz = typeToken.getRawType();

        if (Optional.class.isAssignableFrom(clazz)) {
            final Type type = typeToken.getType();

            final Type elementType = type instanceof ParameterizedType ? ((ParameterizedType) type).getActualTypeArguments()[0] : Object.class;

            return (TypeAdapter<T>) new OptionalTypeAdapter<>(gson, elementType);
        }

        return null;
    }

}