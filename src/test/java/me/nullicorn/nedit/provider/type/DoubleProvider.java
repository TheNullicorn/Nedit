package me.nullicorn.nedit.provider.type;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.TagProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class DoubleProvider extends TagProvider<double[]> {

    @Override
    public double[] provide() {
        return new double[]{
            0,
            Double.MIN_VALUE,
            Double.MAX_VALUE
        };
    }

    @Override
    public Object getExtraneousValue() {
        return Double.MAX_VALUE / 2;
    }

    public static final class IOProvider extends NBTEncodedValueProvider {

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
