package me.nullicorn.nedit.provider;

/**
 * @author Nullicorn
 */
public final class ByteArrayProvider extends ArrayBasedArgumentProvider {

    @Override
    byte[][] provide() {
        return new byte[][]{
            new byte[0],
            generateBytes(500),
            generateBytes(4096)
        };
    }

    public static byte[] generateBytes(int amount) {
        byte[] array = new byte[amount];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) ((i * i * 255 + i * 7) % 100);
        }
        return array;
    }
}
