package me.nullicorn.nedit.provider.io;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.ShortProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class ShortIOProvider extends IOBasedArgumentsProvider {

    @Override
    Supplier<ArgumentsProvider> provider() {
        return ShortProvider::new;
    }

    @Override
    Encoder<Short> encoder() {
        return DataOutputStream::writeShort;
    }
}
