package me.nullicorn.nedit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.TagType;

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
     */
    public static NBTCompound readBase64(String base64) throws IOException {
        return readBase64(base64, false, false);
    }

    /**
     * Same as {@link #readBase64(String)}, but with additional control over the interning of tag
     * names and values
     *
     * @param internNames  Whether or not tag names inside of compounds will be interned
     * @param internValues Whether or not {@link TagType#STRING} values inside compounds and lists
     *                     will be interned
     * @see #readBase64(String)
     * @see String#intern()
     * @see NBTInputStream#NBTInputStream(InputStream, boolean, boolean)
     */
    public static NBTCompound readBase64(String base64, boolean internNames, boolean internValues) throws IOException {
        ByteArrayInputStream b64In = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
        return read(b64In, internNames, internValues);
    }

    /**
     * Read NBT data from a file
     *
     * @param nbtFile A valid NBT file
     * @return The parsed compound
     * @throws IOException If the file or its contents could not be read properly
     */
    public static NBTCompound readFile(File nbtFile) throws IOException {
        return readFile(nbtFile, false, false);
    }

    /**
     * Same as {@link #readFile(File)}, but with additional control over the interning of tag names
     * and values
     *
     * @param internNames  Whether or not tag names inside of compounds will be interned
     * @param internValues Whether or not {@link TagType#STRING} values inside compounds and lists
     *                     will be interned
     * @see #read(InputStream)
     * @see String#intern()
     * @see NBTInputStream#NBTInputStream(InputStream, boolean, boolean)
     */
    public static NBTCompound readFile(File nbtFile, boolean internNames, boolean internValues) throws IOException {
        if (!nbtFile.exists() || !nbtFile.isFile() || !nbtFile.canRead()) {
            throw new FileNotFoundException("NBT file not found or unable to be read");
        }

        try (InputStream fileIn = new FileInputStream(nbtFile)) {
            return read(new FileInputStream(nbtFile), internNames, internValues);
        }
    }

    /**
     * Read NBT data from an InputStream
     *
     * @param inputStream An InputStream containing valid NBT data (may be gzipped)
     * @return The parsed compound
     * @throws IOException If the data could not be read properly
     */
    public static NBTCompound read(InputStream inputStream) throws IOException {
        return read(inputStream, false, false);
    }

    /**
     * Same as {@link #read(InputStream)}, but with additional control over the interning of tag
     * names and values
     *
     * @param internNames  Whether or not tag names inside of compounds will be interned
     * @param internValues Whether or not {@link TagType#STRING} values inside compounds and lists
     *                     will be interned
     * @see #read(InputStream)
     * @see String#intern()
     * @see NBTInputStream#NBTInputStream(InputStream, boolean, boolean)
     */
    public static NBTCompound read(InputStream inputStream, boolean internNames, boolean internValues) throws IOException {
        return new NBTInputStream(inputStream, internNames, internValues).readFully();
    }

    private NBTReader() {
        throw new UnsupportedOperationException("NBTReader should not be instantiated");
    }
}
