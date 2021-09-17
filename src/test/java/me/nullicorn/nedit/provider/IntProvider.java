package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class IntProvider extends NBTValueProvider {

    @Override
    int[] provide() {
        return new int[]{
            0,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider<Integer> {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return IntProvider::new;
        }

        @Override
        NBTEncoder<Integer> encoder() {
            return DataOutputStream::writeInt;
        }
    }
}
