package me.nullicorn.nedit.provider.io;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.ByteProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class ByteIOProvider extends IOBasedArgumentsProvider {

    @Override
    Supplier<ArgumentsProvider> provider() {
        return ByteProvider::new;
    }

    @Override
    Encoder<Byte> encoder() {
        return DataOutputStream::writeByte;
    }
}
