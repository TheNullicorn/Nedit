package me.nullicorn.nedit.provider;

import java.lang.reflect.Array;
import java.util.function.Consumer;
import java.util.stream.Stream;
import me.nullicorn.nedit.provider.annotation.IncludeTagTypes;
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
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * A provider for valid NBT values (those that can be stored in an {@link NBTList} or {@link
 * NBTCompound}).
 * <p><br>
 * If a test using this annotation is also annotated with {@link IncludeTagTypes @IncludeTagTypes},
 * then a second argument will also be provided to indicate the {@link TagType} of the first
 * argument.
 *
 * @author Nullicorn
 * @see IncludeTagTypes @IncludeTagTypes
 */
public class AllTagsProvider implements ArgumentsProvider {

    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
        // Determine whether or not to include a second TagType argument.
        boolean includeTypes = context
            .getRequiredTestMethod()
            .isAnnotationPresent(IncludeTagTypes.class);

        // Returned at the end.
        Stream.Builder<Arguments> aggregate = Stream.builder();

        // Include arguments for each tag type.
        for (TagType type : TagType.values()) {
            if (type == TagType.END) {
                continue;
            }

            TagProvider provider = getProviderForTag(type);
            if (includeTypes) {
                // 1. Get an array of values directly from the provider (not Arguments objects).
                // 2. Iterate over that array of values.
                // 3. Combine each value with the (outer loop's) TagType in an Arguments object.
                // 4. Feed that Arguments object to the builder.
                reflectiveForEach(provider.provide(),
                    value -> aggregate.accept(Arguments.of(value, type))
                );
            } else {
                // Directly pipe each Arguments object into the builder.
                provider.provideArguments(context).forEach(aggregate);
            }
        }

        return aggregate.build();
    }

    /**
     * Determines the corresponding argument provider that should be used to provide NBT values of
     * the supplied {@code type}.
     */
    public static TagProvider getProviderForTag(TagType type) {
        switch (type) {
            case BYTE:
                return new ByteProvider();
            case SHORT:
                return new ShortProvider();
            case INT:
                return new IntProvider();
            case LONG:
                return new LongProvider();
            case FLOAT:
                return new FloatProvider();
            case DOUBLE:
                return new DoubleProvider();
            case BYTE_ARRAY:
                return new ByteArrayProvider();
            case INT_ARRAY:
                return new IntArrayProvider();
            case LONG_ARRAY:
                return new LongArrayProvider();
            case STRING:
                return new StringProvider();
            case LIST:
                return new ListProvider();
            case COMPOUND:
                return new CompoundProvider();
            default:
                throw new IllegalArgumentException("Unable to find provider for tag: " + type);
        }
    }

    /**
     * Reflectively iterates over an object that is assumed to be an {@code array} of "{@code T}"
     * values.
     *
     * @throws IllegalArgumentException if either argument is {@code null}, or if the first argument
     *                                  is not an {@link Class#isArray() array}.
     */
    @SuppressWarnings("unchecked")
    private static <T> void reflectiveForEach(Object array, Consumer<T> action) {
        if (array == null || !array.getClass().isArray()) {
            throw new IllegalArgumentException("Not an array: " + array);
        } else if (action == null) {
            throw new IllegalArgumentException("Iteration action cannot be null");
        }

        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            action.accept((T) Array.get(array, i));
        }
    }
}
