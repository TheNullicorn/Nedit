package me.nullicorn.nedit.provider;

import java.lang.reflect.Array;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
abstract class ArrayBasedArgumentProvider implements ArgumentsProvider {

    abstract Object provide();

    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
        Object array = provide();
        if (array == null) {
            throw new IllegalArgumentException("Provider array cannot be null");
        } else if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("Not an array: " + array);
        }
        int length = Array.getLength(array);

        // Build a stream of the array's values.
        Stream.Builder<Object> stream = Stream.builder();
        for (int i = 0; i < length; i++) {
            Object value = Array.get(array, i);
            stream.accept(value);
        }

        // Map the values into arguments.
        return stream.build().map(Arguments::of);
    }
}
