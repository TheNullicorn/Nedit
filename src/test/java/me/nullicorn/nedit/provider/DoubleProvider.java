package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class DoubleProvider extends NBTValueProvider {

    @Override
    double[] provide() {
        return new double[]{
            0,
            Double.MIN_VALUE,
            Double.MAX_VALUE
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider<Double> {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return DoubleProvider::new;
        }

        @Override
        NBTEncoder<Double> encoder() {
            return DataOutputStream::writeDouble;
        }
    }
}
