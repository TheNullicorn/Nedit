package me.nullicorn.nedit.provider.tag;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.NBTValueProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class ByteProvider extends NBTValueProvider {

    @Override
    public byte[] provide() {
        return new byte[]{
            0,
            Byte.MIN_VALUE,
            Byte.MAX_VALUE
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider<Byte> {

        @Override
        public Supplier<ArgumentsProvider> provider() {
            return ByteProvider::new;
        }

        @Override
        public NBTEncoder<Byte> encoder() {
            return DataOutputStream::write;
        }
    }
}
