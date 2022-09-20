package me.nullicorn.nedit;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;
import me.nullicorn.nedit.exception.NBTSerializationException;
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
     * <p>
     * If control over the root tag's name is needed, use {@link #writeFully(NBTCompound, String)}
     * instead.
     *
     * @throws IOException If the compound could not be written
     * @see #writeFully(NBTCompound, String)
     */
    public void writeFully(NBTCompound compound) throws IOException {
        writeFully(compound, /* rootCompoundName = */ "");
    }

    /**
     * Serialize an NBT compound and write it to the output stream
     *
     * @param rootCompoundName The name that will be given to the root compound tag. In most cases
     *                         this does not matter, and {@link #writeFully(NBTCompound)} can be
     *                         used instead
     * @throws IOException If the compound could not be written
     * @see #writeFully(NBTCompound)
     */
    public void writeFully(NBTCompound compound, String rootCompoundName) throws IOException {
        if (compound == null) {
            writeTagType(TagType.END);
        } else {
            writeTagType(TagType.COMPOUND);
            writeString(rootCompoundName);
            writeCompound(compound);

            if (out instanceof GZIPOutputStream) {
                ((GZIPOutputStream) out).finish();
            }
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
                break;

            case SHORT:
                writeShort((Short) value);
                break;

            case INT:
                writeInt((Integer) value);
                break;

            case LONG:
                writeLong((Long) value);
                break;

            case FLOAT:
                writeFloat((Float) value);
                break;

            case DOUBLE:
                writeDouble((Double) value);
                break;

            case STRING:
                writeString((String) value);
                break;

            case LIST:
                writeList((NBTList) value);
                break;

            case COMPOUND:
                writeCompound((NBTCompound) value);
                break;

            case BYTE_ARRAY:
                writeByteArray((byte[]) value);
                break;

            case INT_ARRAY:
                writeIntArray((int[]) value);
                break;

            case LONG_ARRAY:
                writeLongArray((long[]) value);
                break;

            case END:
                throw new NBTSerializationException(
                    "Tag " + tagType + " cannot be written as a value");
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
     * @param close Whether or not the compound should be closed via a {@link TagType#END}
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
