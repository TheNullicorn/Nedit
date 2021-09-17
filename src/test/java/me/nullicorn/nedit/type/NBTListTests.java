package me.nullicorn.nedit.type;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class NBTListTests {

    private Map<TagType, Object> testTags;
    private Map<TagType, Object> alternativeTestTags;

    @BeforeEach
    void setUp() {
        testTags = new HashMap<>();
        testTags.put(TagType.BYTE, Byte.MAX_VALUE);
        testTags.put(TagType.SHORT, Short.MAX_VALUE);
        testTags.put(TagType.INT, Integer.MAX_VALUE);
        testTags.put(TagType.LONG, Long.MAX_VALUE);
        testTags.put(TagType.FLOAT, Float.MAX_VALUE);
        testTags.put(TagType.DOUBLE, Double.MAX_VALUE);
        testTags.put(TagType.BYTE_ARRAY, new byte[]{0, 1, 2, 3, 4});
        testTags.put(TagType.STRING, "Hello, World!");
        testTags.put(TagType.LIST, new NBTList(TagType.END));
        testTags.put(TagType.COMPOUND, new NBTCompound());
        testTags.put(TagType.INT_ARRAY, new int[]{0, 1, 2, 3, 4});
        testTags.put(TagType.LONG_ARRAY, new long[]{0, 1, 2, 3, 4});

        NBTCompound alternativeCompound = new NBTCompound();
        alternativeCompound.put("alternative_value", 0);

        alternativeTestTags = new HashMap<>();
        alternativeTestTags.put(TagType.BYTE, Byte.MIN_VALUE);
        alternativeTestTags.put(TagType.SHORT, Short.MIN_VALUE);
        alternativeTestTags.put(TagType.INT, Integer.MIN_VALUE);
        alternativeTestTags.put(TagType.LONG, Long.MIN_VALUE);
        alternativeTestTags.put(TagType.FLOAT, Float.MIN_VALUE);
        alternativeTestTags.put(TagType.DOUBLE, Double.MIN_VALUE);
        alternativeTestTags.put(TagType.BYTE_ARRAY, new byte[]{4, 3, 2, 1, 0});
        alternativeTestTags.put(TagType.STRING, "Goodbye, World!");
        alternativeTestTags.put(TagType.LIST, new NBTList(TagType.BYTE));
        alternativeTestTags.put(TagType.COMPOUND, alternativeCompound);
        alternativeTestTags.put(TagType.INT_ARRAY, new int[]{4, 3, 2, 1, 0});
        alternativeTestTags.put(TagType.LONG_ARRAY, new long[]{4, 3, 2, 1, 0});
    }

    @Test
    void shouldHaveTheCorrectContentType() {
        testTags.forEach((type, tag) -> {
            NBTList list = new NBTList(type);

            assertEquals(type, list.getContentType());
        });
    }

    @Test
    void shouldAddValidTags() {
        testTags.forEach((type, tag) -> {
            NBTList list = new NBTList(type);
            assertEquals(0, list.size());

            assertDoesNotThrow(() -> list.add(tag));

            assertEquals(1, list.size());
            assertFalse(list.isEmpty());
        });
    }

    @Test
    void shouldThrowWhenInvalidTagsAreAdded() {
        NBTList list = new NBTList(TagType.BYTE);
        Object invalidValue = new Object();
        Class<? extends Throwable> expect = IllegalArgumentException.class;

        assertThrows(expect, () -> list.add(invalidValue));
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    void shouldGetCorrectTags() {
        testTags.forEach((type, tag) -> {
            NBTList list = new NBTList(type);
            assertEquals(0, list.size());

            assertDoesNotThrow(() -> list.add(tag));

            assertEquals(1, list.size());
            assertEquals(tag, list.get(0));
            assertFalse(list.isEmpty());
        });
    }

    @Test
    void shouldGetCorrectTypes() {
        checkGetter(list -> list::getByte, TagType.BYTE);
        checkGetter(list -> list::getShort, TagType.SHORT);
        checkGetter(list -> list::getInt, TagType.INT);
        checkGetter(list -> list::getLong, TagType.LONG);
        checkGetter(list -> list::getFloat, TagType.FLOAT);
        checkGetter(list -> list::getDouble, TagType.DOUBLE);
        checkGetter(list -> list::getByteArray, TagType.BYTE_ARRAY);
        checkGetter(list -> list::getString, TagType.STRING);
        checkGetter(list -> list::getList, TagType.LIST);
        checkGetter(list -> list::getCompound, TagType.COMPOUND);
        checkGetter(list -> list::getIntArray, TagType.INT_ARRAY);
        checkGetter(list -> list::getLongArray, TagType.LONG_ARRAY);
    }

    @Test
    void shouldThrowWhenInvalidGetterIsUsed() {
        checkInvalidGetterThrows(list -> list::getByte, TagType.BYTE);
        checkInvalidGetterThrows(list -> list::getShort, TagType.SHORT);
        checkInvalidGetterThrows(list -> list::getInt, TagType.INT);
        checkInvalidGetterThrows(list -> list::getLong, TagType.LONG);
        checkInvalidGetterThrows(list -> list::getFloat, TagType.FLOAT);
        checkInvalidGetterThrows(list -> list::getDouble, TagType.DOUBLE);
        checkInvalidGetterThrows(list -> list::getByteArray, TagType.BYTE_ARRAY);
        checkInvalidGetterThrows(list -> list::getString, TagType.STRING);
        checkInvalidGetterThrows(list -> list::getList, TagType.LIST);
        checkInvalidGetterThrows(list -> list::getCompound, TagType.COMPOUND);
        checkInvalidGetterThrows(list -> list::getIntArray, TagType.INT_ARRAY);
        checkInvalidGetterThrows(list -> list::getLongArray, TagType.LONG_ARRAY);
    }

    @Test
    void shouldAddTagsInCorrectIndex() {
        testTags.forEach((type, tag) -> {
            NBTList list = new NBTList(type);
            assertEquals(0, list.size());

            assertDoesNotThrow(() -> list.add(alternativeTestTags.get(type)));

            assertDoesNotThrow(() -> list.add(0, tag));

            assertEquals(list.get(0), tag);
            assertEquals(list.get(1), alternativeTestTags.get(type));

            assertEquals(2, list.size());
            assertFalse(list.isEmpty());
        });
    }

    @Test
    void shouldAddAllValidTags() {
        testTags.forEach((type, tag) -> {
            NBTList list = new NBTList(type);
            assertEquals(0, list.size());

            assertDoesNotThrow(() -> list.addAll(Arrays.asList(tag, alternativeTestTags.get(type))));

            assertEquals(2, list.size());
            assertEquals(tag, list.get(0));
            assertEquals(alternativeTestTags.get(type), list.get(1));
            assertFalse(list.isEmpty());
        });
    }

    @Test
    void shouldThrowWhenInvalidTagsAreAddAlled() {
        NBTList list = new NBTList(TagType.BYTE);
        Object invalidValue = new Object();
        Class<? extends Throwable> expect = IllegalArgumentException.class;

        assertThrows(expect, () -> list.addAll(Collections.singletonList(invalidValue)));
        assertThrows(expect, () -> list.addAll(Arrays.asList(testTags.get(TagType.BYTE), invalidValue)));
        assertThrows(expect, () -> list.addAll(Arrays.asList(invalidValue, testTags.get(TagType.BYTE))));
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    void shouldSetValidTags() {
        testTags.forEach((type, tag) -> {
            NBTList list = new NBTList(type);
            assertEquals(0, list.size());

            assertDoesNotThrow(() -> list.add(alternativeTestTags.get(type)));

            assertDoesNotThrow(() -> list.set(0, tag));

            assertEquals(1, list.size());
            assertEquals(tag, list.get(0));
            assertFalse(list.isEmpty());
        });
    }

    @Test
    void shouldThrowWhenInvalidTagsAreSet() {
        NBTList list = new NBTList(TagType.BYTE);
        Object invalidValue = new Object();
        Class<? extends Throwable> expect = IllegalArgumentException.class;
        assertDoesNotThrow(() -> list.add(testTags.get(TagType.BYTE)));

        assertThrows(expect, () -> list.set(0, invalidValue));
        assertEquals(testTags.get(TagType.BYTE), list.get(0));
    }

    /**
     * Assert that a list's {@code getter} for a tag type returns the same value
     * that was inputted. The expected value comes from the {@link #testTags}
     * map.
     */
    private <T> void checkGetter(Function<NBTList, Function<Integer, T>> getter, TagType type) {
        Object tag = testTags.get(type);
        NBTList list = new NBTList(type);

        assertDoesNotThrow(() -> list.add(tag));

        T returned = getter.apply(list).apply(0);

        assertEquals(tag, returned);
        assertEquals(tag.getClass(), returned.getClass());
    }

    /**
     * Assert that a list's {@code getter} for a tag type throws an exception
     * when it is used for a list of a different tag type
     */
    private <T> void checkInvalidGetterThrows(Function<NBTList, Function<Integer, T>> getter, TagType type) {
        testTags.forEach((differentType, tag) -> {
            // Only run on different types
            if (differentType != type) {
                NBTList list = new NBTList(differentType);
                Class<? extends Throwable> expect = IllegalStateException.class;

                assertDoesNotThrow(() -> list.add(tag));

                assertThrows(expect, () -> getter.apply(list).apply(0));
            }
        });
    }

}
