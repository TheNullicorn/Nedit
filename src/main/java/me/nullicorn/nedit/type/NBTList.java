package me.nullicorn.nedit.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * An ordered sequence of NBT tags, all with the same {@link TagType}. Lists of 1 or more tags
 * cannot have a content type of {@link TagType#END END}. Otherwise, if the list is empty, it is
 * allowed.
 *
 * @author Nullicorn
 */
public class NBTList extends ArrayList<Object> {

    private final TagType contentType;

    /**
     * Creates a new list that can only hold tags with the provided {@code type}.
     */
    public NBTList(TagType type) {
        contentType = (type == null ? TagType.END : type);
    }

    /**
     * The type of tags stored in the list. Attempting to insert any other type of tag will cause an
     * exception to be thrown.
     */
    public TagType getContentType() {
        return contentType;
    }

    /**
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is set to
     *                               {@link TagType#END END}, indicating the list should always be
     *                               empty.
     */
    @Override
    public Object set(int index, Object tag) {
        checkType(tag);
        return super.set(index, tag);
    }

    /**
     * Appends a nameless NBT {@code tag} to the end of the list. The value's {@link
     * TagType#getRuntimeType() runtime type} much match the list's overall {@link #getContentType()
     * content type}, or else an exception will be thrown.
     *
     * @throws IllegalArgumentException If the {@code tag} is null, or if its class is incompatible
     *                                  with the list's content type.
     * @throws IllegalStateException    If the list's {@link #getContentType() content type} is set
     *                                  to {@link TagType#END END}, indicating the list should
     *                                  always be empty.
     */
    @Override
    public boolean add(Object tag) {
        checkType(tag);
        return super.add(tag);
    }

    /**
     * Inserts a nameless NBT {@code tag} at the {@code index} in the list. This will cause any tags
     * previously at or beyond that index to be shifted up one index. The value's {@link
     * TagType#getRuntimeType() runtime type} much match the list's overall {@link #getContentType()
     * content type}, or else an exception will be thrown.
     *
     * @param index The zero-based index in the list where the new tag will be inserted.
     * @throws IllegalArgumentException If the {@code tag} is null, or if its class is incompatible
     *                                  with the list's content type.
     * @throws IllegalStateException    If the list's {@link #getContentType() content type} is set
     *                                  to {@link TagType#END END}, indicating the list should
     *                                  always be empty.
     */
    @Override
    public void add(int index, Object tag) {
        checkType(tag);
        super.add(index, tag);
    }

    /**
     * Appends any provided NBT {@code tags} to the end of the list. All added tags must match the
     * list's overall {@link #getContentType() content type}, or else an exception will be thrown.
     *
     * @throws IllegalArgumentException If the provided collection is {@code null}, or if any of the
     *                                  tags in the collection are {@code null}. Also thrown if any
     *                                  element in the collection has a class different from the
     *                                  {@link TagType#getRuntimeType() runtime type} of the list's
     *                                  {@link #getContentType() contents}.
     * @throws IllegalStateException    If the list's {@link #getContentType() content type} is set
     *                                  to {@link TagType#END END}, indicating the list should
     *                                  always be empty.
     */
    @Override
    public boolean addAll(Collection<?> tags) {
        tags.forEach(this::checkType);
        return super.addAll(tags);
    }

    /**
     * Inserts any provided NBT {@code tags} into the list at a given {@code index}. Any tags
     * previously at or beyond that index will have their indices increased by the added
     * collection's {@link Collection#size() size}.
     *
     * @param index The zero-based index in the list where the first {@code tag} in the collection
     *              will be added.
     * @throws IllegalArgumentException If the provided collection is {@code null}, or if any of the
     *                                  tags in the collection are {@code null}. Also thrown if any
     *                                  element in the collection has a class different from the
     *                                  {@link TagType#getRuntimeType() runtime type} of the list's
     *                                  {@link #getContentType() contents}.
     * @throws IllegalStateException    If the list's {@link #getContentType() content type} is set
     *                                  to {@link TagType#END END}, indicating the list should
     *                                  always be empty.
     */
    @Override
    public boolean addAll(int index, Collection<?> tags) {
        tags.forEach(this::checkType);
        return super.addAll(index, tags);
    }

    /*
     *
     * ============ GETTER METHODS ============
     *
     */

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#BYTE BYTE}.
     * @see #get(int)
     */
    public Byte getByte(int index) {
        checkGetType(TagType.BYTE);
        return (Byte) get(index);
    }

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#SHORT SHORT}.
     * @see #get(int)
     */
    public Short getShort(int index) {
        checkGetType(TagType.SHORT);
        return (Short) get(index);
    }

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#INT INT}.
     * @see #get(int)
     */
    public Integer getInt(int index) {
        checkGetType(TagType.INT);
        return (Integer) get(index);
    }

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#LONG LONG}.
     * @see #get(int)
     */
    public Long getLong(int index) {
        checkGetType(TagType.LONG);
        return (Long) get(index);
    }

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#FLOAT FLOAT}.
     * @see #get(int)
     */
    public Float getFloat(int index) {
        checkGetType(TagType.FLOAT);
        return (Float) get(index);
    }

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#DOUBLE DOUBLE}.
     * @see #get(int)
     */
    public Double getDouble(int index) {
        checkGetType(TagType.DOUBLE);
        return (Double) get(index);
    }

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#STRING STRING}.
     * @see #get(int)
     */
    public String getString(int index) {
        checkGetType(TagType.STRING);
        return (String) get(index);
    }

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#BYTE_ARRAY BYTE_ARRAY}.
     * @see #get(int)
     */
    public byte[] getByteArray(int index) {
        checkGetType(TagType.BYTE_ARRAY);
        return (byte[]) get(index);
    }

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#INT_ARRAY INT_ARRAY}.
     * @see #get(int)
     */
    public int[] getIntArray(int index) {
        checkGetType(TagType.INT_ARRAY);
        return (int[]) get(index);
    }

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#LONG_ARRAY LONG_ARRAY}.
     * @see #get(int)
     */
    public long[] getLongArray(int index) {
        checkGetType(TagType.LONG_ARRAY);
        return (long[]) get(index);
    }

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#LIST LIST}.
     * @see #get(int)
     */
    public NBTList getList(int index) {
        checkGetType(TagType.LIST);
        return (NBTList) get(index);
    }

    /**
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#COMPOUND COMPOUND}.
     * @see #get(int)
     */
    public NBTCompound getCompound(int index) {
        checkGetType(TagType.COMPOUND);
        return (NBTCompound) get(index);
    }

    /*
     *
     * ============ FOREACH METHODS ============
     *
     */

    /**
     * Iterates over every byte in the list using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#BYTE BYTE}.
     */
    public void forEachByte(Consumer<Byte> action) {
        forEachOfType(action, TagType.BYTE);
    }

    /**
     * Consumes every short in the list using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#SHORT SHORT}.
     */
    public void forEachShort(Consumer<Short> action) {
        forEachOfType(action, TagType.SHORT);
    }

    /**
     * Iterates over every int in the list using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#INT INT}.
     */
    public void forEachInt(Consumer<Integer> action) {
        forEachOfType(action, TagType.INT);
    }

    /**
     * Iterates over every long in the list using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#LONG LONG}.
     */
    public void forEachLong(Consumer<Long> action) {
        forEachOfType(action, TagType.LONG);
    }

    /**
     * Iterates over every float in the list using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#FLOAT FLOAT}.
     */
    public void forEachFloat(Consumer<Float> action) {
        forEachOfType(action, TagType.FLOAT);
    }

    /**
     * Iterates over every double in the list using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#DOUBLE DOUBLE}.
     */
    public void forEachDouble(Consumer<Double> action) {
        forEachOfType(action, TagType.DOUBLE);
    }

    /**
     * Iterates over every string in the list using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#STRING STRING}.
     */
    public void forEachString(Consumer<String> action) {
        forEachOfType(action, TagType.STRING);
    }

    /**
     * Iterates over every byte array in the list using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#BYTE_ARRAY BYTE_ARRAY}.
     */
    public void forEachByteArray(Consumer<byte[]> action) {
        forEachOfType(action, TagType.BYTE_ARRAY);
    }

    /**
     * Iterates over every int array in the list using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#INT_ARRAY INT_ARRAY}.
     */
    public void forEachIntArray(Consumer<int[]> action) {
        forEachOfType(action, TagType.INT_ARRAY);
    }

    /**
     * Iterates over every long array in the list using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#LONG_ARRAY LONG_ARRAY}.
     */
    public void forEachLongArray(Consumer<long[]> action) {
        forEachOfType(action, TagType.LIST);
    }

    /**
     * Iterates over every nested list (inside the current list) using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#LIST LIST}.
     */
    public void forEachList(Consumer<NBTList> action) {
        forEachOfType(action, TagType.LIST);
    }

    /**
     * Iterates over every compound in the list using the provided {@code action}.
     *
     * @throws IllegalStateException If the list's {@link #getContentType() content type} is not
     *                               {@link TagType#COMPOUND COMPOUND}.
     */
    public void forEachCompound(Consumer<NBTCompound> action) {
        forEachOfType(action, TagType.COMPOUND);
    }

    /**
     * Same as {@link #forEach(Consumer)} but with a customizable type parameter
     */
    @SuppressWarnings("unchecked")
    private <T> void forEachOfType(Consumer<T> action, TagType type) {
        checkGetType(type);
        forEach(element -> action.accept((T) element));
    }

    /**
     * Throw an exception if the {@code attemptedType} does not match the list's {@link
     * #getContentType() content type}.
     */
    private void checkGetType(TagType attemptedType) {
        if (attemptedType != contentType) {
            throw new IllegalStateException("Cannot get " + attemptedType +
                                            " from a list of " + contentType.toString() + "s");
        }
    }

    /**
     * Throw an exception if...
     * <ul>
     *     <li>The list's {@link #getContentType() content-type} is set to {@link TagType#END}. ({@link IllegalStateException})</li>
     *     <li>The {@code tag} is null. ({@link IllegalArgumentException})</li>
     *     <li>The {@code tag}'s class is different from the {@link #getContentType() content-type}'s {@link TagType#getRuntimeType() runtime type}. ({@link IllegalArgumentException})</li>
     * </ul>
     */
    private void checkType(Object tag) {
        if (contentType == TagType.END) {
            throw new IllegalStateException("Cannot add tags to a list with content-type TAG_End");
        } else if (tag == null) {
            throw new IllegalArgumentException("Cannot add null tags to NBT list");
        } else if (!tag.getClass().equals(contentType.getRuntimeType())) {
            throw new IllegalArgumentException(String.format(
                "Expected %s but found %s",
                contentType.getRuntimeType(), tag.getClass()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        NBTList objects = (NBTList) o;
        if (contentType != objects.contentType) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contentType);
    }
}
