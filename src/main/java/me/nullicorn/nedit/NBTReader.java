package me.nullicorn.nedit;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import me.nullicorn.nedit.exception.NBTParseException;

/**
 * @author Nullicorn
 */
public class NBTReader implements Closeable {

    private final InputStream     originalInputStream;
    private       DataInputStream inputStream;

    public NBTReader(InputStream inputStream) {
        this.originalInputStream = inputStream;
    }

    public NBTCompound read() throws IOException {
        this.inputStream = toDataInputStream(originalInputStream);
        return readCompound();
    }

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

    private NBTCompound readCompound() throws IOException {
        NBTCompound result = new NBTCompound();

        boolean reachedEnd = false;
        while (!reachedEnd) {
            TagType entryType = readTagType();

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

    private List<Object> readList() throws IOException {
        TagType typeOfContents = readTagType();
        if (typeOfContents == null) {
            throw new NBTParseException("Unknown tag ID for TAG_List");
        }

        int length = inputStream.readInt();
        if (length <= 0) {
            return Collections.emptyList();
        }

        List<Object> result = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            result.add(readValue(typeOfContents));
        }
        return result;
    }

    private String readString() throws IOException {
        final char length = inputStream.readChar();

        byte[] stringBytes = new byte[length];
        for (int i = 0; i < stringBytes.length; i++) {
            stringBytes[i] = (byte) inputStream.read();
        }

        return new String(stringBytes);
    }

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

    private TagType readTagType() throws IOException {
        return TagType.fromId(inputStream.read());
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    private static DataInputStream toDataInputStream(InputStream inputStream) throws IOException {
        inputStream = new PushbackInputStream(inputStream);

        int byte1 = inputStream.read();
        int byte2 = inputStream.read();
        inputStream.reset();

        // Check for gzip header (0x1F 0x8B)
        //noinspection ConstantConditions
        if (byte1 == 31 && byte2 == -117) {
            inputStream = new GZIPInputStream(inputStream);
        }

        return new DataInputStream(inputStream);
    }
}