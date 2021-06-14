package me.nullicorn.nedit.filter;

/**
 * Determines how a {@link NBTFilter} treats objects passed through it with regard to the {@link
 * NBTFilter#getFilteredTags() tags} added to the filter.
 *
 * @author Nullicorn
 */
public enum FilterMode {
    /**
     * Indicates that any tag names added to a filter are the <u>only tags</u> that compounds passed
     * through the filter should contain.
     */
    INCLUDE,

    /**
     * Indicates that compounds passed through a filter should <u>never</u> contain any of the tags
     * added to the filter.
     */
    EXCLUDE
}
