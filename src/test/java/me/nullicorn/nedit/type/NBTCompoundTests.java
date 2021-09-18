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

    @ParameterizedTest
    @NullSource
    void put_shouldThrowIfValueIsNull(Object tag) {
        NBTCompound compound = new NBTCompound();
        assertThrows(IllegalArgumentException.class,
            () -> compound.put(SAMPLE_NAME, tag)
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

    @ParameterizedTest
    @NullSource
    void putIfAbsent_shouldThrowIfValueIsNull(Object tag) {
        NBTCompound compound = new NBTCompound();
        assertThrows(IllegalArgumentException.class,
            () -> compound.putIfAbsent(SAMPLE_NAME, tag)
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

    @ParameterizedTest
    @NullSource
    void containsKey_throwsIfTagNameIsNull(String tagName) {
        NBTCompound compound = new NBTCompound();
        assertThrows(NullPointerException.class,
            () -> compound.containsKey(tagName)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @ProvideAllAtOnce
    void containsKey_returnsTrueIfCompoundHasTagWithName(Set<Object> tagSet) {
        Map<String, Object> tags = mapToNames(tagSet);

        for (String tagName : tags.keySet()) {
            NBTCompound compound = new NBTCompound();

            // Add all tags to the compound EXCEPT the one with the current tagName.
            tags.forEach((otherName, otherValue) -> {
                if (!otherName.equals(tagName)) {
                    compound.put(otherName, otherValue);
                }
            });

            // Check that the compound doesn't contain the name we purposely omitted.
            assertFalse(compound.containsKey(tagName));

            // Check that the compound *does* contain all other names we included.
            for (String otherName : tags.keySet()) {
                if (!otherName.equals(tagName)) {
                    assertTrue(compound.containsKey(otherName));
                }
            }
        }
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
