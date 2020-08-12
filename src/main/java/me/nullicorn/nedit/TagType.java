package me.nullicorn.nedit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * @author Nullicorn
 */
public enum TagType {
    END(0x00),
    BYTE(0x01),
    SHORT(0x02),
    INT(0x03),
    LONG(0x04),
    FLOAT(0x05),
    DOUBLE(0x06),
    BYTE_ARRAY(0x07),
    STRING(0x08),
    LIST(0x09),
    COMPOUND(0x0A),
    INT_ARRAY(0x0B),
    LONG_ARRAY(0x0C);

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
    private final int id;

    TagType(int id) {
        this.id = id;
    }

    public static TagType fromId(int id) {
        return values.get(id);
    }
}
