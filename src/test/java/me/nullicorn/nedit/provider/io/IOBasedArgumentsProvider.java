package me.nullicorn.nedit.provider.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
abstract class IOBasedArgumentsProvider implements ArgumentsProvider {

    abstract Supplier<ArgumentsProvider> provider();

    abstract Encoder<?> encoder();

    @SuppressWarnings("unchecked")
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        ArgumentsProvider base = provider().get();
        if (base == null) {
            throw new IllegalArgumentException("provider() cannot return null");
        }

        // Returns 2 arguments:
        //   1. The original value
        //   2. That value encoded as a byte[]
        return base.provideArguments(context)
            .map(arg -> {
                Object[] args = arg.get();

                // There should only be 1 argument at this point, but check to be sure.
                if (args == null || args.length > 1) {
                    throw new IllegalArgumentException("Unexpected args: " + Arrays.toString(args));
                }

                return args[0];
            })
            .map(value -> {
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(result);

                // Encode the result.
                try {
                    Encoder<Object> encoder = (Encoder<Object>) encoder();
                    encoder.encode(out, value);
                } catch (IOException e) {
                    throw new UncheckedIOException("Test encoder failed", e);
                }

                // Pass on the original value and the encoded bytes.
                return Arguments.of(value, result.toByteArray());
            });
    }

    interface Encoder<T> {

        void encode(DataOutputStream out, T value) throws IOException;
    }
}
