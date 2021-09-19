package me.nullicorn.nedit.filter;

import java.util.Objects;

/**
 * Represents the name of an NBT tag that should be handled by a {@link NBTFilter filter}.
 *
 * @author Nullicorn
 */
public class FilteredTag {

    /**
     * Splits a dot-notation tag name into its individual parts (or "tokens"), delimited by dots
     * ({@code .}). Literal dots can be escaped using a backslash (or double-backslash for string
     * literals; e.g. {@code \\.}). Tokens in the returned array will not have escaped dots.
     */
    public static String[] tokenizeTagName(String name) {
        // The regex splits the string at dots (.) that aren't escaped. The negative-lookbehind
        // makes sure there is no backslash before the dot.
        String[] tokens = name.split("(?<!\\\\)\\.");
        for (int i = 0; i < tokens.length; i++) {
            // Remove escape characters.
            tokens[i] = tokens[i].replace("\\.", ".");
        }
        return tokens;
    }

    private final String name;
    private final String[] tokens;

    public FilteredTag(String name) {
        if (name == null) {
            throw new NullPointerException("Filtered tag name cannot be null");
        }
        this.name = name;
        tokens = tokenizeTagName(name);
    }

    /**
     * @return The tag's absolute name, including the names of any parent objects delimited by dots
     * ({@code .})
     */
    public String getName() {
        return name;
    }

    /**
     * @return The names of all parent objects of the tag, as well as the tag's name itself. This is
     * the e
     */
    public String[] getTokens() {
        return tokens;
    }

    /**
     * Same as {@link #isExtendedBy(FilteredTag)}, but extension is only check for {@link
     * #getTokens() tokens} up to the provided {@code depth + 1}.
     */
    public boolean isExtendedBy(FilteredTag other, int depth) {
        if (tokens.length <= depth || other.tokens.length <= depth || other.equals(this)) {
            return false;
        }

        for (int i = 0; i < depth; i++) {
            if (!other.tokens[i].equals(tokens[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return {@code true} if the {@code other} tag starts with all of this tag's {@link
     * #getTokens() tokens} & has additional tokens at the end, otherwise {@code false}.
     */
    public boolean isExtendedBy(FilteredTag other) {
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

    /**
     * @return The tag's full name. Equivalent to {@link #getName()}.
     */
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
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
