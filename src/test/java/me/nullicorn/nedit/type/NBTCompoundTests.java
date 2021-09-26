package me.nullicorn.nedit.type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.nullicorn.nedit.provider.AllTagsProvider;
import me.nullicorn.nedit.provider.TagProvider;
import me.nullicorn.nedit.provider.annotation.AllTagsProviderArgs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullSource;

@SuppressWarnings({
    "rawtypes",
    "unchecked",
    "SuspiciousMethodCalls",
    "ResultOfMethodCallIgnored",
    "MismatchedQueryAndUpdateOfCollection"
})
class NBTCompoundTests {

    static final String SAMPLE_NAME = "a_tag";

    static Set<TagType> numericTypes;
    static Map<TagType, Getter<?>> getters;
    static Map<TagType, GetterWithDefault<?>> gettersWithDefaults;

    @BeforeAll
    static void beforeAll() {
        getters = new HashMap<>();
        gettersWithDefaults = new HashMap<>();

        getters.put(TagType.STRING, NBTCompound::getString);
        getters.put(TagType.LIST, NBTCompound::getList);
        getters.put(TagType.COMPOUND, NBTCompound::getCompound);
        getters.put(TagType.BYTE_ARRAY, NBTCompound::getByteArray);
        getters.put(TagType.INT_ARRAY, NBTCompound::getIntArray);
        getters.put(TagType.LONG_ARRAY, NBTCompound::getLongArray);

        gettersWithDefaults.put(TagType.BYTE, (GetterWithDefault<Byte>) NBTCompound::getByte);
        gettersWithDefaults.put(TagType.SHORT, (GetterWithDefault<Short>) NBTCompound::getShort);
        gettersWithDefaults.put(TagType.INT, (GetterWithDefault<Integer>) NBTCompound::getInt);
        gettersWithDefaults.put(TagType.LONG, (GetterWithDefault<Long>) NBTCompound::getLong);
        gettersWithDefaults.put(TagType.FLOAT, (GetterWithDefault<Float>) NBTCompound::getFloat);
        gettersWithDefaults.put(TagType.DOUBLE, (GetterWithDefault<Double>) NBTCompound::getDouble);
        gettersWithDefaults.put(TagType.STRING, (GetterWithDefault<String>) NBTCompound::getString);

        numericTypes = new HashSet<>(Arrays.asList(
            TagType.BYTE,
            TagType.SHORT,
            TagType.INT,
            TagType.LONG,
            TagType.FLOAT,
            TagType.DOUBLE
        ));
    }

    @Test
    void constructor_shouldInitialSizeBeZero() {
        assertEquals(0, new NBTCompound().size());
        assertTrue(new NBTCompound().isEmpty());
    }

