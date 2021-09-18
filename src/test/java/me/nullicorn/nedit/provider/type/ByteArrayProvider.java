package me.nullicorn.nedit.provider.type;

import java.util.function.Supplier;
import me.nullicorn.nedit.provider.TagProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class ByteArrayProvider extends TagProvider {

    @Override
    public byte[][] provide() {
        return new byte[][]{
            new byte[0],
            generateBytes(500),
            generateBytes(4096)
        };
    }

    /**
     * Generates a diverse array of {@code byte}s, whose {@code length} is determined by the
     * argument with the same name.
     */
    public static byte[] generateBytes(int length) {
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = (byte) ((i * i * 255 + i * 7) % 100);
        }
        return array;
    }

    public static final class IOProvider extends NBTEncodedValueProvider {

        @Override
        public Supplier<ArgumentsProvider> provider() {
            return ByteArrayProvider::new;
        }

        @Override
        public NBTEncoder<byte[]> encoder() {
            return (out, array) -> {
                out.writeInt(array.length);
                out.write(array);
            };
        }
    }
}
