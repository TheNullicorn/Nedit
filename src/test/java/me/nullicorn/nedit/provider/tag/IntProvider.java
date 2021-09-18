package me.nullicorn.nedit.provider.tag;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.NBTValueProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class IntProvider extends NBTValueProvider {

    @Override
    public int[] provide() {
        return new int[]{
            0,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider {

        @Override
        public Supplier<ArgumentsProvider> provider() {
            return IntProvider::new;
        }

        @Override
        public NBTEncoder<Integer> encoder() {
            return DataOutputStream::writeInt;
        }
    }
}
