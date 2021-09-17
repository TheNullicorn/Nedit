package me.nullicorn.nedit.provider;

/**
 * @author Nullicorn
 */
public final class IntArrayProvider extends ArrayBasedArgumentProvider {

    @Override
    int[][] provide() {
        return new int[][]{
            new int[0],
            generateInts(123),
            generateInts(1234)
        };
    }

    public static int[] generateInts(int amount) {
        int[] array = new int[amount];
        for (int i = 0; i < array.length; i++) {
            array[i] = (i * i * 255 + i * 7);
        }
        return array;
    }
}
