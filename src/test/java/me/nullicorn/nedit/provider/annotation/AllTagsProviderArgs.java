package me.nullicorn.nedit.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.nullicorn.nedit.provider.AllTagsProvider;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * Modifies the behavior of test methods using {@link AllTagsProvider} as an {@link ArgumentsSource
 * arguments source}.
 * <p><br>
 * The values defined by this annotation determine the number of arguments, the type of each
 * argument, and how many times arguments are provided. The pseudo-code below describes the current
 * behavior:
 * <pre>{@code
 * if (groupAsOne) {
 *     if (provideTypes) {
 *         No. of Args: 1
 *         Classes: [Map<Object, TagType>]
 *         Runs: Once
 *     } else {
 *         No. of Args: 1
 *         Classes: [Set<Object>]
 *         Runs: Once
 *     }
 * } else if (groupByType) {
 *     if (provideTypes) {
 *         No. of Args: 2
 *         Classes: [Set<Object>, TagType]
 *         Runs: Several times
 *     } else {
 *         No. of Args: 1
 *         Classes: [Set<Object>]
 *         Runs: Several times
 *     }
 * } else if (provideTypes) {
 *     No. of Args: 2
 *     Classes: [Object, TagType]
 *     Runs: Several times
 * } else {
 *     No. of Args: 1
 *     Classes: [Object]
 *     Runs: Several times
 * }
 * }</pre>
 *
 * @author Nullicorn
 * @see AllTagsProvider
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AllTagsProviderArgs {

    /**
     * Indicates whether or not every tag that would normally be provided on separate runs should be
     * provided in a singular collection in one run.
     * <p><br>
     * If {@code true}, the first argument provided will be a {@code Set<Object>} containing valid
     * NBT values.
     * <p><br>
     * If {@link #provideTypes()} is also {@code true}, then the first argument will be a {@code
     * Map<Object, TagType>}, where each key in the map is a valid NBT value, and each value is the
     * correct {@link TagType} for the corresponding key.
     * <p><br>
     * <b>This is NOT compatible with {@link #groupByType()}</b>.
     *
     * @see #provideTypes()
     */
    boolean groupAsOne() default false;

    /**
     * Indicates whether or not all values with the same {@code TagType} should be provided in a
     * collection on the same run.
     * <p><br>
     * If {@code true}, the first argument provided will be a {@code Set<Object>} containing valid
     * NBT values, all for the same unspecified {@code TagType}.
     * <p><br>
     * If {@link #provideTypes()} is also {@code true}, then a second {@link TagType} argument will
     * be provided, indicating the correct TagType of every tag in the collection.
     */
    boolean groupByType() default false;

    /**
     * Indicates whether or not the correct {@link TagType} for each provided value should also be
     * included.
     * <p><br>
     * If this is the only flag that is {@code true}, then a second {@link TagType} argument will be
     * provided on each run, indicating the correct {@code TagType} for the first argument ({@code
     * Object}).
     * <p><br>
     * Otherwise if any other flags are {@code true}, see the documentation for those flags for
     * information on how this flag may modify their output.
     *
     * @see #groupAsOne()
     * @see #groupByType()
     */
    boolean provideTypes() default false;

    @SuppressWarnings("ClassExplicitlyAnnotation")
    class Defaults implements AllTagsProviderArgs {

        @Override
        public boolean groupAsOne() {
            return false;
        }

        @Override
        public boolean groupByType() {
            return false;
        }

        @Override
        public boolean provideTypes() {
            return false;
        }

        @Override
        public Class<AllTagsProviderArgs> annotationType() {
            return AllTagsProviderArgs.class;
        }
    }
}
