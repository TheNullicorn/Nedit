package me.nullicorn.nedit;

import static me.nullicorn.nedit.IOTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.api.Test;

class NBTOutputStreamTests {

    @Test
    void shouldEncodePrimitivesCorrectly() throws IOException {
        tryWrite((int) TEST_BYTE, NBTOutputStream::writeByte, DataOutputStream::writeByte);
        tryWrite((int) TEST_SHORT, NBTOutputStream::writeShort, DataOutputStream::writeShort);
        tryWrite(TEST_INT, NBTOutputStream::writeInt, DataOutputStream::writeInt);
        tryWrite(TEST_LONG, NBTOutputStream::writeLong, DataOutputStream::writeLong);
        tryWrite(TEST_FLOAT, NBTOutputStream::writeFloat, DataOutputStream::writeFloat);
        tryWrite(TEST_DOUBLE, NBTOutputStream::writeDouble, DataOutputStream::writeDouble);
        tryWrite(TEST_STRING, NBTOutputStream::writeString, DataOutputStream::writeUTF);
    }

    @Test
    void shouldEncodeIterablesCorrectly() throws IOException {
        tryWrite(createTestByteArray(), NBTOutputStream::writeByteArray, (out, array) -> {
            out.writeInt(array.length);
            out.write(array);
        });

        tryWrite(createTestIntArray(), NBTOutputStream::writeIntArray, (out, array) -> {
            out.writeInt(array.length);
            for (int value : array) {
                out.writeInt(value);
            }
        });

        tryWrite(createTestLongArray(), NBTOutputStream::writeLongArray, (out, array) -> {
            out.writeInt(array.length);
            for (long value : array) {
                out.writeLong(value);
            }
        });

        tryWrite(createTestEmptyList(), NBTOutputStream::writeList, (out, list) -> {
            out.writeByte(TagType.END.getId());
            out.writeInt(0);
        });

        tryWrite(createTestDoubleList(), NBTOutputStream::writeList, (out, list) -> {
            out.writeByte(TagType.DOUBLE.getId());
            out.writeInt(list.size());
            for (int i = 0; i < list.size(); i++) {
                out.writeDouble(list.getDouble(i));
            }
        });
    }

    @Test
    void shouldEncodeEmptyCompoundsCorrectly() throws IOException {
        tryWrite(new NBTCompound(), NBTOutputStream::writeCompound,
            (out, compound) -> out.writeByte(TagType.END.getId()));
    }

    @Test
    void shouldEncodeCompoundsCorrectly() throws IOException {
        NBTCompound expected = new NBTCompound();
        NBTCompound nested = new NBTCompound();
        nested.put("nested_string", TEST_STRING);
        expected.put("byte", TEST_BYTE);
        expected.put("int", TEST_INT);
        expected.put("string", TEST_STRING);
        expected.put("double", TEST_DOUBLE);
        expected.put("compound", nested);
        expected.put("byte_array", createTestByteArray());

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        new NBTOutputStream(bytesOut, false).writeCompound(expected);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytesOut.toByteArray()));
        int tagId;
        NBTCompound actual = new NBTCompound();
        while ((tagId = in.readByte()) != TagType.END.getId()) {
            assertNotEquals(-1, tagId, "Compound ended unexpectedly");

            TagType type = TagType.fromId(tagId);
            assertNotNull(type, "Child tag had an invalid type (id=" + tagId + ")");

            String name = in.readUTF();
            assertFalse(name.isEmpty(), "Unexpected empty tag name");
            assertTrue(expected.containsTag(name, type));

            Object value;
            switch (name) {
                case "byte":
                    value = in.readByte();
                    break;
                case "int":
                    value = in.readInt();
                    break;
                case "string":
                    value = in.readUTF();
                    break;
                case "double":
                    value = in.readDouble();
                    break;
                case "byte_array":
                    int length = in.readInt();
                    value = new byte[length];
                    in.readFully((byte[]) value);
                    break;
                case "compound":
                    int nestedTagId = in.readByte();
                    assertEquals(TagType.STRING.getId(), nestedTagId, "Incorrect nested tag ID");
                    assertEquals("nested_string", in.readUTF(), "Incorrect nested value");
                    assertEquals(TEST_STRING, in.readUTF());
                    assertEquals(TagType.END.getId(), in.readByte(), "Nested compound not closed");

                    // If those assertions pass ^, the nested value is known to be valid.
                    value = nested;
                    break;
                default:
                    throw new IOException("Unexpected tag \"" + name + "\" with ID " + tagId);
            }

            actual.put(name, value);
        }

        assertEquals(-1, in.read(), "Written compound continues unexpectedly");
        assertEquals(expected, actual);
    }

    private <T> void tryWrite(T value, NBTWriterFunction<T> actualWriter, WriterFunction<T> expectedWriter) throws IOException {
        ByteArrayOutputStream actualBytesOut = new ByteArrayOutputStream();
        ByteArrayOutputStream expectedBytesOut = new ByteArrayOutputStream();

        actualWriter.write(new NBTOutputStream(actualBytesOut, false), value);
        expectedWriter.write(new DataOutputStream(expectedBytesOut), value);

        assertArrayEquals(
            expectedBytesOut.toByteArray(),
            actualBytesOut.toByteArray(),
            "Incorrect encoding for " + value.getClass().getSimpleName());
    }

    private interface NBTWriterFunction<T> {

        void write(NBTOutputStream out, T value) throws IOException;
    }

    private interface WriterFunction<T> {

        void write(DataOutputStream out, T value) throws IOException;
    }
}
