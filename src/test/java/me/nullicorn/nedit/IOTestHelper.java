package me.nullicorn.nedit;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;

public final class IOTestHelper {

    public static final byte   TEST_BYTE   = Byte.MAX_VALUE;
    public static final short  TEST_SHORT  = Short.MAX_VALUE;
    public static final int    TEST_INT    = Integer.MAX_VALUE;
    public static final long   TEST_LONG   = Long.MAX_VALUE;
    public static final float  TEST_FLOAT  = Float.MAX_VALUE;
    public static final double TEST_DOUBLE = Double.MAX_VALUE;
    public static final String TEST_STRING = "Hello, World!";

    public static DataInputStream streamResource(String resourceName) {
        resourceName += ".bin";
        InputStream in = IOTestHelper.class.getClassLoader().getResourceAsStream(resourceName);
        if (in == null) {
            throw new UncheckedIOException(new IOException("Missing resource: " + resourceName));
        }

        return new DataInputStream(in);
    }

    public static byte[] createTestByteArray() {
        byte[] array = new byte[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) ((i * i * 255 + i * 7) % 100);
        }
        return array;
    }

    public static int[] createTestIntArray() {
        int[] array = new int[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = (i * i * 255 + i * 7);
        }
        return array;
    }

    public static long[] createTestLongArray() {
        long[] array = new long[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = (i * 7 + (long) Math.pow(i, 6));
        }
        return array;
    }

    public static NBTList createTestEmptyList() {
        return new NBTList(TagType.END);
    }

    public static NBTList createTestDoubleList() {
        NBTList list = new NBTList(TagType.DOUBLE);
        for (int i = 0; i < 100; i++) {
            list.add(Math.pow(i, Math.PI * Math.PI + i));
        }
        return list;
    }

    public static NBTList createTestCompoundList() {
        NBTList list = new NBTList(TagType.COMPOUND);
        for (int i = 1; i <= 100; i++) {
            NBTCompound element = new NBTCompound();
            element.put("name", "Entry #" + i);
            element.put("value", (float) (i * Math.E));
            list.add(element);
        }
        return list;
    }

    public static NBTCompound createTestCompound(boolean withNestedCompound) {
        NBTCompound compound = new NBTCompound();
        compound.put("byte", TEST_BYTE);
        compound.put("short", TEST_SHORT);
        compound.put("int", TEST_INT);
        compound.put("long", TEST_LONG);
        compound.put("float", TEST_FLOAT);
        compound.put("double", TEST_DOUBLE);
        compound.put("string", TEST_STRING);
        compound.put("byte_array", createTestByteArray());
        compound.put("int_array", createTestIntArray());
        compound.put("long_array", createTestLongArray());
        compound.put("list_end", createTestEmptyList());
        compound.put("list_double", createTestDoubleList());
        compound.put("list_compound", createTestCompoundList());
        if (withNestedCompound) {
            // Writes another compound (inside this one) with all the same tags as above.
            compound.put("compound", createTestCompound(false));
        }
        return compound;
    }

    private IOTestHelper() {
        throw new UnsupportedOperationException("Helper class should not be instantiated");
    }
}
