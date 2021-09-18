package me.nullicorn.nedit.provider.tag;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.NBTValueProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class DoubleProvider extends NBTValueProvider {

    @Override
    public double[] provide() {
        return new double[]{
            0,
            Double.MIN_VALUE,
            Double.MAX_VALUE
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider<Double> {

        @Override
        public Supplier<ArgumentsProvider> provider() {
            return DoubleProvider::new;
        }

        @Override
        public NBTEncoder<Double> encoder() {
            return DataOutputStream::writeDouble;
        }
    }
}
