package me.nullicorn.nedit.filter;

import java.io.IOException;
import java.io.InputStream;
import me.nullicorn.nedit.NBTInputStream;
import me.nullicorn.nedit.exception.NBTParseException;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;

/**
 * @author Nullicorn
 */
public class FilteredNBTInputStream extends NBTInputStream {

    public FilteredNBTInputStream(InputStream inputStream) {
        super(inputStream);
    }

    public FilteredNBTInputStream(InputStream inputStream, boolean internNames, boolean internValues) {
        super(inputStream, internNames, internValues);
    }

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

            //  - Exclusion filters will default to reading unless an exlcuded tag is found, which
            //    will be skipped.
            //  - Inclusion filters default to skipping unless an included tag is found, which will
            //    be read.
            boolean continueReading = (filter.getMode() == FilterMode.EXCLUDE);
            FilteredTag match = null;

            // Check for any filtered tags that match the current tag's name & depth.
            for (FilteredTag filtered : filter.getFilteredTags()) {
                String[] tokens = filtered.getTokens();
                boolean matches = depth < tokens.length && tagName.equals(tokens[depth]);
                if (matches) {
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

    private Object readValue(TagType type, NBTFilter filter, int depth) throws IOException {
        if (type == TagType.COMPOUND) {
            return readCompound(filter, depth);
        } else if (type == TagType.LIST) {
            return readList(filter, depth);
        }
        return readValue(type);
    }

    private void skipString() throws IOException {
        int length = readUnsignedShort();
        skipBytes(length); // Skips the string's characters.
    }

    private void skipCompound() throws IOException {
        TagType childType;
        while ((childType = readTagId()) != TagType.END) {
            skipString(); // Skips the tag's name.
            skipValue(childType);
        }
    }

    private void skipList() throws IOException {
        TagType elementType = readTagId();
        int length = readInt();

        if (elementType != TagType.END) {
            for (int i = 0; i < length; i++) {
                skipValue(elementType);
            }
        }
    }

    private void skipValue(TagType type) throws IOException {
        int bytesToSkip;
        switch (type) {
            case LIST:
                skipList();
                return;

            case COMPOUND:
                skipCompound();
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
}
