package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

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

    public static final class IOProvider extends IOBasedArgumentsProvider {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return StringProvider::new;
        }

        @Override
        Encoder<String> encoder() {
            return DataOutputStream::writeUTF;
        }
    }
}
