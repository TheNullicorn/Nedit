package me.nullicorn.nedit;

import static me.nullicorn.nedit.IOTestHelper.TEST_BYTE;
import static me.nullicorn.nedit.IOTestHelper.TEST_DOUBLE;
import static me.nullicorn.nedit.IOTestHelper.TEST_FLOAT;
import static me.nullicorn.nedit.IOTestHelper.TEST_INT;
import static me.nullicorn.nedit.IOTestHelper.TEST_LONG;
import static me.nullicorn.nedit.IOTestHelper.TEST_SHORT;
import static me.nullicorn.nedit.IOTestHelper.TEST_STRING;
import static me.nullicorn.nedit.IOTestHelper.createTestByteArray;
import static me.nullicorn.nedit.IOTestHelper.createTestDoubleList;
import static me.nullicorn.nedit.IOTestHelper.createTestIntArray;
import static me.nullicorn.nedit.IOTestHelper.createTestLongArray;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

        tryWrite(createTestDoubleList(), NBTOutputStream::writeList, (out, list) -> {
            out.writeByte(TagType.DOUBLE.getId());
            out.writeInt(list.size());
            for (int i = 0; i < list.size(); i++) {
                out.writeDouble(list.getDouble(i));
            }
        });
    }

    private <T> void tryWrite(T value, NBTWriterFunction<T> actualWriter, WriterFunction<T> expectedWriter) throws IOException {
        tryWrite(value, actualWriter, expectedWriter, (input, expected, actual) -> {
            assertArrayEquals(
                expected,
                actual,
                "Incorrect encoding for " + value.getClass().getSimpleName());
        });
    }

    private <T> void tryWrite(T value, NBTWriterFunction<T> actualWriter, WriterFunction<T> expectedWriter, WriterAssertionFunction<T> assertionFunc) throws IOException {
        ByteArrayOutputStream actualBytesOut = new ByteArrayOutputStream();
        ByteArrayOutputStream expectedBytesOut = new ByteArrayOutputStream();

        actualWriter.write(new NBTOutputStream(actualBytesOut, false), value);
        expectedWriter.write(new DataOutputStream(expectedBytesOut), value);

        assertionFunc.check(value, expectedBytesOut.toByteArray(), actualBytesOut.toByteArray());
    }

    interface NBTWriterFunction<T> {

        void write(NBTOutputStream out, T value) throws IOException;
    }

    interface WriterFunction<T> {

        void write(DataOutputStream out, T value) throws IOException;
    }

    interface WriterAssertionFunction<T> {

        void check(T expected, byte[] expectedBytes, byte[] actualBytes);
    }
}
