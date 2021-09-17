package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class IntProvider extends ArrayArgumentsProvider {

    @Override
    int[] provide() {
        return new int[]{
            0,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE
        };
    }

    public static final class IOProvider extends IOBasedArgumentsProvider<Integer> {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return IntProvider::new;
        }

        @Override
        Encoder<Integer> encoder() {
            return DataOutputStream::writeInt;
        }
    }
}
