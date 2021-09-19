package me.nullicorn.nedit.type;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import me.nullicorn.nedit.filter.FilteredTag;

/**
 * A map-like structure for storing NBT tags associated with unique UTF-8 (<a
 * href=https://docs.oracle.com/javase/8/docs/api/java/io/DataInput.html#modified-utf-8>modified</a>)
 * names.
 * <p><br>
 * <h3 id="nesting">Nesting</h3>
 * If a compound is inside another compound, the inner one is considered to be "nested", and the
 * outer one is its "parent". To access the nested compound's contents, you would first need to
 * {@link #getCompound(String) get} it from its parent, check that it is not null, and then repeat
 * for any further nested compounds. To avoid this, the implementation allows you to use
 * dot-notation in these cases. More specifically, dot-notation is valid in methods that
 * <b>cannot</b> alter the structure of the compound (and those that accept a tag name), like
 * {@link #get(String) getter} and {@link #containsTag(String, TagType) contains} methods.
 * <p>
 * Consider the following NBT data:
 * <pre>{@code
 * {
 *   user: {
 *     name: "User291",
 *     id: "ae630aa9-cdbb-4aaf-be42-868889585b4d",
 *     coins: 19531L,
 *     social_media: {
 *       discord: "Player291#4444",
 *       twitter: "User291",
 *       "i_have_a_dot_._in_me": true
 *     },
 *   }
 * }
 * }</pre>
 * In this case, to access the user's <b>Discord username</b>, we'd use the path
 * <pre>{@code "user.social_media.discord"}</pre>
 * To access the <b>user's ID</b>, we would use
 * <pre>{@code "user.id"}</pre>
 * And to access the <b>entire user</b> compound, just
 * <pre>{@code "user"}</pre>
 * If a tag's name has a literal dot ({@code .}) in it that does not indicate nesting, a backslash
 * can be used to escape the character. This means using a double-backslash in string literals, such
 * as
 * <pre>{@code "user.social_media.i_have_a_dot_\\._in_me"}</pre>
 * <p><br>
 * In any case, dot-notation is not required, and a compound's direct children can still be accessed
 * normally using their respective names.
 *
 * @author Nullicorn
 */
public class NBTCompound extends AbstractMap<String, Object> {

    private final Map<String, Object> decorated;

    /**
     * Creates an empty NBT compound.
     */
    public NBTCompound() {
        decorated = new HashMap<>();
    }

    /**
     * @param name The name of the NBT tag whose presence and type should be checked for. <a
     *             href="#nesting">Dot-notation</a> is supported for checking nested tags.
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

    /**
     * Same as {@link #containsTag(String, TagType) containsTag(...)}, but {@code true} will be
     * returned if any tag in the compound uses the {@code key}, regardless of the tag's type.
     * <p><br>
     *
     * @param key The string name of the NBT tag whose presence should be checked for. <a
     *            href="#nesting">Dot-notation</a> is supported for checking nested tags.
     * @throws NullPointerException If the supplied {@code key} is {@code null}.
     */
    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    /**
     * @param name         The name of the tag whose double value should be returned. <a
     *                     href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @param defaultValue The value to return if the tag does not exist, or cannot be cast to the
     *                     appropriate type.
     * @return The value of the numeric tag associated with the {@code name}, cast to a double if it
     * isn't already. If a tag with that name does not exist (or is not a number), the {@code
     * defaultValue} is returned.
     * @see #get(String)
     */
    public double getDouble(String name, double defaultValue) {
        return getNumber(name, defaultValue).doubleValue();
    }

    /**
     * @param name         The name of the tag whose float value should be returned. <a
     *                     href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @param defaultValue The value to return if the tag does not exist, or cannot be cast to the
     *                     appropriate type.
     * @return The value of the numeric tag associated with the {@code name}, cast to a float if it
     * isn't already. If a tag with that name does not exist (or is not a number), the {@code
     * defaultValue} is returned.
     * @see #get(String)
     */
    public float getFloat(String name, float defaultValue) {
        return getNumber(name, defaultValue).floatValue();
    }

