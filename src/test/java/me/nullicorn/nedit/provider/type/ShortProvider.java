package me.nullicorn.nedit.provider.type;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.TagProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class ShortProvider extends TagProvider<short[]> {

    @Override
    public short[] provide() {
        return new short[]{
            0,
            Short.MIN_VALUE,
            Short.MAX_VALUE
        };
    }

    @Override
    public Object getExtraneousValue() {
        // Without the cast this returns an int.
        return (short) (Short.MAX_VALUE / 2);
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
