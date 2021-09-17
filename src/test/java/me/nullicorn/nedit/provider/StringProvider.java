package me.nullicorn.nedit.provider;

/**
 * @author Nullicorn
 */
public final class StringProvider extends ArrayBasedArgumentProvider {

    @Override
    String[] provide() {
        return new String[]{
            "", // Zero-length string
            "Hello, World!", // ASCII-range code points
            "§73 Gold Stars:§6 ✪ ✪ ✪" // Code points outside ASCII range
        };
    }
}