    /**
     * @param name         The name of the tag whose short value should be returned. <a
     *                     href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @param defaultValue The value to return if the tag does not exist, or cannot be cast to the
     *                     appropriate type.
     * @return The value of the numeric tag associated with the {@code name}, cast to a short if it
     * isn't already. If a tag with that name does not exist (or is not a number), the {@code
     * defaultValue} is returned.
     * @see #get(String)
     */
    public short getShort(String name, short defaultValue) {
        return getNumber(name, defaultValue).shortValue();
    }

    /**
     * @param name         The name of the tag whose long value should be returned. <a
     *                     href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @param defaultValue The value to return if the tag does not exist, or cannot be cast to the
     *                     appropriate type.
     * @return The value of the numeric tag associated with the {@code name}, cast to a long if it
     * isn't already. If a tag with that name does not exist (or is not a number), the {@code
     * defaultValue} is returned.
     * @see #get(String)
     */
    public long getLong(String name, long defaultValue) {
        return getNumber(name, defaultValue).longValue();
    }

    /**
     * @param name         The name of the tag whose int value should be returned. <a
     *                     href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @param defaultValue The value to return if the tag does not exist, or cannot be cast to the
     *                     appropriate type.
     * @return The value of the numeric tag associated with the {@code name}, cast to an int if it
     * isn't already. If a tag with that name does not exist (or is not a number), the {@code
     * defaultValue} is returned.
     * @see #get(String)
     */
    public int getInt(String name, int defaultValue) {
        return getNumber(name, defaultValue).intValue();
    }

    /**
     * @param name         The name of the tag whose byte value should be returned. <a
     *                     href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @param defaultValue The value to return if the tag does not exist, or cannot be cast to the
     *                     appropriate type.
     * @return The value of the numeric tag associated with the {@code name}, cast to a byte if it
     * isn't already. If a tag with that name does not exist (or is not a number), the {@code
     * defaultValue} is returned.
     * @see #get(String)
     */
    public byte getByte(String name, byte defaultValue) {
        return getNumber(name, defaultValue).byteValue();
    }

    /**
     * @param name         The name of the tag whose numeric value should be returned. <a
     *                     href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @param defaultValue The value to return if the tag does not exist, or cannot be cast to the
     *                     appropriate type.
     * @return The value of the numeric tag associated with the {@code name}. If a tag with that
     * name does not exist (or is not a number), the {@code defaultValue} is returned.
     * @see #get(String)
     */
    public Number getNumber(String name, Number defaultValue) {
        Object result = get(name);
        return result instanceof Number
            ? (Number) result
            : defaultValue;
    }

    /**
     * Same as {@link #getString(String, String defaultValue)}, but the default value is always
     * {@code null}.
     */
    public String getString(String name) {
        return getString(name, null);
    }

    /**
     * @param name         The name of the string whose value should be returned. <a
     *                     href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @param defaultValue The value to return if the tag does not exist. May be null.
     * @return The stringified value of the tag associated with the {@code name}. If a tag with that
     * name does not exist, the {@code defaultValue} is returned. If a value exists for that name,
     * but is not already a string, the value is converted to a string using its {@link
     * Object#toString() toString()} method.
     * @see #get(String)
     */
    public String getString(String name, String defaultValue) {
        Object result = get(name);
        return result != null
            ? result.toString()
            : defaultValue;
    }

    /**
     * @param name The name of the long-array whose value should be returned. <a
     *             href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @return The long-array tag associated with the {@code name}. If a tag with that name does not
     * exist (or is not a {@code long[]}), then {@code null} is returned.
     * @see #get(String)
     */
    public long[] getLongArray(String name) {
        Object result = get(name);
        return result instanceof long[]
            ? (long[]) result
            : null;
    }

    /**
     * @param name The name of the integer-array whose value should be returned. <a
     *             href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @return The integer-array tag associated with the {@code name}. If a tag with that name does
     * not exist (or is not an {@code int[]}), then {@code null} is returned.
     * @see #get(String)
     */
    public int[] getIntArray(String name) {
        Object result = get(name);
        return result instanceof int[]
            ? (int[]) result
            : null;
    }

