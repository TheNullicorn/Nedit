package me.nullicorn.nedit.type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TagTypeTests {

    private List<TagType> allTags;
    private List<?>       validObjects;

    @BeforeEach
    void setUp() {
        allTags = Collections.unmodifiableList(Arrays.asList(
            TagType.END,
            TagType.BYTE,
            TagType.SHORT,
            TagType.INT,
            TagType.LONG,
            TagType.FLOAT,
            TagType.DOUBLE,
            TagType.BYTE_ARRAY,
            TagType.STRING,
            TagType.LIST,
            TagType.COMPOUND,
            TagType.INT_ARRAY,
            TagType.LONG_ARRAY));

        validObjects = Collections.unmodifiableList(Arrays.asList(
            null,
            Byte.MAX_VALUE,
            Short.MAX_VALUE,
            Integer.MAX_VALUE,
            Long.MAX_VALUE,
            Float.MAX_VALUE,
            Double.MAX_VALUE,
            new byte[]{0, 1, 2, 3, 4},
            "Hello World!",
            new NBTList(TagType.END),
            new NBTCompound(),
            new int[]{0, 1, 2, 3, 4},
            new long[]{0, 1, 2, 3, 4}
        ));
    }

    @Test
    void shouldSerializeIdCorrectly() {
        for (int i = 0; i < allTags.size(); i++) {
            Assertions.assertEquals(i, allTags.get(i).getId());
        }
    }

    @Test
    void shouldDeserializeIdCorrectly() {
        for (int i = 0; i < allTags.size(); i++) {
            Assertions.assertEquals(allTags.get(i), TagType.fromId(i));
        }
    }

    @Test
    void shouldFindCorrectTagForObject() {
        Assertions.assertEquals(allTags.size(), validObjects.size());

        for (int i = 0; i < allTags.size(); i++) {
            Assertions.assertEquals(allTags.get(i), TagType.fromObject(validObjects.get(i)));
        }
    }

    @Test
    void shouldHaveCorrectClassForTag() {
        Assertions.assertEquals(allTags.size(), validObjects.size());

        for (int i = 0; i < allTags.size(); i++) {
            Class<?> tagClass = null;
            if (validObjects.get(i) != null) {
                tagClass = validObjects.get(i).getClass();
            }
            Assertions.assertEquals(allTags.get(i).getClazz(), tagClass);
        }
    }
}
