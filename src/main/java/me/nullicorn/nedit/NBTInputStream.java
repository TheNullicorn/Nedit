package me.nullicorn.nedit;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.GZIPInputStream;
import me.nullicorn.nedit.exception.NBTParseException;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;

/**
 * An InputStream that reads and deserializes binary data in the <a href=https://wiki.vg/NBT>NBT
 * format</a>
 *
 * @author Nullicorn
 */
public class NBTInputStream extends DataInputStream {

    private final boolean internNames;
    private final boolean internValues;

    /**
     * Same as {@link NBTInputStream#NBTInputStream(InputStream, boolean, boolean)}, but all
     * interning is disabled by default
     *
     * @see #NBTInputStream(InputStream, boolean, boolean)
     */
    public NBTInputStream(InputStream inputStream) {
        this(inputStream, false, false);
    }

    /**
     * Constructs a stream for reading NBT data, with control over interning of parsed strings. When
     * reading lots of data with similar structure and tag names, enabling these features may free
     * up significant amounts of energy.
     *
     * @param inputStream  An input stream of valid NBT data; may also be gzipped
     * @param internNames  Whether or not {@link #readCompound()} will use interned tag names
     * @param internValues Whether or not {@link #readValue(TagType) readValue()} will intern {@link
     *                     TagType#STRING} values. This effect also propagates to values in {@link
     *                     #readCompound() compounds} and elements in {@link #readList() lists of
     *                     strings}
     * @see String#intern()
     */
    public NBTInputStream(InputStream inputStream, boolean internNames, boolean internValues) {
        super(inputStream);
        this.internNames = internNames;
        this.internValues = internValues;
    }

    /**
     * Read an NBT compound from the inputStream
     *
     * @return A root TAG_Compound containing the InputStream's NBT data
     * @throws IOException If the inputStream could not be properly read as NBT data
     */
    public NBTCompound readFully() throws IOException {
        gunzipIfNecessary();
        TagType rootType = readTagId();
        if (rootType == TagType.END) {
            return new NBTCompound();
        } else if (rootType != TagType.COMPOUND) {
            throw new IOException("Expected COMPOUND at NBT root, but got " + rootType);
        }
        readString(); // Skip root name; typically empty anyways.
        return readCompound();
    }

    /**
     * Read a TAG_Compound from the inputStream
     *
     * @throws IOException If the compound could not be read or did not contain valid NBT data
     */
    public NBTCompound readCompound() throws IOException {
        NBTCompound result = new NBTCompound();

        boolean reachedEnd = false;
        while (!reachedEnd) {
            TagType entryType = readTagId();

            if (entryType == null) {
                throw new NBTParseException("Unknown tag ID for TAG_Compound");

            } else if (entryType == TagType.END) {
                reachedEnd = true;
                continue;
            }

            String entryName = readString(internNames);
            Object entryValue = readValue(entryType);
            result.put(entryName, entryValue);
        }

        return result;
    }

    /**
     * Read a TAG_List from the inputStream
     *
     * @throws IOException If the list could not be read or did not contain valid NBT data
     */
    public NBTList readList() throws IOException {
        TagType typeOfContents = readTagId();
        if (typeOfContents == null) {
            throw new NBTParseException("Unknown tag ID for TAG_List");
        }

        int length = readInt();
        if (length <= 0) {
            return new NBTList(typeOfContents);
        }

        NBTList result = new NBTList(typeOfContents);
        for (int i = 0; i < length; i++) {
            result.add(readValue(typeOfContents));
        }
        return result;
    }

    /**
     * Same as {@link #readString(boolean)}, but the resulting string will never be interned.
     *
     * @see #readString(boolean)
     * @see String#intern()
     */
    @SuppressWarnings("UnusedReturnValue")
    public String readString() throws IOException {
        return readString(false);
    }

    /**
     * Read a length-prefixed string from the inputStream
     *
     * @param intern Whether or not the string's {@link String#intern() interned} value will be
     *               returned. When deserializing lots of NBT data with the same properties, setting
     *               this to {@code true} can significantly lower memory consumption.
     * @throws IOException If the string could not be read or was not valid NBT data
     * @see String#intern()
     */
    public String readString(boolean intern) throws IOException {
        String utf = readUTF();
        if (intern) {
            return utf.intern();
        }
        return utf;
    }

