package me.nullicorn.nedit.provider.io;

import java.util.function.Supplier;
import me.nullicorn.nedit.provider.LongArrayProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class LongArrayIOProvider extends IOBasedArgumentsProvider {

    @Override
    Supplier<ArgumentsProvider> provider() {
        return LongArrayProvider::new;
    }

    @Override
    Encoder<long[]> encoder() {
        // TODO: 9/16/21 Prefix with array length (VarInt).
        return (out, longs) -> {
            for (long value : longs) {
                out.writeLong(value);
            }
        };
    }
}
