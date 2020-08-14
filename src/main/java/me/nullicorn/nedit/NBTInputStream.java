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
 * An InputStream that reads and deserializes binary data in the <a href=https://wiki.vg/NBT>NBT format</a>
 *
 * @author Nullicorn
 */
public class NBTInputStream extends DataInputStream {

    /**
     * @param inputStream An inputStream containing NBT data for this reader to read; may be gzipped
     */
    public NBTInputStream(InputStream inputStream) {
        super(inputStream);
    }

    /**
     * Read an NBT compound from the inputStream
     *
     * @return A root TAG_Compound containing the InputStream's NBT data
     * @throws IOException If the inputStream could not be properly read as NBT data
     */
    public NBTCompound readFully() throws IOException {
        gunzipIfNecessary();
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

            String entryKey = readString();
            Object entryValue = readValue(entryType);
            result.put(entryKey, entryValue);
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
     * Read a length-prefixed string from the inputStream
     *
     * @throws IOException If the string could not be read or was not valid NBT data
     */
    public String readString() throws IOException {
        final char length = readChar();

        byte[] stringBytes = new byte[length];
        for (int i = 0; i < stringBytes.length; i++) {
            stringBytes[i] = (byte) read();
        }

        return new String(stringBytes);
    }

    /**
     * Read a TAG_Long_Array from the inputStream
     *
     * @throws IOException If the long array could not be read or was not valid NBT data
     */
    public long[] readLongArray() throws IOException {
        int length = readInt();
        if (length < 0) {
            throw new IndexOutOfBoundsException("TAG_Long_Array was prefixed with a negative length");
        }

        long[] longArray = new long[length];
        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = readLong();
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
            throw new IndexOutOfBoundsException("TAG_Int_Array was prefixed with a negative length");
        }

        int[] intArray = new int[length];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = readInt();
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
            throw new IndexOutOfBoundsException("TAG_Byte_Array array was prefixed with a negative length");
        }

        byte[] byteArray = new byte[length];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = readByte();
        }

        return byteArray;
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
                return readString();

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
     * Check if the underlying InputStream contains gzipped data. If it does, it is wrapped in a {@link GZIPInputStream}
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