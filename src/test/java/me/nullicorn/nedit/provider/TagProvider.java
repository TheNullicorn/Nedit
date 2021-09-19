package me.nullicorn.nedit.provider;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * A single-argument provider that provides acceptable values for NBT tags.
 *
 * @author Nullicorn
 */
public abstract class TagProvider implements ArgumentsProvider {

    /**
     * Returns an array of NBT values that will be passed to the provider.
     *
     * @apiNote The returned object *MUST* be an array. May be multi-dimensional if the provided
     * type itself is an array.
     */
    public abstract Object provide();

    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
        Object array = Objects.requireNonNull(provide(), "provide() array cannot be null");
        if (!array.getClass().isArray()) {
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

    /**
     * A functional interface capable of NBT-encoding a single value to a supplied output stream.
     */
    @FunctionalInterface
    public interface NBTEncoder<T> {

        void encode(DataOutputStream out, T value) throws IOException;
    }

    /**
     * A provider of 2 arguments:
     * <ol>
     *     <li>The value of an NBT tag</li>
     *     <li>The sequence of bytes expected if the first argument were to be NBT-encoded</li>
     * </ol>
     */
    public static abstract class NBTEncodedValueProvider implements ArgumentsProvider {

        /**
         * Returns an NBT encoding function compatible with the values returned by this provider.
         */
        public abstract NBTEncoder<?> encoder();

        /**
         * Returns the constructor for an {@code ArgumentsProvider} that this provider can take its
         * values from (the values that will be encoded).
         */
        public abstract Supplier<ArgumentsProvider> provider();

        @SuppressWarnings("unchecked")
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            ArgumentsProvider base = Optional.ofNullable(provider())
                .map(Supplier::get)
                .orElseThrow(() -> new NullPointerException("provider cannot be null"));

            // Returns 2 arguments:
            //   1. The original value
            //   2. That value encoded as a byte[]
            return base.provideArguments(context)
                .map(arg -> {
                    Object[] args = arg.get();

                    // There should only be 1 argument at this point, but check to be sure.
                    if (args == null || args.length > 1) {
                        throw new IllegalArgumentException(
                            "Unexpected args: " + Arrays.toString(args));
                    }

                    return args[0];
                })
                .map(value -> {
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(result);

                    // Encode the result.
                    try {
                        NBTEncoder<Object> encoder = (NBTEncoder<Object>) encoder();
                        encoder.encode(out, value);
                    } catch (IOException e) {
                        throw new UncheckedIOException("Test encoder failed", e);
                    }

                    // Pass on the original value and the encoded bytes.
                    return Arguments.of(value, result.toByteArray());
                });
        }
    }
}
