package me.nullicorn.nedit.provider;

/**
 * @author Nullicorn
 */
public final class LongArrayProvider extends ArrayBasedArgumentProvider {

    @Override
    long[][] provide() {
        return new long[][]{
            new long[0],
            generateLongs(129),
            generateLongs(3210)
        };
    }

    public static long[] generateLongs(int amount) {
        long[] array = new long[amount];
        for (int i = 0; i < array.length; i++) {
            array[i] = (i * 7 + (long) Math.pow(i, 6));
        }
        return array;
    }
}
