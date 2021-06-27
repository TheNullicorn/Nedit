package me.nullicorn.nedit.type;

import java.util.Arrays;
import java.util.Comparator;

/**
 * The data-types that make up the NBT format.
 *
 * @author Nullicorn
 */
public enum TagType {
    /**
     * Used to indicate the end of a {@link #COMPOUND} tag. Also used as a placeholder type for
     * empty {@link #LIST}s. End tags are never used as a value, so their {@link #getRuntimeType()
     * runtime type} is {@code null}.
     */
    END(0x00, null),

    /**
     * A signed octet. Represented in-memory using the boxed {@link Byte} class.
     */
    BYTE(0x01, Byte.class),

    /**
     * A signed 16-bit integer. Represented in-memory using the boxed {@link Short} class.
     */
    SHORT(0x02, Short.class),

    /**
     * A signed 32-bit integer. Represented in-memory using the boxed {@link Integer} class.
     */
    INT(0x03, Integer.class),

    /**
     * A signed 64-bit integer. Represented in-memory using the boxed {@link Long} class.
     */
    LONG(0x04, Long.class),

    /**
     * A 32-bit floating point number. Represented in-memory using the boxed {@link Float} class.
     */
    FLOAT(0x05, Float.class),

    /**
     * A 64-bit floating point number. Represented in-memory using the boxed {@link Double} class.
     */
    DOUBLE(0x06, Double.class),

    /**
     * A length-prefixed array of signed octets. Represented in-memory using {@code byte[]}.
     */
    BYTE_ARRAY(0x07, byte[].class),

    /**
     * A length-prefixed string of text. Strings are encoded using
     * <a href=https://docs.oracle.com/javase/8/docs/api/java/io/DataInput.html#modified-utf-8>
     * modified UTF-8
     * </a>. They are represented in-memory using Java's {@link String} class.
     */
    STRING(0x08, String.class),

    /**
     * A length-prefixed array of unnamed NBT tags with the same type. Represented in-memory using
     * the {@link NBTList} class.
     */
    LIST(0x09, NBTList.class),

    /**
     * A map-like structure for storing zero or more NBT tags, each associated with a unique {@link
     * #STRING string} name. Unlike {@link #LIST}s, tags inside a compound may have different types
     * from one another. When encoded, the end of the compound is denoted by an unnamed tag whose
     * type is {@link #END}. Compounds are represented in-memory using the {@link NBTCompound}
     * class.
     */
    COMPOUND(0x0A, NBTCompound.class),

    /**
     * A length-prefixed array of signed 32-bit integers. Represented in-memory using {@code
     * int[]}.
     */
    INT_ARRAY(0x0B, int[].class),

    /**
     * A length-prefixed array of signed 64-bit integers. Represented in-memory using {@code
     * long[]}.
     */
    LONG_ARRAY(0x0C, long[].class);

    // Cached result of TagType.values(). Used for reverse lookup by tag ID.
    private static final TagType[] values;

    static {
        values = values();
        Arrays.sort(values, Comparator.comparingInt(TagType::getId));
    }

    private final int      id;
    private final Class<?> runtimeType;

    TagType(int id, Class<?> runtimeType) {
        this.id = id;
        this.runtimeType = runtimeType;
    }

    /**
     * @return The one-byte ID used to mark tags using the type.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The class used in-memory to represent tags with the type.
     */
    public Class<?> getRuntimeType() {
        return runtimeType;
    }

    /**
     * @deprecated Renamed to {@link #getRuntimeType}.
     */
    @Deprecated
    public Class<?> getClazz() {
        return getRuntimeType();
    }

    /**
     * Searches for a {@link TagType} that uses the provided {@link #getId() ID}.
     *
     * @return The tag type associated with the {@code id}, or {@code null} if no type uses that ID.
     */
    public static TagType fromId(int id) {
        if (id < 0 || id >= values.length) {
            return null;
        }
        return values[id];
    }

    /**
     * Determines the {@link TagType} to use when storing the {@code obj} in a {@link #LIST} or
     * {@link #COMPOUND}. If {@code obj == null}, {@link #END} is returned.
     *
     * @throws IllegalArgumentException If there is no TagType that uses the provided object's
     *                                  class.
     * @see #getRuntimeType()
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

        } else if (obj == null) {
            return END;

        } else {
            throw new IllegalArgumentException(obj.getClass().getSimpleName() + " has no NBT type");
        }
    }
}
