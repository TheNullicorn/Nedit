package me.nullicorn.nedit.type;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class NBTCompoundTests {

    private Map<TagType, String> testTagNames;
    private Map<String, Object>  testTags;

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
    @Order(1)
    void shouldPutValidTags() {
        NBTCompound compound = new NBTCompound();
        Assertions.assertEquals(0, compound.size());

        Assertions.assertDoesNotThrow(() -> testTags.forEach(compound::put));

        Assertions.assertEquals(testTags.size(), compound.size());
        Assertions.assertFalse(compound.isEmpty());
    }

    @Test
    @Order(2)
    void shouldThrowWhenInvalidTagsArePut() {
        NBTCompound compound = new NBTCompound();
        Object invalidValue = new Object();
        Class<? extends Throwable> expect = IllegalArgumentException.class;

        Assertions.assertThrows(expect, () -> compound.put("tag_with_invalid_value", invalidValue));
        Assertions.assertEquals(0, compound.size());
        Assertions.assertTrue(compound.isEmpty());
    }

    @Test
    @Order(3)
    void shouldGetCorrectTypes() {
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
        checkPrimitiveGetter(compound::getByte, byteTagName, expectedByte, ++expectedByte);
        checkPrimitiveGetter(compound::getShort, shortTagName, expectedShort, ++expectedShort);
        checkPrimitiveGetter(compound::getInt, intTagName, expectedInt, ++expectedInt);
        checkPrimitiveGetter(compound::getLong, longTagName, expectedLong, ++expectedLong);
        checkPrimitiveGetter(compound::getFloat, floatTagName, expectedFloat, ++expectedFloat);
        checkPrimitiveGetter(compound::getDouble, doubleTagName, expectedDouble, ++expectedDouble);
        checkPrimitiveGetter(compound::getString, stringTagName, expectedString, null);

        // Test non-"primitive" getters.
        checkGetter(compound::getByteArray, testTagNames.get(TagType.BYTE_ARRAY));
        checkGetter(compound::getIntArray, testTagNames.get(TagType.INT_ARRAY));
        checkGetter(compound::getLongArray, testTagNames.get(TagType.LONG_ARRAY));
        checkGetter(compound::getList, testTagNames.get(TagType.LIST));
        checkGetter(compound::getCompound, testTagNames.get(TagType.COMPOUND));
    }

    @Test
    @Order(4)
    void shouldGetterAcceptDotNotation() {
        NBTCompound root = new NBTCompound();
        NBTCompound child = new NBTCompound();
        String childName = "child";
        String tagName = "tag";
        String tagValue = "Hello, World!";

        child.put(tagName, tagValue);
        root.put(childName, child);

        Assertions.assertEquals(child, root.get(childName));
        Assertions.assertEquals(tagValue, root.get(childName + "." + tagName));
        System.out.println(root.get(childName + "." + tagName));
    }

    @Test
    @Order(5)
    void shouldGetterReturnNullForNonExistentTags() {
        NBTCompound compound = new NBTCompound();
        Assertions.assertNull(compound.get("i_do_not_exist"));

        String childName = "child";
        String tagName = "tag";
        int tagValue = 7;

        NBTCompound child = new NBTCompound();
        child.put(tagName, tagValue);
        compound.put(childName, child);

        Assertions.assertNull(compound.get(child + ".me_neither"));
        Assertions.assertNull(compound.get(childName + "." + tagName + ".nor_do_i"));
        Assertions.assertNotNull(compound.get(childName + "." + tagName));
    }

    @Test
    @Order(6)
    void shouldContainExpectedTags() {
        NBTCompound compound = new NBTCompound();
        compound.putAll(testTags);

        // Check that a tag is found for each valid type.
        testTagNames.keySet().forEach(tagType -> checkContains(compound, tagType));

        // Check that there aren't false positives for the wrong type.
        testTagNames.values().forEach(
            tagName -> Assertions.assertFalse(compound.containsTag(tagName, TagType.END)));

        // Check that "contains" doesn't promote numbers before checking.
        String byteTagName = testTagNames.get(TagType.BYTE);
        Assertions.assertFalse(compound.containsTag(byteTagName, TagType.INT));

        // Check that non-existent tags are not found.
        Assertions.assertFalse(compound.containsTag("does_not_exist", TagType.STRING));
    }

    /**
     * Asserts that the {@code compound} contains a tag with a certain {@code type}. The name of the
     * tag searched for is provided by the {@link #testTagNames} map.
     */
    private void checkContains(NBTCompound compound, TagType type) {
        String tagName = testTagNames.get(type);
        boolean doesContain = compound.containsTag(tagName, type);

        Assertions.assertTrue(doesContain);
    }

    /**
     * Asserts that a compound's {@code getter} for a primitive tag type returns the expected value
     * when passed the {@code name}. The {@code incorrectValue} is passed into the getter's
     * "default" field to ensure that if the tag is not found, the value will never match the
     * expected one.
     */
    private <T> void checkPrimitiveGetter(BiFunction<String, T, T> getter, String name, T expected, T incorrectValue) {
        Assertions.assertEquals(expected, getter.apply(name, incorrectValue));
    }

    /**
     * Asserts that a compound's {@code getter} for a non-primitive tag type returns the same value
     * that was inputted for the {@code name}. The expected value comes from the {@link #testTags}
     * map.
     */
    private <T> void checkGetter(Function<String, T> getter, String name) {
        //noinspection unchecked
        T expected = (T) testTags.get(name);

        if (expected instanceof byte[]) {
            Assertions.assertArrayEquals((byte[]) expected, (byte[]) getter.apply(name));
        } else if (expected instanceof int[]) {
            Assertions.assertArrayEquals((int[]) expected, (int[]) getter.apply(name));
        } else if (expected instanceof long[]) {
            Assertions.assertArrayEquals((long[]) expected, (long[]) getter.apply(name));
        } else {
            Assertions.assertEquals(expected, getter.apply(name));
        }
    }
}
