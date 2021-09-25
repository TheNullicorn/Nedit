package me.nullicorn.nedit.provider.type;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.TagProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class IntProvider extends TagProvider<int[]> {

    @Override
    public int[] provide() {
        return new int[]{
            0,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE
        };
    }

    @Override
    public Object getExtraneousValue() {
        return Integer.MAX_VALUE / 2;
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
