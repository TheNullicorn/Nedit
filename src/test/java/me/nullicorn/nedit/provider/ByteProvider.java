package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class ByteProvider extends NBTValueProvider {

    @Override
    byte[] provide() {
        return new byte[]{
            0,
            Byte.MIN_VALUE,
            Byte.MAX_VALUE
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider<Byte> {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return ByteProvider::new;
        }

        @Override
        NBTEncoder<Byte> encoder() {
            return DataOutputStream::write;
        }
    }
}
