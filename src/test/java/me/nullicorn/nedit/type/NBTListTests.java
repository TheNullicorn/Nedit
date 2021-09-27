package me.nullicorn.nedit.type;

import static me.nullicorn.nedit.provider.TagProvider.getProviderForType;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.nullicorn.nedit.provider.AllTagsProvider;
import me.nullicorn.nedit.provider.TagTypesProvider;
import me.nullicorn.nedit.provider.annotation.AllTagsProviderArgs;
import me.nullicorn.nedit.provider.annotation.TagTypesProviderArgs;
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
            assertEquals(i + 1, list.size());
            assertFalse(list.isEmpty());

            i++;
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void add_shouldAddToCorrectIndexIfOneIsSpecified(Set<Object> valueSet, TagType contentType) {
        // Wrap in a list so it can be indexed.
        List<Object> values = new ArrayList<>(valueSet);

        NBTList list = new NBTList(contentType);
        list.add(values.get(0));
        assertEquals(1, list.size());

        for (int v = 1; v < values.size(); v++) {
            // Add new value at the middle of the list.
            int index = list.size() / 2;

            Object newValue = values.get(v);
            Object prevValue = list.get(index);
            int prevSize = list.size();

            list.add(index, newValue);

            assertEquals(prevSize + 1, list.size(), "List size did not increase");
            assertSame(newValue, list.get(index), "Value is not at specified index");
            assertSame(prevValue, list.get(index + 1), "Prev value was not moved to next index");
        }
    }

    @ParameterizedTest
    @ArgumentsSource(TagTypesProvider.class)
    @TagTypesProviderArgs(skipEndTag = true)
    void add_shouldThrowIfValueIsNull(TagType contentType) {
        NBTList list = new NBTList(contentType);
        Object valueThatIsNull = null;

        // Check for add() without index.
        assertThrows(NullPointerException.class, () -> list.add(valueThatIsNull));
        assertEquals(0, list.size());

        // And check for add() with index.
        assertThrows(NullPointerException.class, () -> list.add(0, valueThatIsNull));
        assertEquals(0, list.size());
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
    @AllTagsProviderArgs(groupByType = true)
    void add_shouldThrowIfContentTypeIs_END(Set<Object> valueSet) {
        NBTList list = new NBTList(TagType.END);

        for (Object value : valueSet) {
            // Check for add() without index.
            assertThrows(IllegalStateException.class, () -> list.add(value));
            assertEquals(0, list.size());

            // And check for add() with index.
            assertThrows(IllegalStateException.class, () -> list.add(0, value));
            assertEquals(0, list.size());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void add_shouldThrowIfIndexIsOutOfBounds(Set<Object> valueSet, TagType contentType) {
        List<Object> values = new ArrayList<>(valueSet);

        for (int v = 0; v < values.size(); v++) {
            NBTList list = new NBTList(contentType);

            // Add "v" number of values from the input set.
            list.addAll(values.subList(0, v));
            assertEquals(v, list.size());

            // Test 500 indices above & below the list's bounds.
            int min = -500;
            int max = v + 500;
            for (int i = min; i < max; i++) {
                // Ignore in-bounds indices for this test.
                if (i >= 0 && i <= list.size()) {
                    continue;
                }

                int badIndex = i;
                Object value = values.get(0);

                assertThrows(IndexOutOfBoundsException.class, () -> list.add(badIndex, value));
                assertEquals(v, list.size()); // Make sure size did not increase.
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void addAll_shouldAddAllTagsInOrderIfValid(Set<Object> valueSet, TagType contentType) {
        // Wrap in a list so it can be indexed.
        List<Object> values = new ArrayList<>(valueSet);

        NBTList list = new NBTList(contentType);
        list.addAll(values);

        assertEquals(values.size(), list.size());

        for (int i = 0; i < values.size(); i++) {
            assertSame(values.get(i), list.get(i));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void addAll_shouldThrowIfAnyTagIsInvalidForContentType(Set<Object> valueSet, TagType contentType) {
        for (TagType otherType : TagType.values()) {
            if (otherType == contentType || otherType == TagType.END) {
                continue;
            }

            NBTList listDiffType = new NBTList(otherType);
            assertThrows(IllegalArgumentException.class, () -> listDiffType.addAll(valueSet));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(TagTypesProvider.class)
    @TagTypesProviderArgs(skipEndTag = true)
    void addAll_shouldThrowIfCollectionIsNull(TagType contentType) {
        NBTList list = new NBTList(contentType);
        Collection<?> nullCollection = null;

        assertThrows(NullPointerException.class, () -> list.addAll(nullCollection));
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void addAll_shouldThrowIfAnyTagIsNull(Set<Object> valueSet, TagType contentType) {
        NBTList list = new NBTList(contentType);

        valueSet.add(null);
        assertThrows(NullPointerException.class, () -> list.addAll(valueSet));
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true)
    void addAll_shouldThrowIfContentTypeIs_END(Set<Object> valueSet) {
        NBTList list = new NBTList(TagType.END);

        assertThrows(IllegalStateException.class, () -> list.addAll(valueSet));
        assertEquals(0, list.size());

        assertThrows(IllegalStateException.class, () -> list.addAll(0, valueSet));
        assertEquals(0, list.size());
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void get_shouldReturnCorrectValueForIndex(Set<Object> valueSet, TagType contentType) {
        // Wrap in a list so it can be indexed.
        List<Object> values = new ArrayList<>(valueSet);

        NBTList list = new NBTList(contentType);
        list.addAll(values);

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
        list.addAll(values);

        for (int i = 0; i < values.size(); i++) {
            assertEquals(values.get(i), getter.get(list, i));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void getter_shouldThrowIfGetterDoesNotMatchContentType(Set<Object> valueSet, TagType contentType) {
        NBTList list = new NBTList(contentType);

        list.addAll(valueSet);
        assertEquals(valueSet.size(), list.size());

        for (TagType otherType : TagType.values()) {
            if (otherType != contentType && otherType != TagType.END) {
                Getter otherGetter = getters.get(otherType);

                for (int i = 0; i < list.size(); i++) {
                    // Allows index to be used in the lambda.
                    int index = i;

                    assertThrows(IllegalStateException.class, () -> otherGetter.get(list, index));
                }
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void set_shouldSetTagAtCorrectIndex(Set<Object> valueSet, TagType contentType) {
        List<Object> values = new ArrayList<>(valueSet);
        Object extraValue = getProviderForType(contentType).getExtraneousValue();

        NBTList list = new NBTList(contentType);

        list.addAll(values);
        assertEquals(valueSet.size(), list.size());

        for (int i = 0; i < list.size(); i++) {
            // Make sure the old value is at the right index beforehand.
            assertSame(values.get(i), list.get(i));

            Object prevValue = list.set(i, extraValue);

            // Make sure the returned "previous" value is the correct one.
            assertSame(values.get(i), prevValue);
            // Make sure the new value is now in the list.
            assertSame(extraValue, list.get(i));
            // Make sure the list's size wasn't changed by the set().
            assertEquals(valueSet.size(), list.size());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupAsOne = true, provideTypes = true)
    void set_shouldThrowIfValueIsInvalidForContentType(Map<Object, TagType> typedTags) {
        for (TagType contentType : TagType.values()) {
            if (contentType == TagType.END) {
                continue;
            }

            NBTList list = new NBTList(contentType);

            // Populate the list so that there's some indices we can set() on.
            typedTags.keySet()
                .stream()
                .filter(value -> contentType == typedTags.get(value))
                .forEach(list::add);

            // Make sure set() fails when adding tags of any DIFFERENT type.
            typedTags.forEach((value, valueType) -> {
                if (valueType != contentType) {
                    Object prevValue = list.get(0);

                    assertThrows(IllegalArgumentException.class, () -> list.set(0, value));
                    // Make sure the operation wasn't actually applied.
                    assertSame(prevValue, list.get(0));
                }
            });
        }
    }

    @ParameterizedTest
    @ArgumentsSource(TagTypesProvider.class)
    @TagTypesProviderArgs(skipEndTag = true)
    void set_shouldThrowIfTagIsNull(TagType contentType) {
        Object tagThatIsNull = null;

        NBTList list = new NBTList(contentType);
        assertThrows(NullPointerException.class, () -> list.set(0, tagThatIsNull));
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    @AllTagsProviderArgs(groupByType = true, provideTypes = true)
    void set_shouldThrowIfIndexIsOutOfBounds(Set<Object> valueSet, TagType contentType) {
        List<Object> values = new ArrayList<>(valueSet);

        NBTList list = new NBTList(contentType);
        list.addAll(values);

        int range = 500; // Arbitrary # of OOB indices to check above & below.
        int min = -range;
        int max = list.size() + range - 1;
        Object someTag = values.get(0);

        for (int i = min; i < max; i++) {
            if (i < 0 || i >= list.size()) {
                int badIndex = i; // Required for lambda.
                assertThrows(IndexOutOfBoundsException.class, () -> list.set(badIndex, someTag));
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(AllTagsProvider.class)
    void set_shouldThrowIfContentTypeIs_END(Object tagValue) {
        NBTList list = new NBTList(TagType.END);

        assertThrows(IllegalStateException.class, () -> list.set(0, tagValue));
        assertEquals(0, list.size());
    }

    private interface Getter {

        Object get(NBTList list, int index);
    }
}
