package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class FloatProvider extends ArrayBasedArgumentProvider {

    @Override
    float[] provide() {
        return new float[]{
            0,
            Float.MIN_VALUE,
            Float.MAX_VALUE
        };
    }

    public static final class IOProvider extends IOBasedArgumentsProvider {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return FloatProvider::new;
        }

        @Override
        Encoder<Float> encoder() {
            return DataOutputStream::writeFloat;
        }
    }
}