    /**
     * Read a TAG_Long_Array from the inputStream
     *
     * @throws IOException If the long array could not be read or was not valid NBT data
     */
    public long[] readLongArray() throws IOException {
        int length = readInt();
        if (length < 0) {
            throw new NBTParseException(
                new NegativeArraySizeException(
                    "TAG_Long_Array was prefixed with a negative length"));
        } else if (length == 0) {
            return new long[0];
        }

        byte[] bytes = new byte[length * 8];
        readFully(bytes);
        int byteIndex = 0;

        long[] longArray = new long[length];
        for (int i = 0; i < length; i++, byteIndex += 8) {
            long element = 0;
            int bitOffset = 56;
            for (int b = 0; b < 8; b++, bitOffset -= 8) {
                element |= ((long) (bytes[byteIndex + b] & 0xFF)) << bitOffset;
            }
            longArray[i] = element;
        }
        return longArray;
    }

    /**
     * Read a TAG_Int_Array from the inputStream
     *
     * @throws IOException If the integer array could not be read or was not valid NBT data
     */
    public int[] readIntArray() throws IOException {
        int length = readInt();
        if (length < 0) {
            throw new NBTParseException(
                new NegativeArraySizeException(
                    "TAG_Int_Array was prefixed with a negative length"));
        }

        byte[] bytes = new byte[length * 4];
        readFully(bytes);
        int byteIndex = 0;

        int[] intArray = new int[length];
        for (int i = 0; i < length; i++, byteIndex += 4) {
            int element = 0;
            int bitOffset = 24;
            for (int b = 0; b < 4; b++, bitOffset -= 4) {
                element |= (bytes[byteIndex + b] & 0xFF) << bitOffset;
            }
            intArray[i] = element;
        }

        return intArray;
    }

    /**
     * Read a TAG_Byte_Array from the inputStream
     *
     * @throws IOException If the byte array could not be read or was not valid NBT data
     */
    public byte[] readByteArray() throws IOException {
        int length = readInt();
        if (length < 0) {
            throw new NBTParseException(
                new NegativeArraySizeException(
                    "TAG_Byte_Array was prefixed with a negative length"));
        }

        byte[] bytes = new byte[length];
        readFully(bytes);

        return bytes;
    }

    /**
     * Read an NBT tag ID from the inputStream
     *
     * @throws IOException If the tag ID could not be read
     */
    public TagType readTagId() throws IOException {
        return TagType.fromId(read());
    }

    /**
     * Read a NBT value from the inputStream as the specified type
     *
     * @throws IOException If the value could not be read or was not valid NBT data
     */
    public Object readValue(TagType tagType) throws IOException {
        switch (tagType) {
            case BYTE:
                return readByte();

            case SHORT:
                return readShort();

            case INT:
                return readInt();

            case LONG:
                return readLong();

            case FLOAT:
                return readFloat();

            case DOUBLE:
                return readDouble();

            case BYTE_ARRAY:
                return readByteArray();

            case STRING:
                return readString(internValues);

            case LIST:
                return readList();

            case COMPOUND:
                return readCompound();

            case INT_ARRAY:
                return readIntArray();

            case LONG_ARRAY:
                return readLongArray();

            default:
                return null;
        }
    }

    /**
     * Check if the underlying InputStream contains gzipped data. If it does, it is wrapped in a
     * {@link GZIPInputStream}
     */
    public synchronized void gunzipIfNecessary() throws IOException {
        this.in = new PushbackInputStream(this.in, 2);

        byte byte1 = (byte) read();
        byte byte2 = (byte) read();
        ((PushbackInputStream) this.in).unread(new byte[]{byte1, byte2});

        // Check for gzip header (0x1F8B)
        if (byte1 == 31 && byte2 == -117) {
            this.in = new GZIPInputStream(this.in);
        }
    }
}