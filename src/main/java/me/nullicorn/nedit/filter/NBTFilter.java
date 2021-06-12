package me.nullicorn.nedit.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class NBTFilter implements Iterable<FilteredTag> {

    public static NBTFilter with(String... includedTags) {
        NBTFilter filter = new NBTFilter(FilterMode.INCLUDE);
        filter.addTags(includedTags);
        return filter;
    }

    public static NBTFilter without(String... excludedTags) {
        NBTFilter filter = new NBTFilter(FilterMode.EXCLUDE);
        filter.addTags(excludedTags);
        return filter;
    }

    private final FilterMode       mode;
    private final Set<FilteredTag> filteredTags;

    public NBTFilter(FilterMode mode) {
        this.mode = mode;
        filteredTags = new HashSet<>();
    }

    public FilterMode getMode() {
        return mode;
    }

    public Set<FilteredTag> getFilteredTags() {
        return new HashSet<>(filteredTags);
    }

    public NBTFilter subFilter(FilteredTag base, int depth) {
        String[] baseTokens = base.getTokens();
        if (depth > baseTokens.length) {
            throw new IllegalArgumentException("Base tag \"" + base +
                                               "\" does not reach depth " + depth);
        }

        NBTFilter sub = new NBTFilter(mode);
        for (FilteredTag tag : filteredTags) {
            String[] tokens = tag.getTokens();
            if (tokens.length <= depth || tag.equals(base)) {
                continue;
            }

            boolean isExtension = true;
            for (int i = 0; i < depth; i++) {
                if (!tokens[i].equals(baseTokens[i])) {
                    isExtension = false;
                    break;
                }
            }

            if (isExtension) {
                sub.filteredTags.add(tag);
            }
        }

        if (depth < baseTokens.length) {
            sub.filteredTags.add(base);
        }
        return sub;
    }

    @NotNull
    @Override
    public Iterator<FilteredTag> iterator() {
        return filteredTags.iterator();
    }

    public boolean isEmpty() {
        return filteredTags.isEmpty();
    }

    public boolean shouldInclude(String name, int depth) {
        for (FilteredTag filtered : filteredTags) {
            boolean matches = name.equals(filtered.getTokens()[depth]);
            if (matches) {
                // Only matching tags pass through inclusion filters,
                // while matching tags never pass through exclusion filters.
                return mode == FilterMode.INCLUDE;
            }
        }

        // Tags that didn't match will not pass through inclusion filters,
        // but will pass through exclusion filters.
        return mode == FilterMode.EXCLUDE;
    }

    public void addTags(String... tagNames) {
        if (tagNames == null) {
            throw new IllegalArgumentException("Tag names cannot be null");
        }

        // Check for collisions.
        for (String rawName : tagNames) {
            if (rawName == null) {
                throw new IllegalArgumentException("Tag names cannot be null");
            }

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

    public void removeFilteredTags(String... tagNames) {
        if (tagNames == null) {
            throw new IllegalArgumentException("Cannot remove null tag names");
        }

        for (String name : tagNames) {
            if (name == null) {
                throw new IllegalArgumentException("Cannot remove null tag names");
            }
            filteredTags.removeIf(existingName -> existingName.toString().equals(name));
        }
    }
}