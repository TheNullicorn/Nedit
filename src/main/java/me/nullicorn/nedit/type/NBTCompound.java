package me.nullicorn.nedit.type;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * @author Nullicorn
 */
public class NBTCompound extends HashMap<String, Object> {

    public double getDouble(String key, double defaultValue) {
        return getNumber(key, defaultValue).doubleValue();
    }

    public float getFloat(String key, float defaultValue) {
        return getNumber(key, defaultValue).floatValue();
    }

    public short getShort(String key, short defaultValue) {
        return getNumber(key, defaultValue).shortValue();
    }

    public long getLong(String key, long defaultValue) {
        return getNumber(key, defaultValue).longValue();
    }

    public int getInt(String key, int defaultValue) {
        return getNumber(key, defaultValue).intValue();
    }

    public byte getByte(String key, byte defaultValue) {
        return getNumber(key, defaultValue).byteValue();
    }

    public Number getNumber(String key, Number defaultValue) {
        Object result = get(key);
        return result instanceof Number
            ? (Number) result
            : defaultValue;
    }

    @Nullable
    public String getString(String key, String defaultValue) {
        Object result = get(key);
        return result != null
            ? result.toString()
            : defaultValue;
    }

    @Nullable
    public Long[] getLongArray(String key) {
        Object result = get(key);
        return result instanceof Long[]
            ? (Long[]) result
            : null;
    }

    @Nullable
    public Integer[] getIntArray(String key) {
        Object result = get(key);
        return result instanceof Integer[]
            ? (Integer[]) result
            : null;
    }

    @Nullable
    public Byte[] getByteArray(String key) {
        Object result = get(key);
        return result instanceof Byte[]
            ? (Byte[]) result
            : null;
    }

    @Nullable
    public NBTList getList(String key) {
        Object result = get(key);
        return result instanceof NBTList
            ? (NBTList) result
            : null;
    }

    @Nullable
    public NBTCompound getCompound(String key) {
        Object result = get(key);
        return result instanceof NBTCompound
            ? (NBTCompound) result
            : null;
    }

    @Nullable
    public Object get(String key) {
        if (key == null || key.isEmpty()) {
            return this;
        }

        String[] path = key.split("\\.");
        Object currentObj = this;
        for (int i = 0; i < path.length; i++) {
            currentObj = ((Map<?, ?>) currentObj).get(path[i]);
            if (i == path.length - 1) {
                // We reached the end of the path, return the final value
                return currentObj;
            }

            if (currentObj == null) {
                // We reached a dead-end before expected, return null
                return null;
            }
        }

        return null;
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
            sb.append(tag.getKey());
            sb.append(":");
            sb.append(tagToString(tag.getValue()));
            i++;
        }

        sb.append("}");
        return sb.toString();
    }

    /*
     * ================ toString() Helper Functions ================
     */

    private static String tagToString(Object value) {
        if (value instanceof Byte) {
            // TAG_Byte
            return byteToString((Byte) value);

        } else if (value instanceof Short) {
            // TAG_Short
            return shortToString((Short) value);

        } else if (value instanceof Integer) {
            // TAG_Int
            return intToString((Integer) value);

        } else if (value instanceof Long) {
            // TAG_Long
            return longToString((Long) value);

        } else if (value instanceof Float) {
            // TAG_Float
            return floatToString((Float) value);

        } else if (value instanceof Double) {
            // TAG_Double
            return doubleToString((Double) value);

        } else if (value instanceof Byte[]) {
            // TAG_Byte_Array
            return byteArrayToString((Byte[]) value);

        } else if (value instanceof String) {
            // TAG_String
            return stringTagToString((String) value);

        } else if (value instanceof NBTList) {
            // TAG_List
            return listToString((NBTList) value);

        } else if (value instanceof NBTCompound) {
            // TAG_Compound
            return value.toString();

        } else if (value instanceof Integer[]) {
            // TAG_Int_Array
            return intArrayToString((Integer[]) value);

        } else if (value instanceof Long[]) {
            // TAG_Long_Array
            return longArrayToString((Long[]) value);

        } else {
            // Unknown object
            return "{}";
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

    private static String byteArrayToString(Byte[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("[B;");
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i] != null ? value[i] : 0);
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

    private static String intArrayToString(Integer[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("[I;");
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i] != null ? value[i] : 0);
            if (i < value.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static String longArrayToString(Long[] value) {
        StringBuilder sb = new StringBuilder();
        sb.append("[L;");
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i] != null ? value[i] : 0);
            if (i < value.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}