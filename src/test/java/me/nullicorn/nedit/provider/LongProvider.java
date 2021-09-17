package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class LongProvider extends ArrayBasedArgumentProvider {

    @Override
    long[] provide() {
        return new long[]{
            0,
            Long.MIN_VALUE,
            Long.MAX_VALUE
        };
    }

    public static final class IOProvider extends IOBasedArgumentsProvider {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return LongProvider::new;
        }

        @Override
        Encoder<Long> encoder() {
            return DataOutputStream::writeLong;
        }
    }
}
