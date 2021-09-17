package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class FloatProvider extends NBTValueProvider {

    @Override
    float[] provide() {
        return new float[]{
            0,
            Float.MIN_VALUE,
            Float.MAX_VALUE
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider<Float> {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return FloatProvider::new;
        }

        @Override
        NBTEncoder<Float> encoder() {
            return DataOutputStream::writeFloat;
        }
    }
}
