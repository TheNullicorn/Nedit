package me.nullicorn.nedit.provider;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class StringProvider extends NBTValueProvider {

    @Override
    String[] provide() {
        return new String[]{
            "", // Zero-length string
            "Hello, World!", // ASCII-range code points
            "§73 Gold Stars:§6 ✪ ✪ ✪" // Code points outside ASCII range
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider<String> {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return StringProvider::new;
        }

        @Override
        NBTEncoder<String> encoder() {
            return DataOutputStream::writeUTF;
        }
    }
}
