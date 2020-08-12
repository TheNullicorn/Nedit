package me.nullicorn.nedit;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import me.nullicorn.nedit.exception.NBTParseException;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;

/**
 * A class for obtaining readable NBT data from its raw (binary) form
 *
 * @author Nullicorn
 */
public class NBTReader implements Closeable {

    private final InputStream     originalInputStream;
    private       DataInputStream inputStream;

    /**
     * @param base64 The base64 NBT data for this reader to read; may be gzipped
     */
    public NBTReader(String base64) {
        this(new ByteArrayInputStream(Base64.getDecoder().decode(base64)));
    }

    /**
     * @param inputStream An inputStream containing NBT data for this reader to read; may be gzipped
     */
    public NBTReader(InputStream inputStream) {
        this.originalInputStream = inputStream;
    }

    /**
     * Read an NBT compound from the inputStream
     *
     * @return A root TAG_Compound containing the InputStream's NBT data
     * @throws IOException If the inputStream could not be properly read as NBT data
     */
    public NBTCompound read() throws IOException {
        this.inputStream = toDataInputStream(originalInputStream);
        return readCompound();
    }

    /**
     * Read a TAG_Compound from the inputStream
     *
     * @throws IOException If the compound could not be read or did not contain valid NBT data
     */
    private NBTCompound readCompound() throws IOException {
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
    private NBTList readList() throws IOException {
        TagType typeOfContents = readTagId();
        if (typeOfContents == null) {
            throw new NBTParseException("Unknown tag ID for TAG_List");
        }

        int length = inputStream.readInt();
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
    private String readString() throws IOException {
        final char length = inputStream.readChar();

        byte[] stringBytes = new byte[length];
        for (int i = 0; i < stringBytes.length; i++) {
            stringBytes[i] = (byte) inputStream.read();
        }

        return new String(stringBytes);
    }

    /**
     * Read a TAG_Long_Array from the inputStream
     *
     * @throws IOException If the long array could not be read or was not valid NBT data
     */
    private long[] readLongArray() throws IOException {
        int length = inputStream.readInt();
        if (length < 0) {
            throw new IndexOutOfBoundsException("TAG_Long_Array was prefixed with a negative length");
        }

        long[] longArray = new long[length];
        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = inputStream.readLong();
        }

        return longArray;
    }

    /**
     * Read a TAG_Int_Array from the inputStream
     *
     * @throws IOException If the integer array could not be read or was not valid NBT data
     */
    private int[] readIntArray() throws IOException {
        int length = inputStream.readInt();
        if (length < 0) {
            throw new IndexOutOfBoundsException("TAG_Int_Array was prefixed with a negative length");
        }

        int[] intArray = new int[length];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = inputStream.readInt();
        }

        return intArray;
    }

    /**
     * Read a TAG_Byte_Array from the inputStream
     *
     * @throws IOException If the byte array could not be read or was not valid NBT data
     */
    private byte[] readByteArray() throws IOException {
        int length = inputStream.readInt();
        if (length < 0) {
            throw new IndexOutOfBoundsException("TAG_Byte_Array array was prefixed with a negative length");
        }

        byte[] byteArray = new byte[length];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = inputStream.readByte();
        }

        return byteArray;
    }

    /**
     * Read an NBT tag ID from the inputStream
     *
     * @throws IOException If the tag ID could not be read
     */
    private TagType readTagId() throws IOException {
        return TagType.fromId(inputStream.read());
    }

    /**
     * Read a NBT value from the inputStream as the specified type
     *
     * @throws IOException If the value could not be read or was not valid NBT data
     */
    private Object readValue(TagType tagType) throws IOException {
        switch (tagType) {
            case BYTE:
                return inputStream.readByte();

            case SHORT:
                return inputStream.readShort();

            case INT:
                return inputStream.readInt();

            case LONG:
                return inputStream.readLong();

            case FLOAT:
                return inputStream.readFloat();

            case DOUBLE:
                return inputStream.readDouble();

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

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    /**
     * Wrap the provided InputStream in a DataInputStream, and gunzip if necessary
     *
     * @throws IOException If the GZip header could not be read
     */
    private static DataInputStream toDataInputStream(InputStream inputStream) throws IOException {
        inputStream = new PushbackInputStream(inputStream, 2);

        byte byte1 = (byte) inputStream.read();
        byte byte2 = (byte) inputStream.read();
        ((PushbackInputStream) inputStream).unread(new byte[]{byte1, byte2});

        // Check for gzip header (0x1F 0x8B)
        if (byte1 == 31 && byte2 == -117) {
            inputStream = new GZIPInputStream(inputStream);
        }

        return new DataInputStream(inputStream);
    }
}