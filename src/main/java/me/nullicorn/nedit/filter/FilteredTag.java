package me.nullicorn.nedit.filter;

import java.util.Objects;
import me.nullicorn.nedit.type.TagType;
import org.jetbrains.annotations.Nullable;

/**
 * @author Nullicorn
 */
public class FilteredTag {

    @Nullable
    private final TagType  type;
    private final String   name;
    private final String[] tokens;

    public FilteredTag(String name) {
        this(name, null);
    }

    public FilteredTag(String name, @Nullable TagType type) {
        this.name = name;
        this.type = type;
        tokens = name.split("(?<!\\\\)\\.");
    }

    public String getName() {
        return name;
    }

    public String[] getTokens() {
        return tokens;
    }

    boolean doesTokenMatch(String token, int depth) {
        return tokens.length >= (depth - 1) && tokens[depth].equals(token);
    }

    /**
     * @return {@code true} if the {@code other} tag starts with all of this tag's {@link
     * #getTokens() tokens} & has additional tokens at the end, otherwise {@code false}.
     */
    boolean isExtendedBy(FilteredTag other) {
        String otherName = other.getName();
        int extensionIndex = name.length();

        // (1) `other` cannot possibly be an extension if it has a shorter or equal length.
        // (2) Check that the other tag continues immediately after extensionIndex.
        // (3) Check that the dot (.) we found in (2) wasn't escaped.
        // (4) Check that the other tag starts with this tag's entire names.
        return otherName.length() > extensionIndex
               && otherName.charAt(extensionIndex) == '.'
               && otherName.charAt(extensionIndex - 1) != '\\'
               && otherName.startsWith(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilteredTag that = (FilteredTag) o;
        return type == that.type &&
               name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }
}
