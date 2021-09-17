package me.nullicorn.nedit.provider;

/**
 * @author Nullicorn
 */
public final class ShortProvider extends ArrayBasedArgumentProvider {

    @Override
    short[] provide() {
        return new short[]{
            0,
            Short.MIN_VALUE,
            Short.MAX_VALUE
        };
    }
}
