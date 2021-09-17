package me.nullicorn.nedit.provider;

/**
 * @author Nullicorn
 */
public final class IntProvider extends ArrayBasedArgumentProvider {

    @Override
    int[] provide() {
        return new int[]{
            0,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE
        };
    }
}
