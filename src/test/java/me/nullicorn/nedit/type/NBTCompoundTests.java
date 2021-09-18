package me.nullicorn.nedit.type;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.nullicorn.nedit.provider.AllTagsProvider;
import me.nullicorn.nedit.provider.annotation.ProvideAllAtOnce;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullSource;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
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
    void putIfAbsent_shouldNotReplaceExistingValue(Set<Object> tags) {
        NBTCompound compound = new NBTCompound();
        assertEquals(0, compound.size());

        // Create a map that will mimic the compound's expected state.
        Map<String, Object> expected = new HashMap<>();

        // Set up by populating the map with every tag.
        int i = 1;
        for (Object tag : tags) {
            // Map each tag to the hex value of (hashCode * i), as a placeholder name.
            String tagName = Integer.toHexString(tag.hashCode() * i);

            compound.put(tagName, tag);
            expected.put(tagName, tag);
        }

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
}
