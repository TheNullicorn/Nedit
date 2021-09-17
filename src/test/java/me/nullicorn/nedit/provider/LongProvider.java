package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class LongProvider extends NBTValueProvider {

    @Override
    long[] provide() {
        return new long[]{
            0,
            Long.MIN_VALUE,
            Long.MAX_VALUE
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider<Long> {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return LongProvider::new;
        }

        @Override
        NBTEncoder<Long> encoder() {
            return DataOutputStream::writeLong;
        }
    }
}
