package me.nullicorn.nedit.exception;

/**
 * Thrown to indicate a problem reading NBT data from a source.
 *
 * @author Nullicorn
 */
public class NBTParseException extends NBTSerializationException {

    private static final String DEFAULT_MESSAGE = "Unable to parse NBT data from stream";

    public NBTParseException() {
        this(DEFAULT_MESSAGE);
    }

    public NBTParseException(String message) {
        super(message);
    }

    public NBTParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public NBTParseException(Throwable cause) {
        super(cause);
    }
}
