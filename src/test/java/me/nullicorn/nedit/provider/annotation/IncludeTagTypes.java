package me.nullicorn.nedit.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.nullicorn.nedit.provider.AllTagsProvider;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * Indicates whether an {@link AllTagsProvider} should provide a second argument indicating the
 * corresponding {@link TagType} of the primary argument.
 *
 * @author Nullicorn
 * @see ArgumentsProvider
 * @see AllTagsProvider
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IncludeTagTypes {
    // Marker annotation; no members.
}
