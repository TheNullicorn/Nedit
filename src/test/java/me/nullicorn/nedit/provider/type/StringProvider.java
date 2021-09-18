package me.nullicorn.nedit.provider.type;

import java.io.DataOutputStream;
import java.util.function.Supplier;
import me.nullicorn.nedit.provider.TagProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class StringProvider extends TagProvider {

    @Override
    public String[] provide() {
        return new String[]{
            "", // Zero-length string
            "Hello, World!", // ASCII-range code points
            "§73 Gold Stars:§6 ✪ ✪ ✪" // Code points outside ASCII range
        };
    }

    public static final class IOProvider extends NBTEncodedValueProvider {

        @Override
        public Supplier<ArgumentsProvider> provider() {
            return StringProvider::new;
        }

        @Override
        public NBTEncoder<String> encoder() {
            return DataOutputStream::writeUTF;
        }
    }
}
