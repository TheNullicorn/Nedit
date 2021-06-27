package me.nullicorn.nedit.type;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.nullicorn.nedit.filter.FilteredTag;

/**
 * A map-like structure for storing NBT tags associated with unique UTF-8 (<a
 * href=https://docs.oracle.com/javase/8/docs/api/java/io/DataInput.html#modified-utf-8>modified</a>)
 * names.
 *
 * @author Nullicorn
 */
public class NBTCompound extends AbstractMap<String, Object> {

    private final Map<String, Object> decorated;

    public NBTCompound() {
        decorated = new HashMap<>();
    }

    /**
     * @return {@code true} if any of the tags in the compound have a {@code name} and tag-{@code
     * type} matching those provided. Otherwise {@code false}.
     */
    public boolean containsTag(String name, TagType type) {
        if (name != null) {
            Object value = get(name);
            return value != null && TagType.fromObject(value) == type;
        }
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    /**
     * @param name         A dot-separated path to the desired tag
     * @param defaultValue The value to return if the tag does not exist
     * @return The double at the desired path. If a tag with that name does not exist (or is not a
     * number), the {@code defaultValue} is returned.
     * @see #get(String)
     */
    public double getDouble(String name, double defaultValue) {
        return getNumber(name, defaultValue).doubleValue();
    }

    /**
     * @param name         A dot-separated path to the desired tag
     * @param defaultValue The value to return if the tag does not exist
     * @return The float at the desired path. If a tag with that name does not exist (or is not a
     * number), the {@code defaultValue} is returned.
     * @see #get(String)
     */
    public float getFloat(String name, float defaultValue) {
        return getNumber(name, defaultValue).floatValue();
    }

    /**
     * @param name         A dot-separated path to the desired tag
     * @param defaultValue The value to return if the tag does not exist
     * @return The short at the desired path. If a tag with that name does not exist (or is not a
     * number), the {@code defaultValue} is returned.
     * @see #get(String)
     */
    public short getShort(String name, short defaultValue) {
        return getNumber(name, defaultValue).shortValue();
    }

    /**
     * @param name         A dot-separated path to the desired tag
     * @param defaultValue The value to return if the tag does not exist
     * @return The long at the desired path. If a tag with that name does not exist (or is not a
     * number), the {@code defaultValue} is returned.
     * @see #get(String)
     */
    public long getLong(String name, long defaultValue) {
        return getNumber(name, defaultValue).longValue();
    }

    /**
     * @param name         A dot-separated path to the desired tag
     * @param defaultValue The value to return if the tag does not exist
     * @return The integer at the desired path. If a tag with that name does not exist (or is not a
     * number), the {@code defaultValue} is returned.
     * @see #get(String)
     */
    public int getInt(String name, int defaultValue) {
        return getNumber(name, defaultValue).intValue();
    }

    /**
     * @param name         A dot-separated path to the desired tag
     * @param defaultValue The value to return if the tag does not exist
     * @return The byte at the desired path. If a tag with that name does not exist (or is not a
     * number), the {@code defaultValue} is returned.
     * @see #get(String)
     */
    public byte getByte(String name, byte defaultValue) {
        return getNumber(name, defaultValue).byteValue();
    }

    /**
     * @param name         A dot-separated path to the desired tag
     * @param defaultValue The value to return if the tag does not exist
     * @return The double at the desired path. If a tag with that name does not exist (or is not a
     * number), the {@code defaultValue} is returned.
     * @see #get(String)
     */
    public Number getNumber(String name, Number defaultValue) {
        Object result = get(name);
        return result instanceof Number
            ? (Number) result
            : defaultValue;
    }

    /**
     * @param name A dot-separated path to the desired tag
     * @return The string at the desired path. If a tag with that name does not exist, the {@code
     * defaultValue} is returned. If a value exists for that name, but is the wrong type, the value
     * is converted to a string using its {@link Object#toString() toString()} method.
     * @see #get(String)
     */
    public String getString(String name, String defaultValue) {
        Object result = get(name);
        return result != null
            ? result.toString()
            : defaultValue;
    }

    /**
     * @param name A dot-separated path to the desired tag
     * @return The long-array at the desired path. If a tag with that name does not exist (or is not
     * a {@code long[]}), then {@code null} is returned.
     * @see #get(String)
     */
    public long[] getLongArray(String name) {
        Object result = get(name);
        return result instanceof long[]
            ? (long[]) result
            : null;
    }

    /**
     * @param name A dot-separated path to the desired tag
     * @return The integer-array at the desired path. If a tag with that name does not exist (or is
     * not an {@code int[]}), then {@code null} is returned.
     * @see #get(String)
     */
    public int[] getIntArray(String name) {
        Object result = get(name);
        return result instanceof int[]
            ? (int[]) result
            : null;
    }

    /**
     * @param name A dot-separated path to the desired tag
     * @return The byte-array at the desired path. If a tag with that name does not exist (or is not
     * a {@code byte[]}), then {@code null} is returned.
     * @see #get(String)
     */
    public byte[] getByteArray(String name) {
        Object result = get(name);
        return result instanceof byte[]
            ? (byte[]) result
            : null;
    }

    /**
     * @param name A dot-separated path to the desired tag
     * @return The NBT list at the desired path. If a tag with that name does not exist (or is not a
     * list), then {@code null} is returned.
     * @see #get(String)
     */
    public NBTList getList(String name) {
        Object result = get(name);
        return result instanceof NBTList
            ? (NBTList) result
            : null;
    }

    /**
     * @param name A dot-separated path to the desired tag
     * @return The NBT compound at the desired path. If a tag with that name does not exist (or is
     * not a compound), then {@code null} is returned.
     * @see #get(String)
     */
    public NBTCompound getCompound(String name) {
        Object result = get(name);
        return result instanceof NBTCompound
            ? (NBTCompound) result
            : null;
    }

    /**
     * Retrieves the value of a tag inside the compound. If dot-notation is used in the {@code
     * name}, this method is able to access tags inside nested and deeply-nested compounds as well.
     * <p><br>
     * Consider the following NBT data:
     * <pre>{@code {
     *   user: {
     *     name: "User291",
     *     id: "ae630aa9-cdbb-4aaf-be42-868889585b4d",
     *     tokens: 19531L,
     *     socialMedia: {
     *       discord: "Player291#4444",
     *       twitter: "User291"
     *     }
     *   }
     * }}</pre>
     * In this case, to access the user's <b>Discord username</b>, we'd need the path:
     * <pre>{@code "user.socialMedia.discord"}</pre>
     * To access the <b>user's ID</b>, we could use the path:
     * <pre>{@code "user.id"}</pre>
     * And to access the <b>entire user</b> compound, we could just use the path:
     * <pre>{@code "user"}</pre>
     *
     * @param name The name of the tag whose value should be returned. May be dot-notation to access
     *             nested tags. Any literal dots in the name (dots that do not indicating nesting)
     *             must be escaped using a backslash (or double-backslash {@code \\.} in literal
     *             strings),
     * @return The value of the tag associated with the {@code name}. If the name is {@code null},
     * or if there is no tag in the compound with that name, then {@code null} is returned.
     */
    public Object get(String name) {
        if (name == null) {
            return null;
        }

        String[] tokens = FilteredTag.tokenizeTagName(name);
        if (tokens.length == 1) {
            return decorated.get(name);
        }

        NBTCompound parent = this;
        for (int i = 0; i < tokens.length; i++) {

            Object child = parent.decorated.get(tokens[i]);
            if (i + 1 == tokens.length) {
                // No more tokens; current child must be the output.
                return child;
            }

            // More tokens follow; child must be a compound to continue.
            if (!(child instanceof NBTCompound)) {
                break;
            }
            parent = (NBTCompound) child;
        }

        return null;
    }

    /**
     * See
     *
     * @return {@code null} if the {@code name} is not a {@link String}.
     * @see #get(String)
     */
    @Override
    public Object get(Object name) {
        return name instanceof String
            ? get((String) name)
            : null;
    }

    /**
     * @throws IllegalArgumentException If the value is {@code null}, or if it cannot be {@link
     *                                  TagType#getRuntimeType() represented} as an NBT type.
     */
    @Override
    public Object put(String name, Object value) {
        checkTag(name, value);
        return decorated.put(name, value);
    }

    /**
     * @throws IllegalArgumentException If the value is {@code null}, or if it cannot be {@link
     *                                  TagType#getRuntimeType() represented} as an NBT type.
     */
    @Override
    public Object putIfAbsent(String name, Object value) {
        checkTag(name, value);
        return decorated.putIfAbsent(name, value);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return decorated.entrySet();
    }

    @Override
    public int size() {
        return decorated.size();
    }

    /**
     * Throws an {@link IllegalArgumentException} if the {@code value} cannot be converted to an NBT
     * tag type.
     */
    private void checkTag(String name, Object value) {
        String errorMsg = null;
        if (name == null) {
            errorMsg = "Compounds cannot have null tag names (value=" + value + ")";
        } else if (value == null) {
            errorMsg = "Compounds cannot have null tag values (name=" + name + ")";
        }

        TagType type = TagType.fromObject(value);
        if (type == TagType.END) {
            errorMsg = "TAG_End cannot be used as a value (name=" + name + ",value=" + value + ")";
        }

        if (errorMsg != null) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    /**
     * @return The compound represented by an SNBT string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        int i = 0;
        for (Entry<String, Object> tag : entrySet()) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(tag.getKey().isEmpty() ? "\"\"" : tag.getKey());
            sb.append(":");
            sb.append(tagToString(tag.getValue()));
            i++;
        }

        sb.append("}");
        return sb.toString();
    }

    /*
     *
     * ============ toString() Helper Functions ============
     *
     */

    private static String tagToString(Object value) {
        TagType tagType = TagType.fromObject(value);
        switch (tagType) {
            case BYTE:
                return byteToString((Byte) value);
            case SHORT:
                return shortToString((Short) value);
            case INT:
                return intToString((Integer) value);
            case LONG:
                return longToString((Long) value);
            case FLOAT:
                return floatToString((Float) value);
            case DOUBLE:
                return doubleToString((Double) value);
            case BYTE_ARRAY:
                return byteArrayToString((byte[]) value);
            case STRING:
                return stringTagToString((String) value);
            case LIST:
                return listToString((NBTList) value);
            case COMPOUND:
                return value.toString();
            case INT_ARRAY:
                return intArrayToString((int[]) value);
            case LONG_ARRAY:
                return longArrayToString((long[]) value);
            default:
                return "";
        }
    }

    private static String byteToString(byte value) {
        return value + "b";
    }

    private static String shortToString(short value) {
        return value + "s";
    }

    private static String intToString(int value) {
        return Integer.toString(value);
    }

    private static String longToString(long value) {
        return value + "l";
    }

    private static String floatToString(float value) {
        return value + "f";
    }

    private static String doubleToString(double value) {
        return value + "d";
    }

    private static String byteArrayToString(byte[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("[B;");
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i]);
            if (i < value.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static String stringTagToString(String value) {
        return "\"" + value + "\"";
    }

    private static String listToString(NBTList value) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < value.size(); i++) {
            sb.append(value.get(i) != null ? tagToString(value.get(i)) : 0);
            if (i < value.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static String intArrayToString(int[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("[I;");
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i]);
            if (i < value.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static String longArrayToString(long[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("[L;");
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i]);
            if (i < value.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof NBTCompound)) {
            return false;
        }
        NBTCompound c = (NBTCompound) o;

        if (c.size() != this.size()) {
            return false;
        }

        for (Entry<String, Object> entry : decorated.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Object oValue = c.get(key);

            if (value == null) {
                if (!(oValue == null && c.containsKey(key))) {
                    return false;
                }
            } else if (!Objects.deepEquals(value, oValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (Entry<String, Object> entry : decorated.entrySet()) {
            final Object value = entry.getValue();
            final int valHash;

            if (value instanceof byte[]) {
                valHash = Arrays.hashCode((byte[]) value);
            } else if (value instanceof int[]) {
                valHash = Arrays.hashCode((int[]) value);
            } else if (value instanceof long[]) {
                valHash = Arrays.hashCode((long[]) value);
            } else {
                valHash = value.hashCode();
            }

            hashCode += Objects.hashCode(entry.getKey()) ^ valHash;
        }
        return hashCode;
    }
}