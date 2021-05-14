package me.nullicorn.nedit;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;

/**
 * An OutputStream for serializing data in the <a href=https://wiki.vg/NBT>NBT format</a>
 *
 * @author Nullicorn
 */
public class NBTOutputStream extends DataOutputStream {

    public NBTOutputStream(OutputStream out, boolean compress) throws IOException {
        super(compress ? new GZIPOutputStream(out) : out);
    }

    /**
     * Serialize an NBT compound and write it to the output stream
     *
     * @throws IOException If the compound could not be written
     */
    public void writeFully(NBTCompound compound) throws IOException {
        writeCompound(compound, false);
        if (out instanceof GZIPOutputStream) {
            ((GZIPOutputStream) out).finish();
        }
    }

    /**
     * Write a tag ID (1 byte) to the stream
     *
     * @throws IOException If the tag ID could not be written
     */
    public void writeTagType(TagType type) throws IOException {
        if (type == null) {
            writeTagType(TagType.END);
            return;
        }
        writeByte((byte) type.getId());
    }

    /**
     * Write a value to the stream without knowing its type
     *
     * @throws IOException If the value could not be written for an io-related reason
     */
    public void writeValue(Object value) throws IOException {
        TagType tagType = TagType.fromObject(value);
        switch (tagType) {
            case BYTE:
                writeByte((Byte) value);
                return;

            case SHORT:
                writeShort((Short) value);
                return;

            case INT:
                writeInt((Integer) value);
                return;

            case LONG:
                writeLong((Long) value);
                return;

            case FLOAT:
                writeFloat((Float) value);
                return;

            case DOUBLE:
                writeDouble((Double) value);
                return;

            case STRING:
                writeString((String) value);
                return;

            case LIST:
                writeList((NBTList) value);
                return;

            case COMPOUND:
                writeCompound((NBTCompound) value);
                return;

            case BYTE_ARRAY:
                writeByteArray((byte[]) value);
                return;

            case INT_ARRAY:
                writeIntArray((int[]) value);
                return;

            case LONG_ARRAY:
                writeLongArray((long[]) value);
        }
    }

    /**
     * Same as {@link #writeCompound(NBTCompound, boolean)}, but with {@code close} set to {@literal
     * true}.
     *
     * @see #writeCompound(NBTCompound, boolean)
     */
    public void writeCompound(NBTCompound compound) throws IOException {
        writeCompound(compound, true);
    }

    /**
     * Write a compound tag to the stream
     *
     * @param close Whether or not the compound should be closed via a {@link TagType#END}. This
     *              should be true for any compound except the root.
     * @throws IOException If the compound could not be written
     */
    public void writeCompound(NBTCompound compound, boolean close) throws IOException {
        for (Entry<String, Object> tag : compound.entrySet()) {
            writeTagType(TagType.fromObject(tag.getValue())); // Tag type
            writeString(tag.getKey()); // Tag name
            writeValue(tag.getValue()); // Tag value
        }
        if (close) {
            writeTagType(TagType.END);
        }
    }

    /**
     * Write a length-prefixed list of tags (all of the same type) to the stream
     *
     * @throws IOException If the list could not be written
     */
    public void writeList(NBTList list) throws IOException {
        if (list == null) {
            writeTagType(TagType.END);
            writeInt(0); // Length of zero
            return;
        }

        writeTagType(list.getContentType()); // Type of list contents
        writeInt(list.size()); // Size of lise
        for (Object item : list) { // List items
            writeValue(item);
        }
    }

    /**
     * Write a length-prefixed long array to the stream
     *
     * @throws IOException If the value could not be written
     */
    public void writeLongArray(long[] longs) throws IOException {
        if (longs == null) {
            writeInt(0); // Length of zero
            return;
        }

        writeInt(longs.length);
        for (Long item : longs) {
            writeLong(item);
        }
    }

    /**
     * Write a length-prefixed integer array to the stream
     *
     * @throws IOException If the value could not be written
     */
    public void writeIntArray(int[] ints) throws IOException {
        if (ints == null) {
            writeInt(0); // Length of zero
            return;
        }

        writeInt(ints.length);
        for (Integer item : ints) {
            writeInt(item);
        }
    }

    /**
     * Write a length-prefixed byte array to the stream
     *
     * @throws IOException If the value could not be written
     */
    public void writeByteArray(byte[] bytes) throws IOException {
        if (bytes == null) {
            writeInt(0); // Length of zero
            return;
        }

        writeInt(bytes.length);
        for (Byte item : bytes) {
            writeByte(item);
        }
    }

    /**
     * Write an length-prefixed string to the stream
     *
     * @throws IOException If the value could not be written
     */
    public void writeString(String value) throws IOException {
        if (value == null) {
            writeInt(0); // Length of zero
            return;
        }

        byte[] strBytes = value.getBytes(StandardCharsets.UTF_8);
        writeUnsignedShort(strBytes.length);
        write(strBytes);
    }

    /**
     * Write an unsigned short (2 bytes) to the stream
     *
     * @throws IOException If the value could not be written
     */
    protected void writeUnsignedShort(int value) throws IOException {
        writeChar(value);
    }
}
