package me.nullicorn.nedit.provider;

import static me.nullicorn.nedit.provider.TagProvider.getProviderForType;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import me.nullicorn.nedit.provider.annotation.AllTagsProviderArgs;
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
 * By default, this provides a single {@code Object} argument representing a valid value for an
 * unspecified type of NBT tag.
 * <p><br>
 * The {@link AllTagsProviderArgs} annotation can be used on test methods to modify the behavior of
 * this provider.
 *
 * @author Nullicorn
 * @see AllTagsProviderArgs
 */
public class AllTagsProvider implements ArgumentsProvider {

    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
        AllTagsProviderArgs args = context
            .getRequiredTestMethod()
            .getAnnotation(AllTagsProviderArgs.class);
        if (args == null) {
            args = new AllTagsProviderArgs.Defaults();
        }

        // Check for conflicting flags.
        if (args.groupAsOne() && args.groupByType()) {
            throw new IllegalStateException("Test cannot use groupAsOne and groupByType together");
        }

        if (args.groupAsOne()) {
            return provideAllAtOnce(args.provideTypes());

        } else if (args.groupByType()) {
            return provideSameTypeAtOnce(args.provideTypes());

        } else {
            return provideSeparately(args.provideTypes());
        }
    }

    private static Stream<Arguments> provideSeparately(boolean doProvideTagTypes) {
        Map<Object, TagType> tagValues = getAllTypedTags();

        Stream.Builder<Arguments> stream = Stream.builder();
        tagValues.forEach((value, type) -> {
            if (doProvideTagTypes) {
                // Provide values and types (Object, TagType).
                stream.accept(arguments(value, type));
            } else {
                // Provide only the values (Object).
                stream.accept(arguments(value));
            }
        });
        return stream.build();
    }

    private static Stream<Arguments> provideAllAtOnce(boolean doProvideTagTypes) {
        Map<Object, TagType> tagValues = getAllTypedTags();

        if (doProvideTagTypes) {
            // Provide values and types (Map<Object, TagType>)
            return Stream.of(arguments(tagValues));
        } else {
            // Return only the values (Set<Object>).
            return Stream.of(arguments(tagValues.keySet()));
        }
    }

    private static Stream<Arguments> provideSameTypeAtOnce(boolean doProvideTagTypes) {
        Stream.Builder<Arguments> stream = Stream.builder();

        for (TagType type : TagType.values()) {
            if (type == TagType.END) {
                // No valid values for end; must be skipped.
                continue;
            }

            Set<Object> values = getTypedTags(type).keySet();

            Arguments args = doProvideTagTypes
                ? Arguments.of(values, type)
                : Arguments.of(values);

            stream.accept(args);
        }

        return stream.build();
    }

    private static Map<Object, TagType> getAllTypedTags() {
        Map<Object, TagType> tags = new HashMap<>();

        for (TagType type : TagType.values()) {
            if (type == TagType.END) {
                continue;
            }
            tags.putAll(getTypedTags(type));
        }

        return tags;
    }

    private static Map<Object, TagType> getTypedTags(TagType type) {
        Map<Object, TagType> tags = new HashMap<>();

        // 1. Get an array of values directly from the provider.
        // 2. Iterate over each value in the array.
        // 3. Add the value & its type to the map.
        Object valueArray = getProviderForType(type).provide();
        reflectiveForEach(valueArray, value -> tags.put(value, type));

        return tags;
    }

    /**
     * Reflectively iterates over an object that is assumed to be an {@code array} of "{@code T}"
     * values.
     *
     * @throws IllegalArgumentException If the {@code array} argument is not actually an array.
     * @throws NullPointerException     If either argument is {@code null}.
     */
    @SuppressWarnings("unchecked")
    private static <T> void reflectiveForEach(Object array, Consumer<T> action) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(action);
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("Not an array: " + array);
        }

        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            action.accept((T) Array.get(array, i));
        }
    }
}
