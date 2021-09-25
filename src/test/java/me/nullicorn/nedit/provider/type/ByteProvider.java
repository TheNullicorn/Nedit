package me.nullicorn.nedit.provider.type;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.TagProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class ByteProvider extends TagProvider<byte[]> {

    @Override
    public byte[] provide() {
        return new byte[]{
            0,
            Byte.MIN_VALUE,
            Byte.MAX_VALUE
        };
    }

    @Override
    public Object getExtraneousValue() {
        // Without the cast this returns an int.
        return (byte) (Byte.MAX_VALUE / 2);
    }

    public static final class IOProvider extends NBTEncodedValueProvider {

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
