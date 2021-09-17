package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class DoubleProvider extends ArrayArgumentsProvider {

    @Override
    double[] provide() {
        return new double[]{
            0,
            Double.MIN_VALUE,
            Double.MAX_VALUE
        };
    }

    public static final class IOProvider extends IOBasedArgumentsProvider<Double> {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return DoubleProvider::new;
        }

        @Override
        Encoder<Double> encoder() {
            return DataOutputStream::writeDouble;
        }
    }
}
