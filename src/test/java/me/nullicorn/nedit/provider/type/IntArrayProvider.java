package me.nullicorn.nedit.provider.type;

import java.util.function.Supplier;
import me.nullicorn.nedit.provider.TagProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class IntArrayProvider extends TagProvider<int[][]> {

    @Override
    public int[][] provide() {
        return new int[][]{
            new int[0],
            generateInts(123),
            generateInts(1234)
        };
    }

    @Override
    public Object getExtraneousValue() {
        return new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    }

    /**
     * Generates a diverse array of {@code int}s, whose {@code length} is determined by the argument
     * with the same name.
     */
    public static int[] generateInts(int length) {
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = (i * i * 255 + i * 7);
        }
        return array;
    }

    public static final class IOProvider extends NBTEncodedValueProvider {

        @Override
        public Supplier<ArgumentsProvider> provider() {
            return IntArrayProvider::new;
        }

        @Override
        public NBTEncoder<int[]> encoder() {
            return (out, array) -> {
                out.writeInt(array.length);
                for (int value : array) {
                    out.writeInt(value);
                }
            };
        }
    }
}
