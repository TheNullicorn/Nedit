package me.nullicorn.nedit.type;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.nullicorn.nedit.provider.AllTagsProvider;
import me.nullicorn.nedit.provider.TagTypesProvider;
import me.nullicorn.nedit.provider.annotation.AllTagsProviderArgs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullSource;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class NBTListTests {

    private static Map<TagType, Getter> getters;

    @BeforeAll
    static void beforeAll() {
        getters = new HashMap<>();
        getters.put(TagType.BYTE, NBTList::getByte);
        getters.put(TagType.SHORT, NBTList::getShort);
        getters.put(TagType.INT, NBTList::getInt);
        getters.put(TagType.LONG, NBTList::getLong);
        getters.put(TagType.FLOAT, NBTList::getFloat);
        getters.put(TagType.DOUBLE, NBTList::getDouble);
        getters.put(TagType.BYTE_ARRAY, NBTList::getByteArray);
        getters.put(TagType.STRING, NBTList::getString);
        getters.put(TagType.LIST, NBTList::getList);
        getters.put(TagType.COMPOUND, NBTList::getCompound);
        getters.put(TagType.INT_ARRAY, NBTList::getIntArray);
        getters.put(TagType.LONG_ARRAY, NBTList::getLongArray);
        getters = Collections.unmodifiableMap(getters);
    }

    @ParameterizedTest
    @ArgumentsSource(TagTypesProvider.class)
    void size_shouldBeZeroInitially(TagType contentType) {
        NBTList list = new NBTList(contentType);
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @ParameterizedTest
    @ArgumentsSource(TagTypesProvider.class)
    void getContentType_shouldMatchConstructorValue(TagType contentType) {
        NBTList list = new NBTList(contentType);
        assertEquals(contentType, list.getContentType());
    }

    @ParameterizedTest
    @NullSource
    void getContentType_shouldBe_END_ifConstructorValueIsNull(TagType contentTypeThatIsNull) {
        NBTList list = new NBTList(contentTypeThatIsNull);
        assertEquals(TagType.END, list.getContentType());
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void add_shouldNotThrowIfValueIsValidForContentType(Set<Object> values, TagType contentType) {
        NBTList list = new NBTList(contentType);

        Iterator<Object> valueIter = values.iterator();
        int i = 0;

        while (valueIter.hasNext()) {
            Object value = valueIter.next();

            assertDoesNotThrow(() -> list.add(value));
            assertEquals(i + 1, list.size(), "Unexpected list size");
            assertFalse(list.isEmpty(), "List should not be considered empty");

            i++;
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true, provideTypes = true)
    void add_shouldThrowIfValueIsInvalidForContentType(Map<Object, TagType> typedTags) {
        for (TagType contentType : TagType.values()) {
            if (contentType == TagType.END) {
                continue;
            }

            NBTList list = new NBTList(contentType);
            typedTags.forEach((value, valueType) -> {
                if (valueType != contentType) {
                    assertThrows(IllegalArgumentException.class, () -> list.add(value));
                    assertEquals(0, list.size(), "Failed add() should not increase size");
                }
            });
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void get_shouldReturnCorrectValueForIndex(Set<Object> valueSet, TagType contentType) {
        // Wrap in a list so it can be indexed.
        List<Object> values = new ArrayList<>(valueSet);

        NBTList list = new NBTList(contentType);

        for (int i = 0; i < values.size(); i++) {
            list.add(values.get(i));

            assertEquals(i + 1, list.size());
            assertFalse(list.isEmpty());
        }

        for (int i = 0; i < values.size(); i++) {
            assertEquals(values.get(i), list.get(i));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void getter_shouldReturnCorrectValueForIndex(Set<Object> valueSet, TagType contentType) {
        List<Object> values = new ArrayList<>(valueSet);
        Getter getter = getters.get(contentType);

        NBTList list = new NBTList(contentType);
        for (int i = 0; i < values.size(); i++) {
            list.add(values.get(i));

            assertEquals(i + 1, list.size());
            assertFalse(list.isEmpty());
        }

        for (int i = 0; i < list.size(); i++) {
            assertEquals(values.get(i), getter.get(list, i));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void getter_shouldThrowIfGetterDoesNotMatchContentType(Set<Object> valueSet, TagType contentType) {
        List<Object> values = new ArrayList<>(valueSet);

        NBTList list = new NBTList(contentType);
        for (int i = 0; i < values.size(); i++) {
            list.add(values.get(i));

            assertEquals(i + 1, list.size());
            assertFalse(list.isEmpty());
        }

        for (TagType otherType : TagType.values()) {
            if (otherType != contentType && otherType != TagType.END) {
                Getter otherGetter = getters.get(otherType);

                for (int i = 0; i < list.size(); i++) {
                    // Allows index to be used in the lambda.
                    int index = i;

                    assertThrows(IllegalStateException.class,
                        () -> otherGetter.get(list, index)
                    );
                }
            }
        }
    }

    /*@Test
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

            assertDoesNotThrow(
                () -> list.addAll(Arrays.asList(tag, alternativeTestTags.get(type))));

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
        assertThrows(expect,
            () -> list.addAll(Arrays.asList(testTags.get(TagType.BYTE), invalidValue)));
        assertThrows(expect,
            () -> list.addAll(Arrays.asList(invalidValue, testTags.get(TagType.BYTE))));
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

    */

    /**
     * Assert that a list's {@code getter} for a tag type throws an exception when it is used for a
     * list of a different tag type
     *//*
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
    }*/

    private interface Getter {

        Object get(NBTList list, int index);
    }
}
