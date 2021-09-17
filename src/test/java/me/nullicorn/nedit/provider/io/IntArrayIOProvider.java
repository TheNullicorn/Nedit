package me.nullicorn.nedit.provider.io;

import java.util.function.Supplier;
import me.nullicorn.nedit.provider.IntArrayProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class IntArrayIOProvider extends IOBasedArgumentsProvider {

    @Override
    Supplier<ArgumentsProvider> provider() {
        return IntArrayProvider::new;
    }

    @Override
    Encoder<int[]> encoder() {
        // TODO: 9/16/21 Prefix with array length (VarInt).
        return (out, ints) -> {
            for (int value : ints) {
                out.writeInt(value);
            }
        };
    }
}
