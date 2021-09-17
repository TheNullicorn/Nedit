package me.nullicorn.nedit.provider.io;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.ByteArrayProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class ByteArrayIOProvider extends IOBasedArgumentsProvider {

    @Override
    Supplier<ArgumentsProvider> provider() {
        return ByteArrayProvider::new;
    }

    @Override
    Encoder<byte[]> encoder() {
        // TODO: 9/16/21 Prefix with array length (VarInt).
        return DataOutputStream::write;
    }
}
