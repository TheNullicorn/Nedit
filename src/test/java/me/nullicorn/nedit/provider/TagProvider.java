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
import me.nullicorn.nedit.provider.type.ByteArrayProvider;
import me.nullicorn.nedit.provider.type.ByteProvider;
import me.nullicorn.nedit.provider.type.CompoundProvider;
import me.nullicorn.nedit.provider.type.DoubleProvider;
import me.nullicorn.nedit.provider.type.FloatProvider;
import me.nullicorn.nedit.provider.type.IntArrayProvider;
import me.nullicorn.nedit.provider.type.IntProvider;
import me.nullicorn.nedit.provider.type.ListProvider;
import me.nullicorn.nedit.provider.type.LongArrayProvider;
import me.nullicorn.nedit.provider.type.LongProvider;
import me.nullicorn.nedit.provider.type.ShortProvider;
import me.nullicorn.nedit.provider.type.StringProvider;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * A single-argument provider that provides acceptable values for NBT tags.
 *
 * @author Nullicorn
 */
public abstract class TagProvider<A> implements ArgumentsProvider {

    /**
     * Determines the corresponding argument provider that should be used to provide NBT values of
     * the supplied {@code type}.
     */
    @SuppressWarnings("unchecked")
    public static <A> TagProvider<A> getProviderForType(TagType type) {
        TagProvider<?> provider;

        switch (type) {
            case BYTE:
                provider = new ByteProvider();
                break;
            case SHORT:
                provider = new ShortProvider();
                break;
            case INT:
                provider = new IntProvider();
                break;
            case LONG:
                provider = new LongProvider();
                break;
            case FLOAT:
                provider = new FloatProvider();
                break;
            case DOUBLE:
                provider = new DoubleProvider();
                break;
            case BYTE_ARRAY:
                provider = new ByteArrayProvider();
                break;
            case INT_ARRAY:
                provider = new IntArrayProvider();
                break;
            case LONG_ARRAY:
                provider = new LongArrayProvider();
                break;
            case STRING:
                provider = new StringProvider();
                break;
            case LIST:
                provider = new ListProvider();
                break;
            case COMPOUND:
                provider = new CompoundProvider();
                break;
            default:
                throw new IllegalArgumentException("Unable to find provider for tag: " + type);
        }

        return (TagProvider<A>) provider;
    }

    /**
     * Returns an array of NBT values that will be passed to the provider.
     *
     * @apiNote The returned object *MUST* be an array. May be multi-dimensional if the provided
     * type itself is an array.
     */
    public abstract A provide();

    /**
     * Provides a single, valid value for the target type, but one that will never be returned by
     * {@link #provide() provide()} or {@link #provideArguments(ExtensionContext)
     * provideArguments()}.
     */
    public Object getExtraneousValue() {
        return null;
    }

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
