package me.nullicorn.nedit.type;

import java.util.Arrays;
import java.util.Comparator;
import lombok.Getter;

/**
 * The 13 tag types that make up the NBT format
 *
 * @author Nullicorn
 */
public enum TagType {
    /**
     * Marks the end of a {@link #COMPOUND} tag and is used in various other places as a substitute for null
     */
    END(0x00, Void.class),

    /**
     * Represents a signed byte
     */
    BYTE(0x01, Byte.class),

    /**
     * Represents an signed, short integer with a max value of {@link Short#MAX_VALUE} and a min value of {@link Short#MIN_VALUE}
     */
    SHORT(0x02, Short.class),

    /**
     * Represents an integer with a max value of {@link Integer#MAX_VALUE} and a min value of {@link Integer#MIN_VALUE}
     */
    INT(0x03, Integer.class),

    /**
     * Represents a long integer with a max value of {@link Long#MAX_VALUE} and a min value of {@link Long#MIN_VALUE}
     */
    LONG(0x04, Long.class),

    /**
     * Represents an floating-point number with a max value of {@link Float#MAX_VALUE} and a min value of {@link Float#MIN_VALUE}
     */
    FLOAT(0x05, Float.class),

    /**
     * Represents an double-precision floating-point number with a max value of {@link Double#MAX_VALUE} and a min value of {@link Double#MIN_VALUE}
     */
    DOUBLE(0x06, Double.class),

    /**
     * Represents a length-prefixed array of signed bytes
     */
    BYTE_ARRAY(0x07, byte[].class),

    /**
     * Represents a length-prefixed string of text
     */
    STRING(0x08, String.class),

    /**
     * Represents a length-prefixed list of elements that share the same {@link TagType}
     */
    LIST(0x09, NBTList.class),

    /**
     * Represents a map of field names to tags
     */
    COMPOUND(0x0A, NBTCompound.class),

    /**
     * Represents a length-prefixed array of signed integers
     */
    INT_ARRAY(0x0B, int[].class),

    /**
     * Represents a length-prefixed array of signed long integers
     */
    LONG_ARRAY(0x0C, long[].class);

    // An unmodifiable map of IDs to types (for reverse search)
    private static final TagType[] values;

    static {
        values = values();
        Arrays.sort(values, Comparator.comparingInt(TagType::getId));
    }

    /**
     * The 1-byte ID of this tag type
     */
    @Getter
    private final int id;

    /**
     * The Java class used to represent tags of this type in memory
     */
    @Getter
    private final Class<?> clazz;

    TagType(int id, Class<?> typeClazz) {
        this.id = id;
        this.clazz = typeClazz;
    }

    /**
     * Reverse search for a {@link TagType} using its ID
     *
     * @return The TagType associated with that ID, or null if none is
     */
    public static TagType fromId(int id) {
        if (id == -1) {
            return END;
        } else if (id < 0 || id >= values.length) {
            return null;
        }
        return values[id];
    }

    /**
     * Get the TagType associated with an object's class
     *
     * @param obj The object to get the TagType of
     * @return The TagType that the object can be stored as, or {@link #END} if the object has no associated TagType
     */
    public static TagType fromObject(Object obj) {
        if (obj instanceof Byte) {
            return BYTE;

        } else if (obj instanceof Short) {
            return SHORT;

        } else if (obj instanceof Integer) {
            return INT;

        } else if (obj instanceof Long) {
            return LONG;

        } else if (obj instanceof Float) {
            return FLOAT;

        } else if (obj instanceof Double) {
            return DOUBLE;

        } else if (obj instanceof byte[]) {
            return BYTE_ARRAY;

        } else if (obj instanceof String) {
            return STRING;

        } else if (obj instanceof NBTList) {
            return LIST;

        } else if (obj instanceof NBTCompound) {
            return COMPOUND;

        } else if (obj instanceof int[]) {
            return INT_ARRAY;

        } else if (obj instanceof long[]) {
            return LONG_ARRAY;

        } else {
            return END;
        }
    }
}
