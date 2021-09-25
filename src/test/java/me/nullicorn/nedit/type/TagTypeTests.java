package me.nullicorn.nedit.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Stream;
import me.nullicorn.nedit.provider.AllTagsProvider;
import me.nullicorn.nedit.provider.annotation.ProvideTagTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class TagTypeTests {

    private static TagType[] allTagTypes;

    @BeforeAll
    static void beforeAll() {
        allTagTypes = new TagType[]{
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
        };
    }

    @ParameterizedTest
    @MethodSource("provider_tagTypesAndTheirIds")
    void getId_shouldReturnCorrectId(TagType type, int typeId) {
        assertEquals(typeId, type.getId());
    }

    @ParameterizedTest
    @MethodSource("provider_tagTypesAndTheirIds")
    void fromId_shouldReturnCorrectTypeIfIdExists(TagType type, int typeId) {
        assertEquals(type, TagType.fromId(typeId));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -8, -32, -64, -128, -256, 32, 64, 128, 256, 512})
    void fromId_shouldReturnNullIfIdDoesNotExist(int typeIdThatDoesNotExist) {
        assertNull(TagType.fromId(typeIdThatDoesNotExist));
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @ProvideTagTypes
    void fromObject_shouldReturnCorrectTypeForValidObjects(Object tagValue, TagType tagType) {
        assertEquals(tagType, TagType.fromObject(tagValue));
    }

    @ParameterizedTest
    @NullSource
    void fromObject_shouldReturn_END_ifObjectIsNull(Object tagValueThatIsNull) {
        assertEquals(TagType.END, TagType.fromObject(tagValueThatIsNull));
    }

    @ParameterizedTest
    @MethodSource("provider_invalidTagValues")
    void fromObject_shouldThrowIfObjectCannotBeATagValue(Object tagValueThatIsInvalid) {
        assertThrows(IllegalArgumentException.class,
            () -> TagType.fromObject(tagValueThatIsInvalid)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @ProvideTagTypes
    void getRuntimeType_shouldReturnCorrectClassForType(Object tagValue, TagType tagType) {
        assertEquals(tagValue.getClass(), tagType.getRuntimeType());
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @ProvideTagTypes
    void getClazz_shouldBehaveTheSameAs_getRuntimeType(Object tagValue, TagType tagType) {
        // noinspection deprecation
        assertEquals(tagType.getRuntimeType(), tagType.getClazz());
    }

    /**
     * Provides two arguments:
     * <ol>
     *     <li>{@code TagType type} - Any type of NBT tag</li>
     *     <li>{@code int typeId} - The expected integer ID of the first argument</li>
     * </ol>
     */
    private static Stream<Arguments> provider_tagTypesAndTheirIds() {
        Stream.Builder<Arguments> stream = Stream.builder();
        for (int i = 0; i < allTagTypes.length; i++) {
            stream.accept(Arguments.of(allTagTypes[i], i));
        }
        return stream.build();
    }

    /**
     * Provides a single argument:
     * <ol>
     *     <li>{@code Object tagValueThatIsNotValid} - An object that, when passed to
     *     {@link TagType#fromObject(Object) fromObject()}, should cause an {@link
     *     IllegalArgumentException IAE} to be thrown.</li>
     * </ol>
     */
    private static Object[] provider_invalidTagValues() {
        return new Object[]{
            new Object(),
            new BigInteger("987654321"),
            new BigDecimal("987654321.123456789"),
            new StringBuilder(),
            TagType.BYTE
        };
    }
}
