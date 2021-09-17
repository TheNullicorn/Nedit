package me.nullicorn.nedit.provider;

/**
 * @author Nullicorn
 */
public final class ByteProvider extends ArrayBasedArgumentProvider {

    @Override
    byte[] provide() {
        return new byte[]{
            0,
            Byte.MIN_VALUE,
            Byte.MAX_VALUE
        };
    }
}
