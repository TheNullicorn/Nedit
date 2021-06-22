package me.nullicorn.nedit;

import static me.nullicorn.nedit.IOTestHelper.createTestByteArray;
import static me.nullicorn.nedit.IOTestHelper.createTestCompoundList;
import static me.nullicorn.nedit.IOTestHelper.createTestDoubleList;
import static me.nullicorn.nedit.IOTestHelper.createTestIntArray;
import static me.nullicorn.nedit.IOTestHelper.createTestLongArray;
import static me.nullicorn.nedit.IOTestHelper.streamResource;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NBTInputStreamTests {

    // Appended to the end of each test file so we can check that the correct number of bytes is
    // read for each tag type.
    private static final String TEST_TERMINATOR = "NEDIT";

    @Test
    void shouldCorrectlyDecodePrimitives() throws IOException {
        tryReadPrimitive(TagType.BYTE, Byte.MAX_VALUE, NBTInputStream::readByte);
        tryReadPrimitive(TagType.SHORT, Short.MAX_VALUE, NBTInputStream::readShort);
        tryReadPrimitive(TagType.INT, Integer.MAX_VALUE, NBTInputStream::readInt);
        tryReadPrimitive(TagType.LONG, Long.MAX_VALUE, NBTInputStream::readLong);
        tryReadPrimitive(TagType.FLOAT, Float.MAX_VALUE, NBTInputStream::readFloat);
        tryReadPrimitive(TagType.DOUBLE, Double.MAX_VALUE, NBTInputStream::readDouble);
    }

    @Test
    void shouldCorrectlyDecodeIterables() throws IOException {
        tryRead("iterables/byte_array",
            createTestByteArray(),
            NBTInputStream::readByteArray,
            Assertions::assertArrayEquals);

        tryRead("iterables/int_array",
            createTestIntArray(),
            NBTInputStream::readIntArray,
            Assertions::assertArrayEquals);

        tryRead("iterables/long_array",
            createTestLongArray(),
            NBTInputStream::readLongArray,
            Assertions::assertArrayEquals);

        tryRead("iterables/list_doubles",
            createTestDoubleList(),
            NBTInputStream::readList,
            Assertions::assertEquals);

        tryRead("iterables/list_compounds",
            createTestCompoundList(),
            NBTInputStream::readList,
            Assertions::assertEquals);
    }

    private <T> void tryReadPrimitive(TagType type, T expected, ReaderFunction<T> reader) throws IOException {
        tryRead("primitives/" + type.name().toLowerCase(),
            expected,
            reader,
            Assertions::assertEquals);
    }

    private <T> void tryRead(String testResource, T expected, ReaderFunction<T> reader, BiConsumer<T, T> assertionFunc) throws IOException {
        try (DataInputStream in = streamResource(testResource)) {
            T actual = reader.read(new NBTInputStream(in));
            assertionFunc.accept(expected, actual);

            tryReadTerminator(in);
        }
    }

    private void tryReadTerminator(DataInputStream in) {
        // The first assertion will fail if an EOFException is thrown, indicating the reader read
        // too many bytes.
        // The second fails if the termination string was not read correctly, indicating not enough
        // bytes were read.
        AtomicReference<String> terminator = new AtomicReference<>();
        assertDoesNotThrow(() -> terminator.set(in.readUTF()), "Too many bytes read");
        assertEquals(TEST_TERMINATOR, terminator.get(), "Not enough bytes read");
    }

    private interface ReaderFunction<T> {

        T read(NBTInputStream in) throws IOException;
    }
}
