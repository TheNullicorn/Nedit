package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class ShortProvider extends ArrayBasedArgumentProvider {

    @Override
    short[] provide() {
        return new short[]{
            0,
            Short.MIN_VALUE,
            Short.MAX_VALUE
        };
    }

    public static final class IOProvider extends IOBasedArgumentsProvider {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return ShortProvider::new;
        }

        @Override
        Encoder<Short> encoder() {
            return DataOutputStream::writeShort;
        }
    }
}
