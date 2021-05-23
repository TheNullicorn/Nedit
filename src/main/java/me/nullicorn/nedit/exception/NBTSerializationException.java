package me.nullicorn.nedit.exception;

import java.io.IOException;

/**
 * @author Nullicorn
 */
public class NBTSerializationException extends IOException {

    public NBTSerializationException() {
    }

    public NBTSerializationException(String message) {
        super(message);
    }

    public NBTSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NBTSerializationException(Throwable cause) {
        super(cause);
    }
}
