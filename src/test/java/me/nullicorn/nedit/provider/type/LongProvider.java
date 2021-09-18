package me.nullicorn.nedit.provider.type;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.TagProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class LongProvider extends TagProvider {

    @Override
    public long[] provide() {
        return new long[]{
            0,
            Long.MIN_VALUE,
            Long.MAX_VALUE
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider {

        @Override
        public Supplier<ArgumentsProvider> provider() {
            return LongProvider::new;
        }

        @Override
        public NBTEncoder<Long> encoder() {
            return DataOutputStream::writeLong;
        }
    }
}
