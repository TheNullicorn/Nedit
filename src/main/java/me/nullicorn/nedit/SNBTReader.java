package me.nullicorn.nedit;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.nullicorn.nedit.exception.NBTParseException;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class for reading stringified NBT data (SNBT)
 * <p>
 * For converting NBT data in base64 string format, see {@link NBTReader#readBase64(String)}
 *
 * @author Nullicorn
 */
public final class SNBTReader {

    private static final char COMPOUND_START = '{';
    private static final char COMPOUND_END   = '}';

    private static final char ENTRY_VALUE_INDICATOR = ':';
    private static final char ENTRY_SEPARATOR       = ','; // Used in compounds, lists, and arrays

    private static final char ARRAY_START          = '['; // Used in arrays and lists
    private static final char ARRAY_END            = ']'; // Used in arrays and lists
    private static final char ARRAY_TYPE_INDICATOR = ';';

    private static final char STRING_DELIMITER_1 = '\"';
    private static final char STRING_DELIMITER_2 = '\'';
    private static final char STRING_ESCAPE      = '\\';

    private static final String BYTE_PATTERN   = "^[+-]?\\d+[Bb]$";
    private static final String SHORT_PATTERN  = "^[+-]?\\d+[Ss]$";
    private static final String INT_PATTERN    = "^[+-]?\\d+$";
    private static final String LONG_PATTERN   = "^[+-]?\\d+[Ll]$";
    private static final String FLOAT_PATTERN  = "^[+-]?[0-9]*\\.?[0-9]+[Ff]$";
    private static final String DOUBLE_PATTERN = "^[+-]?[0-9]*\\.?[0-9]+[Dd]$";

    /**
     * Used to find and delete suffixes from numeric literals.
     */
    private static final String LITERAL_SUFFIX_PATTERN = "[BbDdFfLlSs]$";

    /**
     * All characters that can be used in strings without quotation marks (including tag names).
     */
    private static final String VALID_UNQUOTED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-+_.";

    /**
     * Read an NBT value from an SNBT (stringified NBT) string.
     * <p>
     * If you are sure that your data will be an {@link NBTCompound} or {@link NBTList}, you may use
     * {@link #readCompound(String)} and {@link #readList(String)} respectively. Unlike those
     * methods, this one will directly parse and return SNBT literals (e.g. 1b, 12.34F, "Hello
     * World", 450s, etc) and array types.
     *
     * @param snbt The text to parse into NBT
     * @return The parsed NBT value (type will vary)
     * @throws IOException If the input string cannot be read or is not valid SNBT
     */
    @NotNull
    public static Object read(@NotNull String snbt) throws IOException {
        return read(new StringReader(snbt.trim()));
    }

    /**
     * Read an NBT compound from an SNBT (stringified NBT) string.
     *
     * @param snbt The text to parse into NBT
     * @return The parsed NBT compound
     * @throws IOException If the input string cannot be read or is not a valid SNBT compound
     */
    @NotNull
    public static NBTCompound readCompound(@NotNull String snbt) throws IOException {
        return readCompound(new StringReader(snbt.trim()));
    }

    /**
     * Read an NBT list from an SNBT (stringified NBT) string.
     *
     * @param snbt The text to parse into NBT
     * @return The parsed NBT list
     * @throws IOException If the input string cannot be read or is not a valid SNBT list
     */
    @NotNull
    public static NBTList readList(@NotNull String snbt) throws IOException {
        return readList(new StringReader(snbt.trim()));
    }

    /**
     * Read an SNBT value with an unknown type from the current index of a reader
     */
    private static Object read(@NonNull Reader reader) throws IOException {
        final int firstChar = peekChar(reader);

        switch (firstChar) {
            case COMPOUND_START:
                return readCompound(reader);

            case ARRAY_START:
                return readIterable(reader);

            default:
                return readLiteral(reader);
        }
    }

    /**
     * Read an SNBT compound from the current index of a reader
     */
    private static NBTCompound readCompound(@NonNull Reader reader) throws IOException {
        NBTCompound compound = new NBTCompound();

        if (readChar(reader) != COMPOUND_START) {
            throw new NBTParseException("Invalid start of SNBT TAG_Compound");
        }

        do {
            skipWhitespace(reader);

            // Check if we've reached the end of the compound.
            reader.mark(1);
            if (readChar(reader) == COMPOUND_END) {
                break;
            }
            reader.reset();

            // Read the entry's key/name.
            String key = readString(reader);

            // Ensure there's a colon between the key and value.
            skipWhitespace(reader);
            if (readChar(reader) != ENTRY_VALUE_INDICATOR) {
                throw new NBTParseException("Invalid value indicator in SNBT TAG_Compound");
            }
            skipWhitespace(reader);

            // Read the value and add it to the returned compound.
            compound.put(key, read(reader));
            skipWhitespace(reader);
        } while (readChar(reader) == ENTRY_SEPARATOR);

        return compound;
    }

    /**
     * Read an SNBT iterable type from the current index of a reader
     * <p>
     * If the type is known to be a list, prefer {@link #readList(Reader)}
     */
    private static Object readIterable(@NonNull Reader reader) throws IOException {
        reader.mark(3);

        if (readChar(reader) != ARRAY_START) {
            throw new NBTParseException("Invalid start of SNBT iterable");
        }

        int secondChar = readChar(reader);
        int thirdChar = readChar(reader);

        final TagType arrayType;

        if (thirdChar == ARRAY_TYPE_INDICATOR) {
            switch (secondChar) {
                // TODO: Add constants for these
                case 'B':
                    arrayType = TagType.BYTE_ARRAY;
                    break;

                case 'I':
                    arrayType = TagType.INT_ARRAY;
                    break;

                case 'L':
                    arrayType = TagType.LONG_ARRAY;
                    break;

                default:
                    throw new NBTParseException("Unknown SNBT array type");
            }
        } else {
            reader.reset();
            return readList(reader);
        }

        List<Object> values = new ArrayList<>();
        do {
            skipWhitespace(reader);

            // Check if we've reached the end of the array.
            reader.mark(1);
            if (readChar(reader) == ARRAY_END) {
                break;
            }
            reader.reset();

            // Read the next value from the array.
            Object value = readLiteral(reader);

            // Ensure that the value's type matches that of the array itself.
            if (arrayType == TagType.BYTE_ARRAY && value instanceof Byte
                || arrayType == TagType.INT_ARRAY && value instanceof Integer
                || arrayType == TagType.LONG_ARRAY && value instanceof Long) {
                values.add(value);
            } else {
                throw new NBTParseException("Mismatch between SNBT array and element types");
            }

            skipWhitespace(reader);
        } while (readChar(reader) == ENTRY_SEPARATOR);

        // Unwrap the value list into a primitive array.
        final Object result = Array
            .newInstance(arrayType.getClazz().getComponentType(), values.size());
        for (int i = 0; i < values.size(); i++) {
            Array.set(result, i, values.get(i));
        }
        return result;
    }

    /**
     * Read an SNBT list from the current index of a reader
     */
    private static NBTList readList(@NonNull Reader reader) throws IOException {
        if (readChar(reader) != ARRAY_START) {
            throw new NBTParseException("Invalid start of SNBT list");
        }

        NBTList list = null;
        do {
            skipWhitespace(reader);

            // Check if we've reached the end of the list.
            reader.mark(1);
            if (readChar(reader) == ARRAY_END) {
                if (list == null) {
                    return new NBTList(TagType.END);
                }
                break;
            }
            reader.reset();

            // Read the next value from the list.
            Object entry = read(reader);

            if (list == null) {
                // Create a new list using the tag type of the first entry.
                list = new NBTList(TagType.fromObject(entry));
                if (list.getContentType() == TagType.END) {
                    throw new NBTParseException("SNBT list entry has unrecognized type");
                }
                list.add(entry);
            } else {
                // Add the entry to the existing list.
                list.add(entry);
            }
        } while (readChar(reader) == ENTRY_SEPARATOR);

        return list;
    }

    /**
     * Read any SNBT literal type from the current index of a reader
     * <p>
     * If the value is known to be a string, prefer {@link #readString(Reader)}
     */
    private static Object readLiteral(@NonNull Reader reader) throws IOException {
        // Check if the value is in quotes.
        int firstChar = peekChar(reader);
        boolean isQuoted = firstChar == STRING_DELIMITER_1 || firstChar == STRING_DELIMITER_2;

        String asString = readString(reader);

        // Always use the string type for text in quotes.
        if (isQuoted) {
            return asString;
        }

        String withoutSuffix = asString.replaceFirst(LITERAL_SUFFIX_PATTERN, "");

        // Try to parse the string as a numeric value.
        if (asString.matches(INT_PATTERN)) {
            return Integer.parseInt(withoutSuffix);

        } else if (asString.matches(DOUBLE_PATTERN)) {
            return Double.parseDouble(withoutSuffix);

        } else if (asString.matches(BYTE_PATTERN)) {
            return Byte.parseByte(withoutSuffix);

        } else if (asString.matches(SHORT_PATTERN)) {
            return Short.parseShort(withoutSuffix);

        } else if (asString.matches(LONG_PATTERN)) {
            return Long.parseLong(withoutSuffix);

        } else if (asString.matches(FLOAT_PATTERN)) {
            return Float.parseFloat(withoutSuffix);

        } else {
            // Fall-back to string value.
            return asString;
        }
    }

    /**
     * Read an SNBT string from the current index of a reader
     */
    private static String readString(@NonNull Reader reader) throws IOException {
        final StringBuilder valueBuilder = new StringBuilder();

        final int firstChar = reader.read();
        int lastChar;

        // Check if the string is quoted.
        if (firstChar == STRING_DELIMITER_1 || firstChar == STRING_DELIMITER_2) {
            boolean isEscaped = false;
            while ((lastChar = reader.read()) != firstChar || isEscaped) {
                valueBuilder.append((char) lastChar);
                isEscaped = lastChar == STRING_ESCAPE;
            }
        } else {
            valueBuilder.append((char) firstChar);
            reader.mark(1);
            while (VALID_UNQUOTED_CHARS.indexOf(lastChar = reader.read()) != -1) {
                valueBuilder.append((char) lastChar);
                reader.mark(1);
            }
            reader.reset();
        }

        String value = valueBuilder.toString();

        // Only trim whitespace if the string was NOT quoted.
        if (firstChar != STRING_DELIMITER_1 && firstChar != STRING_DELIMITER_2) {
            value = value.trim();
        }

        return value;
    }

    /**
     * Skip over zero or more whitespace characters at the current index of a reader
     */
    private static void skipWhitespace(@NonNull Reader reader) throws IOException {
        do {
            reader.mark(1);
        } while (Character.isWhitespace(reader.read()));
        reader.reset();
    }

    /**
     * Read a single character from the provided reader without increasing its index
     */
    private static int peekChar(@NonNull Reader reader) throws IOException {
        reader.mark(1);
        int value = reader.read();
        reader.reset();
        if (value == -1) {
            throw new IOException("Unexpected end of SNBT string");
        }
        return value;
    }

    /**
     * Read a single character from the provided reader.
     * <p>
     * To read without increasing the reader's index, see {@link #peekChar(Reader)}.
     */
    private static int readChar(@NonNull Reader reader) throws IOException {
        int value = reader.read();
        if (value == -1) {
            throw new IOException("Unexpected end of SNBT string");
        }
        return value;
    }

    private SNBTReader() {
        // Prevent instantiation of this class
    }
}
