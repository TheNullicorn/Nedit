package me.nullicorn.nedit.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.nullicorn.nedit.provider.AllTagsProvider;

/**
 * A marker annotation used in conjunction with the {@link AllTagsProvider} arguments source. For
 * usage, see the provider's {@link AllTagsProvider documentation}.
 *
 * @author Nullicorn
 * @see ProvideAllAtOnce
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProvideTagTypes {
    // Marker annotation; no members.
}
