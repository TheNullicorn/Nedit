package me.nullicorn.nedit.type;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.nullicorn.nedit.provider.AllTagsProvider;
import me.nullicorn.nedit.provider.annotation.ProvideAllAtOnce;
import me.nullicorn.nedit.provider.annotation.ProvideTagTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullSource;

@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "ResultOfMethodCallIgnored"})
class NBTCompoundTests {

    static final String SAMPLE_NAME = "a_tag";

    @Test
    void constructor_shouldInitialSizeBeZero() {
        assertEquals(0, new NBTCompound().size());
        assertTrue(new NBTCompound().isEmpty());
    }

    // put()

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @ProvideAllAtOnce
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
    @ProvideAllAtOnce
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
    @ProvideAllAtOnce
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
    @ProvideAllAtOnce
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
    @SuppressWarnings("SuspiciousMethodCalls")
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
    @ProvideAllAtOnce
    void get_shouldReturnCorrectValueForName(Set<Object> tagSet) {
        Map<String, Object> tags = mapToNames(tagSet);

        NBTCompound compound = new NBTCompound();
        tags.forEach(compound::put);

        tags.forEach((name, value) -> assertEquals(value, compound.get(name)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @ProvideAllAtOnce
    void get_shouldReturnCorrectValueForNestedTags(Set<Object> tagSet) {
        Map<String, Object> tags = mapToNames(tagSet);

        int maxDepth = 5;
        Map<String, Object> nestedTags = new HashMap<>();
        NBTCompound rootCompound = new NBTCompound();

        /*
         * Generate a series of nested NBT compounds like so:
         * | - >
         * | - - >
         * | - - - >
         * | - - - - >
         * | - - - - - >
         * Where..
         *   - Pipes '|' represent the root compound
         *   - Hyphens '-' represent a nested compound
         *   - Arrows '>' represent an endpoint where the contents of the `tags` map are dumped out.
         *
         * Successful nesting will be tested for at each layer of that generated structure.
         */
        for (int i = 1; i <= maxDepth; i++) {
            // Start at the root (pipe '|').
            NBTCompound lastParent = rootCompound;
            StringBuilder lastParentPath = new StringBuilder();

            for (int depth = 0; depth < i; depth++) {
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
                nestedTags.put(parentPath + name, value);
            });
        }

        nestedTags.forEach((path, value) -> assertEquals(value, rootCompound.get(path)));
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
    @ProvideAllAtOnce
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
    @ProvideAllAtOnce
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
    @ProvideTagTypes
    void containsTag_shouldReturnTrueIfCompoundHasTagWithName(Object tag, TagType type) {
        NBTCompound compound = new NBTCompound();
        assertFalse(compound.containsTag(SAMPLE_NAME, type));

        compound.put(SAMPLE_NAME, tag);
        assertTrue(compound.containsTag(SAMPLE_NAME, type));
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @ProvideTagTypes
    @ProvideAllAtOnce
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
}
