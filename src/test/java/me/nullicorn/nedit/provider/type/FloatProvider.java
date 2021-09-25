package me.nullicorn.nedit.provider.type;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.TagProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class FloatProvider extends TagProvider<float[]> {

    @Override
    public float[] provide() {
        return new float[]{
            0,
            Float.MIN_VALUE,
            Float.MAX_VALUE
        };
    }

    @Override
    public Object getExtraneousValue() {
        return Float.MAX_VALUE / 2;
    }

    public static final class IOProvider extends NBTEncodedValueProvider {

        @Override
        public Supplier<ArgumentsProvider> provider() {
            return FloatProvider::new;
        }

        @Override
        public NBTEncoder<Float> encoder() {
            return DataOutputStream::writeFloat;
        }
    }
}
