package me.nullicorn.nedit.provider.tag;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.NBTValueProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class ShortProvider extends NBTValueProvider {

    @Override
    public short[] provide() {
        return new short[]{
            0,
            Short.MIN_VALUE,
            Short.MAX_VALUE
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider {

        @Override
        public Supplier<ArgumentsProvider> provider() {
            return ShortProvider::new;
        }

        @Override
        public NBTEncoder<Short> encoder() {
            return DataOutputStream::writeShort;
        }
    }
}
