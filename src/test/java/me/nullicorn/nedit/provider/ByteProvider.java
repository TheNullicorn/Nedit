package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class ByteProvider extends ArrayBasedArgumentProvider {

    @Override
    byte[] provide() {
        return new byte[]{
            0,
            Byte.MIN_VALUE,
            Byte.MAX_VALUE
        };
    }

    public static final class IOProvider extends IOBasedArgumentsProvider {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return ByteProvider::new;
        }

        @Override
        Encoder<Byte> encoder() {
            return DataOutputStream::write;
        }
    }
}
