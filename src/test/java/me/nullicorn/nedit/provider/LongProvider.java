package me.nullicorn.nedit.provider;

/**
 * @author Nullicorn
 */
public final class LongProvider extends ArrayBasedArgumentProvider {

    @Override
    long[] provide() {
        return new long[]{
            0,
            Long.MIN_VALUE,
            Long.MAX_VALUE
        };
    }
}
