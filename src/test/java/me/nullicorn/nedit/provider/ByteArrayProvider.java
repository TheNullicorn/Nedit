package me.nullicorn.nedit.provider;

import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class ByteArrayProvider extends ArrayArgumentsProvider {

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

    public static final class IOProvider extends IOBasedArgumentsProvider<byte[]> {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return ByteArrayProvider::new;
        }

        @Override
        Encoder<byte[]> encoder() {
            return (out, array) -> {
                out.writeInt(array.length);
                out.write(array);
            };
        }
    }
}