    /**
     * @param name The name of the byte-array whose value should be returned. <a
     *             href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @return The byte-array tag associated with the {@code name}. If a tag with that name does not
     * exist (or is not a {@code byte[]}), then {@code null} is returned.
     * @see #get(String)
     */
    public byte[] getByteArray(String name) {
        Object result = get(name);
        return result instanceof byte[]
            ? (byte[]) result
            : null;
    }

    /**
     * @param name The name of the list whose value should be returned. <a
     *             href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @return The NBT list associated with the {@code name}. If a tag with that name does not exist
     * (or is not a list), then {@code null} is returned.
     * @see #get(String)
     */
    public NBTList getList(String name) {
        Object result = get(name);
        return result instanceof NBTList
            ? (NBTList) result
            : null;
    }

    /**
     * @param name The name of the compound whose value should be returned. <a
     *             href="#nesting">Dot-notation</a> is supported for accessing nested tags.
     * @return The nested NBT compound associated with the {@code name}. If a tag with that name
     * does not exist (or is not a compound), then {@code null} is returned.
     * @see #get(String)
     */
    public NBTCompound getCompound(String name) {
        Object result = get(name);
        return result instanceof NBTCompound
            ? (NBTCompound) result
            : null;
    }

    /**
     * Retrieves the value of a tag inside the compound.
     *
     * @param name The name of the tag whose value should be returned. <a href="#nesting">Dot-notation</a>
     *             is supported for accessing nested tags.
     * @return The value of the tag associated with the {@code name}. If the name is {@code null},
     * or if there is no tag inside the compound with that name, then {@code null} is returned.
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
     * See <a href="#nesting">here</a> for more info.
     *
     * @return {@code null} if the {@code name} is not a {@link String}. Otherwise, returns the
     * result of {@link #get(String)} for that name.
     * @throws NullPointerException If the supplied {@code name} is {@code null}.
     * @see #get(String)
     */
    @Override
    public Object get(Object name) {
        Objects.requireNonNull(name, "Tag name cannot be null");
        return name instanceof String
            ? get((String) name)
            : null;
    }

    /**
     * @throws IllegalArgumentException If the {@code value}, when converted to an NBT type, is
     *                                  {@link TagType#END TAG_End}.
     * @throws NullPointerException     If the supplied {@code name} or {@code value} are {@code
     *                                  null}.
     */
    @Override
    public Object put(String name, Object value) {
        checkTag(name, value);
        return decorated.put(name, value);
    }

    /**
     * @throws IllegalArgumentException If the {@code value}, when converted to an NBT type, is
     *                                  {@link TagType#END TAG_End}.
     * @throws NullPointerException     If the supplied {@code name} or {@code value} are {@code
     *                                  null}.
     */
    @Override
    public Object putIfAbsent(String name, Object value) {
        checkTag(name, value);
        return decorated.putIfAbsent(name, value);
    }

    @Override
    public boolean remove(Object name, Object value) {
        return decorated.remove(name, value);
    }

    @Override
    public Object remove(Object name) {
        return decorated.remove(name);
    }

    @Override
    public void clear() {
        decorated.clear();
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
     * @throws NullPointerException     If the supplied {@code name} or {@code tag} is {@code
     *                                  null}.
     * @throws IllegalArgumentException If the value, when converted to an NBT type, is a {@link
     *                                  TagType#END TAG_End}.
     */
    private void checkTag(String name, Object value) {
        String message = null;
        Function<String, RuntimeException> exception = null;

        if (name == null) {
            message = "Compounds cannot have null tag names (value=" + value + ")";
            exception = NullPointerException::new;

        } else if (value == null) {
            message = "Compounds cannot have null tag values (name=" + name + ")";
            exception = NullPointerException::new;

        } else if (TagType.END == TagType.fromObject(value)) {
            message = "TAG_End cannot be used as a value (name=" + name + ",value=" + value + ")";
            exception = IllegalArgumentException::new;
        }

        // Throw if there was an error.
        if (message != null) {
            throw exception.apply(message);
        }
    }

    /**
     * @return The compound in SNBT format.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        int i = 0;
        for (Entry<String, Object> tag : decorated.entrySet()) {
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