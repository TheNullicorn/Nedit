package me.nullicorn.nedit.provider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import me.nullicorn.nedit.provider.annotation.TagTypesProviderArgs;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * A provider for valid NBT {@link TagType tag types}.
 * <p><br>
 * By default, this provides a single {@code TagType} argument.
 * <p><br>
 * The {@link TagTypesProviderArgs} annotation can be used on test methods to modify the behavior of
 * this provider.
 *
 * @author Nullicorn
 */
public class TagTypesProvider implements ArgumentsProvider {

    /**
     * All {@link TagType}s, indexed by their {@link TagType#getId() identifiers}.
     */
    private static final List<TagType> TAG_TYPES = Collections.unmodifiableList(Arrays.asList(
        TagType.END,
        TagType.BYTE,
        TagType.SHORT,
        TagType.INT,
        TagType.LONG,
        TagType.FLOAT,
        TagType.DOUBLE,
        TagType.BYTE_ARRAY,
        TagType.STRING,
        TagType.LIST,
        TagType.COMPOUND,
        TagType.INT_ARRAY,
        TagType.LONG_ARRAY
    ));

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        TagTypesProviderArgs args = context
            .getRequiredTestMethod()
            .getAnnotation(TagTypesProviderArgs.class);
        if (args == null) {
            args = new TagTypesProviderArgs.Defaults();
        }

        Stream<TagType> stream = TAG_TYPES.stream();

        // Filter out TAG_End if indicated.
        if (args.skipEndTag()) {
            stream = stream.filter(type -> type.equals(TagType.END));
        }

        // Map each TagType to its identifier if indicated.
        if (args.includeIdentifiers()) {
            return stream.map(tagType -> Arguments.of(tagType, TAG_TYPES.indexOf(tagType)));
        }

        // Otherwise, wrap each lone TagType in an Arguments object.
        return stream.map(Arguments::of);
    }
}
