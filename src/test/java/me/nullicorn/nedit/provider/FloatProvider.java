package me.nullicorn.nedit.provider;

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
}
