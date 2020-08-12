package me.nullicorn.nedit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * @author Nullicorn
 */
public enum TagType {
    END(0x00, Void.class),
    BYTE(0x01, Byte.class),
    SHORT(0x02, Short.class),
    INT(0x03, Integer.class),
    LONG(0x04, Long.class),
    FLOAT(0x05, Float.class),
    DOUBLE(0x06, Double.class),
    BYTE_ARRAY(0x07, Byte[].class),
    STRING(0x08, String.class),
    LIST(0x09, NBTList.class),
    COMPOUND(0x0A, NBTCompound.class),
    INT_ARRAY(0x0B, Integer[].class),
    LONG_ARRAY(0x0C, Long[].class);

    // Create an unmodifiable map of IDs to types (for reverse search)
    private static final Map<Integer, TagType> values;

    static {
        Map<Integer, TagType> valueMap = new HashMap<>();
        for (TagType tagType : TagType.values()) {
            valueMap.put(tagType.getId(), tagType);
        }
        values = Collections.unmodifiableMap(valueMap);
    }

    @Getter
    private final int      id;
    @Getter
    private final Class<?> clazz;

    TagType(int id, Class<?> typeClazz) {
        this.id = id;
        this.clazz = typeClazz;
    }

    public static TagType fromId(int id) {
        if (id == -1) {
            return END;
        }
        return values.get(id);
    }
}
