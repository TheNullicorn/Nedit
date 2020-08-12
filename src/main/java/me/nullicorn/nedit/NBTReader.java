package me.nullicorn.nedit;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Nullicorn
 */
public class NBTReader implements Closeable {

    private final InputStream inputStream;

    public NBTReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}