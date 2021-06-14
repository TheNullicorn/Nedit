package me.nullicorn.nedit.filter;

import java.io.IOException;
import java.io.InputStream;
import me.nullicorn.nedit.NBTInputStream;
import me.nullicorn.nedit.exception.NBTParseException;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;

/**
 * A stream for decoding and serializing NBT data, with the option to provide a {@link NBTFilter
 * filter} to be applied when reading compounds.
 * <p><br>
 * The class's featured method is {@link #readFully(NBTFilter)}.
 *
 * @author Nullicorn
 */
public class FilteredNBTInputStream extends NBTInputStream {

    /**
     * @see NBTInputStream#NBTInputStream(InputStream)
     */
    public FilteredNBTInputStream(InputStream inputStream) {
        super(inputStream);
    }

    /**
     * @see NBTInputStream#NBTInputStream(InputStream, boolean, boolean)
     */
    public FilteredNBTInputStream(InputStream inputStream, boolean internNames, boolean internValues) {
        super(inputStream, internNames, internValues);
    }

    /**
     * Reads an entire NBT compound from the stream. Any properties not allowed in the output (as
     * per the {@code filter}'s rules) will be skipped in the underlying stream.
     *
     * @param filter A filter used to determine which properties should and should not be added to
     *               the resulting compound and any of its child tags. Tags not allowed by the
     *               filter will not be read, and instead will be skipped over in the stream
     *               itself.
     * @return The read compound, conforming to the {@code filter}'s rules.
     * @throws IOException If the compound or any of its child tags could not be read.
     */
    public NBTCompound readFully(NBTFilter filter) throws IOException {
        gunzipIfNecessary();
        TagType rootType = readTagId();
        if (rootType == TagType.END) {
            return new NBTCompound();
        } else if (rootType != TagType.COMPOUND) {
            throw new IOException("Expected COMPOUND at NBT root, but got " + rootType);
        }
        readString();
        return readCompound(filter, 0);
    }

    /**
     * Reads a nameless NBT compound from the head of the stream, recursively applying a {@code
     * filter} to any tags inside it that are lists or compounds.
     *
     * @param filter The filter to apply to each nested list and compound inside the compound being
     *               read.
     * @param depth  The compound's distance from the root compound, or how many parent tags the
     *               compound has.
     */
    private NBTCompound readCompound(NBTFilter filter, int depth) throws IOException {
        NBTCompound result = new NBTCompound();

        boolean reachedEnd = false;
        while (!reachedEnd) {
            TagType tagType = readTagId();

            if (tagType == null) {
                throw new NBTParseException("Unknown tag ID for TAG_Compound");
            } else if (tagType == TagType.END) {
                reachedEnd = true;
                continue;
            }

            String tagName = readString(internNames);

            //  - Exclusion filters will default to reading unless an excluded tag is found, which
            //    will be skipped.
            //  - Inclusion filters default to skipping unless an included tag is found, which will
            //    be read.
            boolean continueReading = (filter.getMode() == FilterMode.EXCLUDE);
            FilteredTag match = null;

            // Check for any filtered tags that match the current tag's name & depth.
            for (FilteredTag filtered : filter) {
                String[] tokens = filtered.getTokens();
                boolean matches = depth < tokens.length && tagName.equals(tokens[depth]);
                if (matches) {
                    // A match always means to keep reading in an inclusion filter.
                    // In an exclusion filter, we only need to keep reading if the match goes
                    // deeper.
                    continueReading = (filter.getMode() == FilterMode.INCLUDE)
                                      || (depth + 1 < tokens.length);
                    match = filtered;
                    break; // Only one match is needed to determine the tag's inclusion status.
                }
            }

            if (continueReading) {
                int childDepth = depth + 1;

                // Try to create a filter that could be used on the child value (if its a list or
                // compound).
                NBTFilter subFilter = (match == null)
                    ? null
                    : filter.subFilter(match, childDepth);

                // Only apply the filter if it has entries. Otherwise, read the entire tag.
                Object value = (subFilter == null || subFilter.isEmpty())
                    ? readValue(tagType)
                    : readValue(tagType, subFilter, childDepth);

                result.put(tagName, value);
            } else {
                // Skip excluded (or non-included) tags.
                skipValue(tagType);
            }
        }

        return result;
    }

    /**
     * Reads a nameless NBT list from the head of the stream, applying a {@code filter} to its
     * elements if they are compounds or lists.
     *
     * @param filter The filter to apply to the lists's elements (if they are compound or list
     *               tags).
     * @param depth  The list's distance from the root compound, or how many parent tags the list
     *               has.
     */
    private NBTList readList(NBTFilter filter, int depth) throws IOException {
        TagType contentType = readTagId();
        int length = readInt();

        if (contentType == null) {
            throw new NBTParseException("Unknown tag ID for TAG_List");
        } else if (length == 0) {
            return new NBTList(contentType);
        } else if (length < 0) {
            throw new NBTParseException(new NegativeArraySizeException());
        }

        NBTList list = new NBTList(contentType);
        for (int i = 0; i < length; i++) {
            // Don't increase the depth here, because when a list of compounds is filtered, those
            // compounds' tags are referenced directly on the list itself.
            //
            // e.g. `Level.Sections.Palette`, NOT `Level.Sections.[index].Palette`
            list.add(readValue(contentType, filter, depth));
        }

        return list;
    }

    /**
     * Reads a nameless NBT value from the stream's head, applying a {@code filter} if the tag is a
     * list or compound.
     *
     * @param type   The type of value to read.
     * @param filter The filter to apply (only applicable to compound and list tags).
     * @param depth  The value's distance from the root compound, or how many parent tags the value
     *               has.
     */
    private Object readValue(TagType type, NBTFilter filter, int depth) throws IOException {
        if (type == TagType.COMPOUND) {
            return readCompound(filter, depth);
        } else if (type == TagType.LIST) {
            return readList(filter, depth);
        }
        return readValue(type);
    }

    /**
     * Skips however many bytes from the stream are needed to encode a tag of a given {@code type}.
     *
     * @param type The type of tag at the stream's head.
     */
    private void skipValue(TagType type) throws IOException {
        int bytesToSkip;
        switch (type) {
            case LIST:
                TagType elementType = readTagId();
                int length = readInt();

                // Skip each element in the list.
                if (elementType != TagType.END) {
                    for (int i = 0; i < length; i++) {
                        skipValue(elementType);
                    }
                }
                return;

            case COMPOUND:
                // Skip each child tag in the compound.
                TagType childType;
                while ((childType = readTagId()) != TagType.END) {
                    skipString(); // Skips the tag's name.
                    skipValue(childType);
                }
                return;

            case STRING:
                skipString();
                return;

            case BYTE:
                bytesToSkip = 1;
                break;

            case SHORT:
                bytesToSkip = 2;
                break;

            case FLOAT:
            case INT:
                bytesToSkip = 4;
                break;

            case LONG:
            case DOUBLE:
                bytesToSkip = 8;
                break;

            case BYTE_ARRAY:
                bytesToSkip = readInt();
                break;

            case INT_ARRAY:
                bytesToSkip = readInt() * 4;
                break;

            case LONG_ARRAY:
                bytesToSkip = readInt() * 8;
                break;

            default:
                return;
        }

        skipBytes(bytesToSkip);
    }

    /**
     * Skips however many bytes from the stream are needed to encode an NBT string, including its
     * prefixed length.
     */
    private void skipString() throws IOException {
        int length = readUnsignedShort();
        skipBytes(length); // Skips the string's characters.
    }
}
