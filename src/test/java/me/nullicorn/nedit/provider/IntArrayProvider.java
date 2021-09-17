package me.nullicorn.nedit.provider;

import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

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

    public static final class IOProvider extends IOBasedArgumentsProvider {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return IntArrayProvider::new;
        }

        @Override
        Encoder<int[]> encoder() {
            return (out, array) -> {
                out.writeInt(array.length);
                for (int value : array) {
                    out.writeInt(value);
                }
            };
        }
    }
}
