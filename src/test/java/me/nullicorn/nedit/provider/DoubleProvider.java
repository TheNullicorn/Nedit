package me.nullicorn.nedit.provider;

/**
 * @author Nullicorn
 */
public final class DoubleProvider extends ArrayBasedArgumentProvider {

    @Override
    double[] provide() {
        return new double[]{
            0,
            Double.MIN_VALUE,
            Double.MAX_VALUE
        };
    }
}
