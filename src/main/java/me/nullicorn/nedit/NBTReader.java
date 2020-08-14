package me.nullicorn.nedit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import lombok.NonNull;
import me.nullicorn.nedit.type.NBTCompound;

/**
 * A utility class for reading NBT data from various sources
 *
 * @author Nullicorn
 */
public final class NBTReader {

    /**
     * Read NBT data from a Base64 string
     *
     * @param base64 Base64-encoded string containing NBT data (may be gzipped)
     * @return The parsed compound
     * @throws IOException If the data could not be read properly
     * @see #read(InputStream)
     */
    public static NBTCompound readBase64(@NonNull String base64) throws IOException {
        return read(new ByteArrayInputStream(Base64.getDecoder().decode(base64)));
    }

    /**
     * Read NBT data from a file
     *
     * @param nbtFile A valid NBT file
     * @return The parsed compound
     * @throws IOException If the file or its contents could not be read properly
     */
    public static NBTCompound readFile(@NonNull File nbtFile) throws IOException {
        if (!nbtFile.exists() || !nbtFile.isFile() || !nbtFile.canRead()) {
            throw new FileNotFoundException("NBT file not found or unable to be read");
        }
        return read(new FileInputStream(nbtFile));
    }

    /**
     * Read NBT data from an InputStream
     *
     * @param inputStream An InputStream containing valid NBT data (may be gzipped)
     * @return The parsed compound
     * @throws IOException If the data could not be read properly
     */
    public static NBTCompound read(@NonNull InputStream inputStream) throws IOException {
        try (InputStream nbtIn = inputStream) {
            return new NBTInputStream(nbtIn).readFully();
        }
    }

    private NBTReader() {
        // Prevent instantiation of this class
    }
}
