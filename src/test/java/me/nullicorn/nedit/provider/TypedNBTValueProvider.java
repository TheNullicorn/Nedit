package me.nullicorn.nedit.provider;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import me.nullicorn.nedit.provider.tag.ByteArrayProvider;
import me.nullicorn.nedit.provider.tag.ByteProvider;
import me.nullicorn.nedit.provider.tag.CompoundProvider;
import me.nullicorn.nedit.provider.tag.IntArrayProvider;
import me.nullicorn.nedit.provider.tag.IntProvider;
import me.nullicorn.nedit.provider.tag.ListProvider;
import me.nullicorn.nedit.provider.tag.LongArrayProvider;
import me.nullicorn.nedit.provider.tag.LongProvider;
import me.nullicorn.nedit.provider.tag.ShortProvider;
import me.nullicorn.nedit.provider.tag.StringProvider;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * Provides an acceptable NBT value (first argument), as well as the {@link TagType} of that value
 * (second argument).
 */
public final class TypedNBTValueProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Stream.Builder<Arguments> stream = Stream.builder();

        for (TagType type : TagType.values()) {
            if (type == TagType.END) {
                continue;
            }

            // Combine each of the provider's values with its TagType.
            getProviderForType(type).provideArguments(context)
                .map(args -> {
                    // There should only be 1 value per argument here, but we'll check to be safe.
                    Object[] values = args.get();
                    if (values.length != 1) {
                        throw new IllegalArgumentException("Expected 1 argument for type " + type);
                    }
                    return values[0];
                })
                .forEach(value -> stream.accept(arguments(value, type)));
        }

        return stream.build();
    }

    /**
     * Determines the corresponding argument provider that should be used to provide NBT values of
     * the supplied {@code type}.
     */
    public static NBTValueProvider getProviderForType(TagType type) {
        switch (type) {
            case BYTE:
                return new ByteProvider();
            case SHORT:
                return new ShortProvider();
            case INT:
                return new IntProvider();
            case LONG:
                return new LongProvider();
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
}
