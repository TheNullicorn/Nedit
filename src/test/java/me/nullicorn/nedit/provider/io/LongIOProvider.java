package me.nullicorn.nedit.provider.io;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.LongProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class LongIOProvider extends IOBasedArgumentsProvider {

    @Override
    Supplier<ArgumentsProvider> provider() {
        return LongProvider::new;
    }

    @Override
    Encoder<Long> encoder() {
        return DataOutputStream::writeLong;
    }
}
