package me.nullicorn.nedit.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * A tool for stripping NBT compounds of excess data, especially to retain a low memory footprint.
 */
public class NBTFilter implements Iterable<FilteredTag> {

    /**
     * Shorthand for constructing a new filter with the mode set to {@link FilterMode#INCLUDE
     * INCLUDE}, and calling {@link #addTags(String...) addTags(...)} with the {@code
     * includedTags}.
     */
    public static NBTFilter with(String... includedTags) {
        NBTFilter filter = new NBTFilter(FilterMode.INCLUDE);
        filter.addTags(includedTags);
        return filter;
    }

    /**
     * Shorthand for constructing a new filter with the mode set to {@link FilterMode#EXCLUDE
     * EXCLUDE}, and calling {@link #addTags(String...) addTags(...)} with the {@code
     * excludedTags}.
     */
    public static NBTFilter without(String... excludedTags) {
        NBTFilter filter = new NBTFilter(FilterMode.EXCLUDE);
        filter.addTags(excludedTags);
        return filter;
    }

    private final FilterMode mode;
    private final Set<FilteredTag> filteredTags;

    /**
     * Creates a new filter whose behaviour is determined by the provided {@code mode}.
     *
     * @see FilterMode#INCLUDE INCLUDE
     * @see FilterMode#EXCLUDE EXCLUDE
     */
    public NBTFilter(FilterMode mode) {
        this.mode = mode;
        filteredTags = new HashSet<>();
    }

    /**
     * @return The filter's mode, which determines how its {@link #getFilteredTags() tags} are
     * applied to filtered objects.
     * @see FilterMode#INCLUDE INCLUDE
     * @see FilterMode#EXCLUDE EXCLUDE
     */
    public FilterMode getMode() {
        return mode;
    }

    /**
     * @return All tags that are included or excluded from the filter, depending on the {@link
     * #getMode() mode}. Tags added via {@link #addTags(String...) addTags(...)} or the static
     * {@link #with(String...) with(...)} and {@link #without(String...) without(...)} constructors
     * will be returned here, unless removed via {@link #removeFilteredTags(String...)
     * removeFilteredTags(...)}.
     */
    public Set<FilteredTag> getFilteredTags() {
        return new HashSet<>(filteredTags);
    }

    /**
     * Creates a new filter that contains all tags in the current filter that {@link
     * FilteredTag#isExtendedBy(FilteredTag, int) extend} the {@code base} tag up to the provided
     * {@code depth}. The returned filter will always have the same {@link #getMode() mode} as the
     * current filter.
     */
    public NBTFilter subFilter(FilteredTag base, int depth) {
        String[] baseTokens = base.getTokens();
        if (depth > baseTokens.length) {
            throw new IllegalArgumentException("Base tag \"" + base +
                                               "\" does not reach depth " + depth);
        }

        NBTFilter sub = new NBTFilter(mode);
        for (FilteredTag tag : filteredTags) {
            if (base.isExtendedBy(tag, depth)) {
                sub.filteredTags.add(tag);
            }
        }

        // Only include the base itself it it has more tokens beyond the provided `depth`.
        if (depth < baseTokens.length) {
            sub.filteredTags.add(base);
        }
        return sub;
    }

    /**
     * @return {@code true} if the filter has no {@link #getFilteredTags() tags} included or
     * excluded. Otherwise {@code false}.
     */
    public boolean isEmpty() {
        return filteredTags.isEmpty();
    }

    /**
     * @return An iterator over each of the filter's added {@link #getFilteredTags() tags}.
     */
    @Override
    public Iterator<FilteredTag> iterator() {
        return filteredTags.iterator();
    }

    /**
     * Adds each of the {@code tagNames} to the filter.
     * <p><br>
     * If the filter's mode is set to {@link FilterMode#INCLUDE INCLUDE}, then tags added via this
     * method are the only ones allowed in compounds passed through the filter. Otherwise, if the
     * mode is {@link FilterMode#EXCLUDE EXCLUDE}, then compounds passed through the filter will
     * never include tags with these names. Filters are dynamic though, so both of those may change
     * if tags are {@link #removeFilteredTags(String...) removed} from the filter, or if another tag
     * takes precedence during collision.
     * <p><br>
     * Tag names may reference a compound's direct children, as well as nested tags (compounds
     * inside compounds etc). When referencing nested tags, dot-notation is used, similar to how
     * fully-qualified names are formatted in Java. If a tag name has dots ({@code .}) in it
     * literally, they can be escaped with a double-backslash ({@code \\.}).
     * <p>
     * Examples of this format include...
     * <pre>
     * -{@code "DataVersion"} —> a chunk's DataVersion tag
     * -{@code "Level.Sections"} —> all of a chunk's sections
     * -{@code "Level.Sections.Palette"} —> only the "Palette" tag inside each of the chunk's sections
     * </pre>
     * Any duplicate tags, and tags already included in the filter, will not be added. If one tag's
     * name begins with the entirety of another's, and it has more tokens / parts after that, the
     * two tags are considered to be colliding. Some notes about tag collision:
     * <pre>
     *     • New tags always take precedence when collision happens, such that...
     *     • If an added tag has a <u>wider scope</u> (fewer tokens) than any existing tags, all
     *       colliding tags with narrower scopes (more tokens) will be removed, and the new tag will
     *       be added.
     *     • If the added tag has a <u>narrower scope</u> (more tokens) than an existing one, then
     *       that new tag will replace the tag with the wider scope (fewer tokens).
     * </pre>
     *
     * @throws NullPointerException If the supplied {@code tagNames} array or any of its elements
     *                              are {@code null}.
     */
    public void addTags(String... tagNames) {
        Objects.requireNonNull(tagNames, "tagNames array cannot be null");

        // Check for collisions.
        for (String rawName : tagNames) {
            Objects.requireNonNull(rawName, "tagNames array cannot contain null names");

            FilteredTag tag = new FilteredTag(rawName);
            boolean shouldAddTag = true;
            Iterator<FilteredTag> existingTags = filteredTags.iterator();

            while (existingTags.hasNext()) {
                FilteredTag existingTag = existingTags.next();

                // Ignore duplicate tags.
                if (existingTag.equals(tag)) {
                    shouldAddTag = false;
                    break;
                }

                // Check if the new tag collides with the existing tag's scope.
                if (tag.isExtendedBy(existingTag)) {
                    // Replace & continue, since there can be multiple tags with narrower scopes.
                    existingTags.remove();
                } else if (existingTag.isExtendedBy(tag)) {
                    // Replace & break, since only 1 tag can possibly have a wider scope.
                    existingTags.remove();
                    break;
                }
            }

            if (shouldAddTag) {
                filteredTags.add(tag);
            }
        }
    }

    /**
     * Removes certain {@code tagNames} from the filter, such that {@link #getFilteredTags()} will
     * no longer return tags with those {@link FilteredTag#getName() names} unless they are
     * explicitly re-{@link #addTags(String...) added}.
     *
     * @throws NullPointerException If the supplied {@code tagNames} array or any of its elements
     *                              are {@code null}.
     */
    public void removeFilteredTags(String... tagNames) {
        Objects.requireNonNull(tagNames, "tagNames array cannot be null");

        for (String name : tagNames) {
            Objects.requireNonNull(name, "tagNames array cannot contain null names");
            filteredTags.removeIf(tag -> tag.getName().equals(name));
        }
    }
}