package me.nullicorn.nedit;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;

public final class IOTestHelper {

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

    private IOTestHelper() {
        throw new UnsupportedOperationException("Helper class should not be instantiated");
    }
}
