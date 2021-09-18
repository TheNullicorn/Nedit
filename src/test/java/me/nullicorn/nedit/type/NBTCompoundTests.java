package me.nullicorn.nedit.type;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.nullicorn.nedit.provider.AllTagsProvider;
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
    void put_shouldNotThrowIfValueCanBeNBT(Object tag) {
        NBTCompound compound = new NBTCompound();
        assertDoesNotThrow(() -> compound.put(SAMPLE_NAME, tag));
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
    void put_shouldIncreaseSizeByOne(Object tag) {
        NBTCompound compound = new NBTCompound();
        assertEquals(0, compound.size());

        compound.put(SAMPLE_NAME, tag);
        assertEquals(1, compound.size());
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    void put_shouldReplaceExistingValue(Object tag) {
        NBTCompound compound = new NBTCompound();
        assertEquals(0, compound.size());

        compound.put(SAMPLE_NAME, tag);
        assertEquals(1, compound.size());
        assertEquals(tag, compound.get(SAMPLE_NAME));

        String replacement = "ya dun got replaced";
        compound.put(SAMPLE_NAME, replacement);
        assertEquals(1, compound.size());
        assertEquals(replacement, compound.get(SAMPLE_NAME));
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
    void putIfAbsent_shouldNotReplaceExistingValue(Object tag) {
        NBTCompound compound = new NBTCompound();
        assertEquals(0, compound.size());

        compound.putIfAbsent(SAMPLE_NAME, tag);
        assertEquals(1, compound.size());
        assertEquals(tag, compound.get(SAMPLE_NAME));

        String replacement = "not replaced (hopefully)";
        compound.putIfAbsent(SAMPLE_NAME, replacement);
        assertEquals(1, compound.size());
        assertEquals(tag, compound.get(SAMPLE_NAME));
    }
}
