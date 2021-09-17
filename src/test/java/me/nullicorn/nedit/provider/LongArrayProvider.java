package me.nullicorn.nedit.provider;

import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

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

    public static final class IOProvider extends IOBasedArgumentsProvider {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return LongArrayProvider::new;
        }

        @Override
        Encoder<long[]> encoder() {
            return (out, array) -> {
                out.writeInt(array.length);
                for (long value : array) {
                    out.writeLong(value);
                }
            };
        }
    }
}
