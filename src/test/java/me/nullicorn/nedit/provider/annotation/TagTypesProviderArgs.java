package me.nullicorn.nedit.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.nullicorn.nedit.provider.TagTypesProvider;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * Modifies the behavior of test methods using {@link TagTypesProvider} as an {@link ArgumentsSource
 * arguments source}.
 *
 * @author Nullicorn
 * @see TagTypesProvider
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TagTypesProviderArgs {

    /**
     * Indicates whether or not {@link TagType#END TAG_End} should be provided or not.
     * <p><br>
     * This may be desired for tests involving values of tags, since {@code TAG_End} is the only tag
     * that cannot have a value.
     */
    boolean skipEndTag() default false;

    /**
     * Indicates whether or not to also provide the {@link TagType#getId() identifier} of each
     * provided {@code TagType}.
     * <p><br>
     * If {@code true}, a second {@code int} argument will be provided indicating the correct
     * identifier for the {@code TagType} (first argument).
     */
    boolean includeIdentifiers() default false;

    @SuppressWarnings("ClassExplicitlyAnnotation")
    class Defaults implements TagTypesProviderArgs {

        @Override
        public boolean skipEndTag() {
            return false;
        }

        @Override
        public boolean includeIdentifiers() {
            return false;
        }

        @Override
        public Class<TagTypesProviderArgs> annotationType() {
            return TagTypesProviderArgs.class;
        }
    }
}
