package me.nullicorn.nedit.type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class NBTCompoundTests {

    private Map<TagType, String> testTagNames;
    private Map<String, Object> testTags;

    @BeforeEach
    void setUp() {
        testTagNames = new HashMap<>();
        testTagNames.put(TagType.BYTE, "byte");
        testTagNames.put(TagType.SHORT, "short");
        testTagNames.put(TagType.INT, "int");
        testTagNames.put(TagType.LONG, "long");
        testTagNames.put(TagType.FLOAT, "float");
        testTagNames.put(TagType.DOUBLE, "double");
        testTagNames.put(TagType.BYTE_ARRAY, "byte_array");
        testTagNames.put(TagType.STRING, "string");
        testTagNames.put(TagType.LIST, "list");
        testTagNames.put(TagType.COMPOUND, "compound");
        testTagNames.put(TagType.INT_ARRAY, "int_array");
        testTagNames.put(TagType.LONG_ARRAY, "long_array");

        testTags = new HashMap<>();
        testTags.put(testTagNames.get(TagType.BYTE), Byte.MAX_VALUE);
        testTags.put(testTagNames.get(TagType.SHORT), Short.MAX_VALUE);
        testTags.put(testTagNames.get(TagType.INT), Integer.MAX_VALUE);
        testTags.put(testTagNames.get(TagType.LONG), Long.MAX_VALUE);
        testTags.put(testTagNames.get(TagType.FLOAT), Float.MAX_VALUE);
        testTags.put(testTagNames.get(TagType.DOUBLE), Double.MAX_VALUE);
        testTags.put(testTagNames.get(TagType.BYTE_ARRAY), new byte[]{0, 1, 2, 3, 4});
        testTags.put(testTagNames.get(TagType.STRING), "Hello, World!");
        testTags.put(testTagNames.get(TagType.LIST), new NBTList(TagType.END));
        testTags.put(testTagNames.get(TagType.COMPOUND), new NBTCompound());
        testTags.put(testTagNames.get(TagType.INT_ARRAY), new int[]{0, 1, 2, 3, 4});
        testTags.put(testTagNames.get(TagType.LONG_ARRAY), new long[]{0, 1, 2, 3, 4});
    }

    @Test
    void get_shouldDotNotationGetNestedTags() {
        NBTCompound root = new NBTCompound();
        NBTCompound child = new NBTCompound();
        String childName = "child";
        String tagName = "tag";
        String tagValue = "Hello, World!";

        child.put(tagName, tagValue);
        root.put(childName, child);

        assertEquals(child, root.get(childName));
        assertEquals(tagValue, root.get(childName + "." + tagName));
    }

    @Test
    void get_shouldReturnNullForNonExistentTags() {
        NBTCompound compound = new NBTCompound();
        assertNull(compound.get("i_do_not_exist"));

        String childName = "child";
        String tagName = "tag";
        int tagValue = 7;

        NBTCompound child = new NBTCompound();
        child.put(tagName, tagValue);
        compound.put(childName, child);

        assertNull(compound.get(childName + ".me_neither"));
        assertNull(compound.get(childName + "." + tagName + ".nor_do_i"));
    }

    @Test
    void contains_shouldOnlyReturnTrueForAddedTags() {
        NBTCompound compound = new NBTCompound();
        compound.putAll(testTags);

        // Check that a tag is found for each valid type.
        testTagNames.keySet()
            .forEach(tagType -> assertCompoundContainsTagOfType(compound, tagType));

        // Check that there aren't false positives for the wrong type.
        testTagNames.values().forEach(
            tagName -> assertFalse(compound.containsTag(tagName, TagType.END)));

        // Check that "contains" doesn't promote numbers before checking.
        String byteTagName = testTagNames.get(TagType.BYTE);
        assertFalse(compound.containsTag(byteTagName, TagType.INT));

        // Check that non-existent tags are not found.
        assertFalse(compound.containsTag("does_not_exist", TagType.STRING));
    }

    @Test
    void put_shouldAcceptValidValues() {
        NBTCompound compound = new NBTCompound();
        assertEquals(0, compound.size());

        assertDoesNotThrow(() -> testTags.forEach(compound::put));

        assertEquals(testTags.size(), compound.size());
        assertFalse(compound.isEmpty());
    }

    @Test
    void put_shouldThrowIfTagsAreInvalid() {
        NBTCompound compound = new NBTCompound();
        Object invalidValue = new Object();
        Class<? extends Throwable> expect = IllegalArgumentException.class;

        assertThrows(expect, () -> compound.put("tag_with_invalid_value", invalidValue));
        assertEquals(0, compound.size());
        assertTrue(compound.isEmpty());
    }

    @Test
    void putAll_shouldAddAllNamesAndValues() {
        NBTCompound compound = new NBTCompound();
        compound.putAll(testTags);

        // Test "primitive" NBT getters (methods that have default values).
        String byteTagName = testTagNames.get(TagType.BYTE);
        String shortTagName = testTagNames.get(TagType.SHORT);
        String intTagName = testTagNames.get(TagType.INT);
        String longTagName = testTagNames.get(TagType.LONG);
        String floatTagName = testTagNames.get(TagType.FLOAT);
        String doubleTagName = testTagNames.get(TagType.DOUBLE);
        String stringTagName = testTagNames.get(TagType.STRING);

        byte expectedByte = (byte) testTags.get(testTagNames.get(TagType.BYTE));
        short expectedShort = (short) testTags.get(testTagNames.get(TagType.SHORT));
        int expectedInt = (int) testTags.get(testTagNames.get(TagType.INT));
        long expectedLong = (long) testTags.get(testTagNames.get(TagType.LONG));
        float expectedFloat = (float) testTags.get(testTagNames.get(TagType.FLOAT));
        double expectedDouble = (double) testTags.get(testTagNames.get(TagType.DOUBLE));
        String expectedString = (String) testTags.get(testTagNames.get(TagType.STRING));

        // "Invalid" value for each is the expected value + 1, so they will never be the same.
        assertPrimitiveGetterReturnsValue(compound::getByte, byteTagName, expectedByte,
            ++expectedByte);
        assertPrimitiveGetterReturnsValue(compound::getShort, shortTagName, expectedShort,
            ++expectedShort);
        assertPrimitiveGetterReturnsValue(compound::getInt, intTagName, expectedInt, ++expectedInt);
        assertPrimitiveGetterReturnsValue(compound::getLong, longTagName, expectedLong,
            ++expectedLong);
        assertPrimitiveGetterReturnsValue(compound::getFloat, floatTagName, expectedFloat,
            ++expectedFloat);
        assertPrimitiveGetterReturnsValue(compound::getDouble, doubleTagName, expectedDouble,
            ++expectedDouble);
        assertPrimitiveGetterReturnsValue(compound::getString, stringTagName, expectedString, null);

        // Test non-"primitive" getters.
        assertGetterReturnsValue(compound::getByteArray, testTagNames.get(TagType.BYTE_ARRAY));
        assertGetterReturnsValue(compound::getIntArray, testTagNames.get(TagType.INT_ARRAY));
        assertGetterReturnsValue(compound::getLongArray, testTagNames.get(TagType.LONG_ARRAY));
        assertGetterReturnsValue(compound::getList, testTagNames.get(TagType.LIST));
        assertGetterReturnsValue(compound::getCompound, testTagNames.get(TagType.COMPOUND));
    }

    /**
     * Asserts that the {@code compound} contains a tag with a certain {@code type}. The name of the
     * tag searched for is provided by the {@link #testTagNames} map.
     */
    private void assertCompoundContainsTagOfType(NBTCompound compound, TagType type) {
        String tagName = testTagNames.get(type);
        boolean doesContain = compound.containsTag(tagName, type);

        assertTrue(doesContain);
    }

    /**
     * Asserts that a compound's {@code getter} for a primitive tag type returns the expected value
     * when passed the {@code name}. The {@code incorrectValue} is passed into the getter's
     * "default" field to ensure that if the tag is not found, the value will never match the
     * expected one.
     */
    private <T> void assertPrimitiveGetterReturnsValue(BiFunction<String, T, T> getter, String name, T expected, T incorrectValue) {
        assertEquals(expected, getter.apply(name, incorrectValue));
    }

    /**
     * Asserts that a compound's {@code getter} for a non-primitive tag type returns the same value
     * that was inputted for the {@code name}. The expected value comes from the {@link #testTags}
     * map.
     */
    private <T> void assertGetterReturnsValue(Function<String, T> getter, String name) {
        //noinspection unchecked
        T expected = (T) testTags.get(name);

        if (expected instanceof byte[]) {
            assertArrayEquals((byte[]) expected, (byte[]) getter.apply(name));
        } else if (expected instanceof int[]) {
            assertArrayEquals((int[]) expected, (int[]) getter.apply(name));
        } else if (expected instanceof long[]) {
            assertArrayEquals((long[]) expected, (long[]) getter.apply(name));
        } else {
            assertEquals(expected, getter.apply(name));
        }
    }
}