    // put()

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true)
    void put_shouldNotThrowIfValueCanBeNBT(Set<Object> tags) {
        NBTCompound compound = new NBTCompound();
        for (Object tag : tags) {
            assertDoesNotThrow(() -> compound.put(SAMPLE_NAME, tag));
        }
    }

    // TODO: 9/24/21 Test that exception IS thrown if value CANNOT be NBT.

    @ParameterizedTest
    @NullSource
    void put_shouldThrowIfValueIsNull(Object tag) {
        NBTCompound compound = new NBTCompound();

        // "value" argument is null.
        assertThrows(NullPointerException.class,
            () -> compound.put(SAMPLE_NAME, tag)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    void put_shouldThrowIfNameIsNull(Object tag) {
        NBTCompound compound = new NBTCompound();

        // "name" argument is null.
        assertThrows(NullPointerException.class,
            () -> compound.put(null, tag)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true)
    void put_shouldIncreaseSizeByOne(Set<Object> tags) {
        NBTCompound compound = new NBTCompound();

        int expectedSize = compound.size();
        for (Object tag : tags) {
            expectedSize++;

            String name = "tag_" + expectedSize;
            compound.put(name, tag);

            assertEquals(expectedSize, compound.size());
            assertFalse(compound.isEmpty());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true)
    void put_shouldReplaceExistingValue(Set<Object> tags) {
        NBTCompound compound = new NBTCompound();

        Object prev = null;
        for (Object tag : tags) {
            // Perform the replacement.
            Object replaced = compound.put(SAMPLE_NAME, tag);

            if (prev == null) {
                // Nothing should be replaced on the first iteration.
                assertNull(replaced);
            } else {
                // The return value should be the previous value.
                assertEquals(prev, replaced);
            }

            // The get() value should reflect the replacement.
            assertEquals(tag, compound.get(SAMPLE_NAME));
            prev = tag;
        }
    }

    // putIfAbsent()

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    void putIfAbsent_shouldNotThrowIfValueCanBeNBT(Object tag) {
        NBTCompound compound = new NBTCompound();
        assertDoesNotThrow(() -> compound.putIfAbsent(SAMPLE_NAME, tag));
    }

    // TODO: 9/24/21 Test that exception IS thrown if value CANNOT be NBT.

    @ParameterizedTest
    @NullSource
    void putIfAbsent_shouldThrowIfValueIsNull(Object tag) {
        NBTCompound compound = new NBTCompound();

        // "value" argument is null.
        assertThrows(NullPointerException.class,
            () -> compound.putIfAbsent(SAMPLE_NAME, tag)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    void putIfAbsent_shouldThrowIfNameIsNull(Object tag) {
        NBTCompound compound = new NBTCompound();

        // "name" argument is null.
        assertThrows(NullPointerException.class,
            () -> compound.putIfAbsent(null, tag)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    void putIfAbsent_shouldIncreaseSizeByOne(Object tag) {
        NBTCompound compound = new NBTCompound();
        assertEquals(0, compound.size());

        compound.putIfAbsent(SAMPLE_NAME, tag);
        assertEquals(1, compound.size());
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true)
    void putIfAbsent_shouldNotReplaceExistingValue(Set<Object> tagSet) {
        Map<String, Object> expected = mapToNames(tagSet);

        // Use regular put() initially to populate the compound.
        NBTCompound compound = new NBTCompound();
        expected.forEach(compound::put);

        // Loop over each tag that's in the compound.
        for (String tagName : expected.keySet()) {
            Object expectedValue = expected.get(tagName);

            // Try replacing the tag with each other value.
            for (Object otherValue : expected.values()) {
                // Don't bother replacing the value with itself.
                if (otherValue == expectedValue) {
                    continue;
                }

                Object actualValue = compound.putIfAbsent(tagName, otherValue);
                // Make sure the return value is the initial value.
                assertEquals(expectedValue, actualValue);
                // Make sure get() hasn't changed either for the tag.
                assertEquals(expectedValue, compound.get(tagName));
            }
        }
    }

    // get()

    @ParameterizedTest
    @NullSource
    void get_shouldThrowIfNameIsNull(String tagNameNull) {
        NBTCompound compound = new NBTCompound();

        assertThrows(NullPointerException.class,
            () -> compound.get(tagNameNull)
        );
    }

    @Test
    void get_shouldThrowIfNameIsNotAString() {
        // Non-string tag name. Should cause an exception.
        Object tagNameBad = new Object();

        NBTCompound compound = new NBTCompound();
        assertThrows(ClassCastException.class,
            () -> compound.get(tagNameBad)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true)
    void get_shouldReturnCorrectValueWithOrWithoutDotNotation(Set<Object> tagSet) {
        int maxDepth = 5;
        NBTCompound root = new NBTCompound();
        Map<String, Object> tagsByName = mapToNames(tagSet);
        Map<String, Object> tagsByPath = nestRecursively(root, tagsByName, maxDepth);

        tagsByPath.forEach(
            (path, value) -> assertEquals(value, root.get(path))
        );
    }

    // type-specific getters [getByte(), getString(), getCompound(), ...]

    @ParameterizedTest
    @NullSource
    void getter_shouldThrowIfTagNameIsNull(String tagNameNull) {
        NBTCompound compound = new NBTCompound();

        for (TagType type : TagType.values()) {
            Getter getter = getters.get(type);
            GetterWithDefault getterWithDefault = gettersWithDefaults.get(type);

            // If the type uses a regular getter, make sure it throws NPE for null tag names.
            if (getter != null) {
                assertThrows(NullPointerException.class,
                    () -> getter.get(compound, tagNameNull),
                    "Getter for type " + type + " did not throw NPE"
                );
            }

            // If the also/only has a getter w/ default value, make sure it also throws NPE for null
            // tag names.
            if (getterWithDefault != null) {
                Object defaultValue = getValueWithType(type);
                assertThrows(NullPointerException.class,
                    () -> getterWithDefault.get(compound, tagNameNull, defaultValue),
                    "Getter w/ default for type " + type + " did not throw NPE"
                );
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true, provideTypes = true)
    void getter_shouldReturnCorrectValueForName(Map<Object, TagType> typedTags) {
        Map<String, Object> namedTags = mapToNames(typedTags.keySet());

        NBTCompound compound = new NBTCompound();
        namedTags.forEach(compound::put);

        namedTags.forEach((name, value) -> {
            TagType type = typedTags.get(value);
            Getter getter = getters.get(type);
            GetterWithDefault getterWithDefault = gettersWithDefaults.get(type);

            if (getter != null) {
                assertEquals(
                    value,
                    getter.get(compound, name),
                    "Getter for type " + type + " failed to return correct value"
                );
            }

            if (getterWithDefault != null) {
                Object defaultValue = getValueWithType(type);
                assertEquals(
                    value,
                    getterWithDefault.get(compound, name, defaultValue),
                    "Getter for type " + type + " failed to return correct value"
                );
            }
        });
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true, provideTypes = true)
    void getter_shouldReturnDefaultValueIfNotPresent() {
        NBTCompound compound = new NBTCompound();

        for (TagType type : TagType.values()) {
            GetterWithDefault getterWithDefault = gettersWithDefaults.get(type);

            if (getterWithDefault != null) {
                Object defaultValue = getValueWithType(type);

                assertEquals(
                    defaultValue,
                    getterWithDefault.get(compound, SAMPLE_NAME, defaultValue),
                    "Getter for type " + type + " did not return default value: " + defaultValue
                );
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true, provideTypes = true)
    void getter_shouldReturnCorrectValueWithOrWithoutDotNotation(Map<Object, TagType> typedTags) {
        int maxDepth = 5;
        NBTCompound root = new NBTCompound();
        Map<String, Object> tagsByName = mapToNames(typedTags.keySet());
        Map<String, Object> tagsByPath = nestRecursively(root, tagsByName, maxDepth);

        tagsByPath.forEach((path, value) -> {
            TagType type = typedTags.get(value);
            Getter getter = getters.get(type);
            GetterWithDefault getterWithDefault = gettersWithDefaults.get(type);

            if (getter != null) {
                assertEquals(value, getter.get(root, path));
            }

            if (getterWithDefault != null) {
                Object defaultValue = getValueWithType(type);
                assertEquals(value, getterWithDefault.get(root, path, defaultValue));
            }
        });
    }

    @Test
    void getter_shouldNotThrowIfDefaultValueIsNull() {
        NBTCompound compound = new NBTCompound();

        // getString()
        assertDoesNotThrow(
            () -> compound.getString(SAMPLE_NAME, null),
            "getString() threw an exception with null as a default value"
        );
        assertNull(
            compound.getString(SAMPLE_NAME, null),
            "getString() did not return null even though it was the default value"
        );

        // getNumber()
        assertDoesNotThrow(
            () -> compound.getNumber(SAMPLE_NAME, null),
            "getNumber() threw an exception with null as a default value"
        );
        assertNull(
            compound.getNumber(SAMPLE_NAME, null),
            "getNumber() did not return null even though it was the default value"
        );
    }

    // containsKey()

    @ParameterizedTest
    @NullSource
    void containsKey_shouldThrowIfTagNameIsNull(String tagName) {
        NBTCompound compound = new NBTCompound();
        assertThrows(NullPointerException.class,
            () -> compound.containsKey(tagName)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true)
    void containsKey_shouldReturnTrueIfCompoundHasTagWithName(Set<Object> tagSet) {
        Map<String, Object> tags = mapToNames(tagSet);

        for (String omittedName : tags.keySet()) {
            NBTCompound compound = new NBTCompound();

            // Add all tags to the compound EXCEPT the one with the omittedName.
            tags.forEach((otherName, otherValue) -> {
                if (!otherName.equals(omittedName)) {
                    compound.put(otherName, otherValue);
                }
            });

            // Check that the compound doesn't contain the name we purposely omitted.
            assertFalse(compound.containsKey(omittedName));

            // Check that the compound *does* contain all other names we included.
            for (String otherName : tags.keySet()) {
                if (!otherName.equals(omittedName)) {
                    assertTrue(compound.containsKey(otherName));
                }
            }
        }
    }

    // containsValue()

    @ParameterizedTest
    @NullSource
    void containsValue_shouldThrowIfTagNameIsNull(Object tag) {
        NBTCompound compound = new NBTCompound();
        assertThrows(NullPointerException.class,
            () -> compound.containsValue(null)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true)
    void containsValue_shouldReturnTrueIfCompoundHasTagWithValue(Set<Object> tagSet) {
        Map<String, Object> tags = mapToNames(tagSet);

        for (Object omittedValue : tags.values()) {
            NBTCompound compound = new NBTCompound();

            // Add all tags to the compound EXCEPT the omittedValue.
            tags.forEach((otherName, otherValue) -> {
                if (otherValue != omittedValue) {
                    compound.put(otherName, otherValue);
                }
            });

            // Check that the compound doesn't contain the value we purposely omitted.
            assertFalse(compound.containsValue(omittedValue));

            // Check that the compound *does* contain all other values we included.
            for (Object otherValue : tags.values()) {
                if (otherValue != omittedValue) {
                    assertTrue(compound.containsValue(otherValue));
                }
            }
        }
    }

    // containsTag()

    @ParameterizedTest
    @NullSource
    void containsTag_shouldThrowIfTagNameIsNull(String tagName) {
        NBTCompound compound = new NBTCompound();
        for (TagType tagType : TagType.values()) {
            assertThrows(NullPointerException.class,
                () -> compound.containsTag(tagName, tagType)
            );
        }
    }

    @ParameterizedTest
    @NullSource
    void containsTag_shouldThrowIfTagTypeIsNull(TagType tagType) {
        NBTCompound compound = new NBTCompound();
        assertThrows(NullPointerException.class,
            () -> compound.containsTag(SAMPLE_NAME, tagType)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(provideTypes = true)
    void containsTag_shouldReturnTrueIfCompoundHasTagWithName(Object tag, TagType type) {
        NBTCompound compound = new NBTCompound();
        assertFalse(compound.containsTag(SAMPLE_NAME, type));

        compound.put(SAMPLE_NAME, tag);
        assertTrue(compound.containsTag(SAMPLE_NAME, type));
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true, provideTypes = true)
    void containsTag_shouldReturnTrueIfCompoundHasTagWithType(Map<Object, TagType> typedTags) {
        Map<String, Object> namedTags = mapToNames(typedTags.keySet());

        NBTCompound compound = new NBTCompound();

        // Before we put anything in the compound, make sure false is returned initially.
        namedTags.keySet().forEach(name -> {
            for (TagType type : TagType.values()) {
                assertFalse(compound.containsTag(name, type));
            }
        });

        // Now populate the compound...
        namedTags.forEach(compound::put);

        // And run the check again...
        namedTags.forEach((name, tag) -> {
            TagType type = typedTags.get(tag);

            // Check that true is returned for the correct type.
            assertTrue(compound.containsTag(name, type));

            // Check that false is returned for any other type.
            for (TagType otherType : TagType.values()) {
                if (otherType != type) {
                    assertFalse(compound.containsTag(name, otherType));
                }
            }
        });
    }

    /**
     * Generates unique names for each NBT tag supplied, and puts each value into a new map under
     * its generated name.
     */
    private static Map<String, Object> mapToNames(Iterable<?> tags) {
        Map<String, Object> map = new HashMap<>();

        int i = 1;
        for (Object tag : tags) {
            // Map each tag to the hex value of (hashCode * i), as a placeholder name.
            String tagName = Integer.toHexString(tag.hashCode() * i);
            map.put(tagName, tag);
            i++;
        }

        return map;
    }

    /**
     * Generates a valid value for the supplied type, but one that will never be provided by {@link
     * AllTagsProvider}. Useful for testing methods that accept a default value.
     */
    private static <T> T getValueWithType(TagType type) {
        TagProvider<T> provider = TagProvider.getProviderForType(type);
        return (T) provider.getExtraneousValue();
    }

    /**
     * Generate a series of nested NBT compounds like so:
     * <pre>
     * | >
     * | - >
     * | - - >
     * | - - - >
     * | - - - - >
     * | - - - - - >
     * </pre>
     * Where...
     * <ul>
     *     <li>Each line above represents a separate branch in the root compound</li>
     *     <li>Pipes '|' represent the {@code root} compound itself</li>
     *     <li>Hyphens '-' represent a nested compound</li>
     *     <li>Arrows '>' represent an endpoint where all of the supplied {@code tags} are dumped
     *         out using their existing names</li>
     * </ul>
     * The returned map contains each tag, mapped to the full path string (dot notation) where it
     * was dumped.
     */
    private static Map<String, Object> nestRecursively(NBTCompound root, Map<String, Object> tags, int maxDepth) {
        Map<String, Object> tagsByPath = new HashMap<>();

        for (int i = 0; i <= maxDepth; i++) {
            // Start at the root (pipe '|').
            NBTCompound lastParent = root;
            StringBuilder lastParentPath = new StringBuilder();

            for (int depth = 1; depth <= i; depth++) {
                // Name the child compound after its depth.
                String childName = "child_" + i + "_" + depth;
                lastParentPath.append(childName);

                // Add a child compound to the current parent (hyphen '-').
                // Then make that child the new parent.
                NBTCompound child = new NBTCompound();
                lastParent.put(childName, child);
                lastParent = child;

                // Continue the path...
                lastParentPath.append('.');
            }

            // Dump the `tags` map into the innermost nested child (arrow '>').
            final NBTCompound parent = lastParent;
            final String parentPath = lastParentPath.toString();
            tags.forEach((name, value) -> {
                parent.put(name, value);

                // Store the full path & value to be tested at the end.
                tagsByPath.put(parentPath + name, value);
            });
        }

        return tagsByPath;
    }

    /**
     * Represents a getter method from the {@link NBTCompound} class.
     */
    private interface Getter<T> {

        T get(NBTCompound compound, String name);
    }

    /**
     * Represents a getter method from the {@link NBTCompound} class that also accepts a default
     * value.
     */
    private interface GetterWithDefault<T> {

        T get(NBTCompound compound, String name, T defaultValue);
    }
}
