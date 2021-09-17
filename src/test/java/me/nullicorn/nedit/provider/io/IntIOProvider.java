package me.nullicorn.nedit.provider.io;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.IntProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class IntIOProvider extends IOBasedArgumentsProvider {

    @Override
    Supplier<ArgumentsProvider> provider() {
        return IntProvider::new;
    }

    @Override
    Encoder<Integer> encoder() {
        return DataOutputStream::writeInt;
    }
}
