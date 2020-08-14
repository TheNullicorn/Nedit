package me.nullicorn.nedit.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Getter;

/**
 * A class representing an NBT TAG_List
 *
 * @author Nullicorn
 */
public class NBTList extends ArrayList<Object> {

    /**
     * The TagType of all elements stored in this list. Attempting to insert any other type of element will cause an exception to be thrown
     */
    @Getter
    private final TagType contentType;

    public NBTList(TagType type) {
        this.contentType = (type == null ? TagType.END : type);
    }

    @Override
    public Object set(int index, Object element) {
        checkType(element);
        return super.set(index, element);
    }

    @Override
    public boolean add(Object o) {
        checkType(o);
        return super.add(o);
    }

    @Override
    public void add(int index, Object element) {
        checkType(element);
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<?> c) {
        c.forEach(this::checkType);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        c.forEach(this::checkType);
        return super.addAll(index, c);
    }

    /*
     *
     * ============ GETTER METHODS ============
     *
     */

    /**
     * @throws IllegalStateException If this this list cannot contain byte tags
     */
    public Byte getByte(int index) {
        checkGetType(TagType.BYTE);
        return (Byte) get(index);
    }

    /**
     * @throws IllegalStateException If this this list cannot contain short tags
     */
    public Short getShort(int index) {
        checkGetType(TagType.SHORT);
        return (Short) get(index);
    }

    /**
     * @throws IllegalStateException If this this list cannot contain integer tags
     */
    public Integer getInt(int index) {
        checkGetType(TagType.INT);
        return (Integer) get(index);
    }

    /**
     * @throws IllegalStateException If this this list cannot contain long tags
     */
    public Long getLong(int index) {
        checkGetType(TagType.LONG);
        return (Long) get(index);
    }

    /**
     * @throws IllegalStateException If this this list cannot contain float tags
     */
    public Float getFloat(int index) {
        checkGetType(TagType.FLOAT);
        return (Float) get(index);
    }

    /**
     * @throws IllegalStateException If this this list cannot contain double tags
     */
    public Double getDouble(int index) {
        checkGetType(TagType.DOUBLE);
        return (Double) get(index);
    }

    /**
     * @throws IllegalStateException If this this list cannot contain string tags
     */
    public String getString(int index) {
        checkGetType(TagType.STRING);
        return (String) get(index);
    }

    /**
     * @throws IllegalStateException If this this list cannot contain byte array tags
     */
    public Byte[] getByteArray(int index) {
        checkGetType(TagType.BYTE_ARRAY);
        return (Byte[]) get(index);
    }

    /**
     * @throws IllegalStateException If this this list cannot contain integer array tags
     */
    public Integer[] getIntArray(int index) {
        checkGetType(TagType.INT_ARRAY);
        return (Integer[]) get(index);
    }

    /**
     * @throws IllegalStateException If this this list cannot contain long array tags
     */
    public Long[] getLongArray(int index) {
        checkGetType(TagType.LONG_ARRAY);
        return (Long[]) get(index);
    }

    /**
     * @throws IllegalStateException If this this list cannot contain NBT list tags
     */
    public NBTList getList(int index) {
        checkGetType(TagType.LIST);
        return (NBTList) get(index);
    }

    /**
     * @throws IllegalStateException If this this list cannot contain NBT compound tags
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
     * Iterate through each byte in this list
     *
     * @throws IllegalStateException If this this list cannot contain byte tags
     */
    public void forEachByte(Consumer<Byte> action) {
        checkGetType(TagType.BYTE);
        forEachOfType(action);
    }

    /**
     * Iterate through each short in this list
     *
     * @throws IllegalStateException If this this list cannot contain short tags
     */
    public void forEachShort(Consumer<Short> action) {
        checkGetType(TagType.SHORT);
        forEachOfType(action);
    }

    /**
     * Iterate through each integer in this list
     *
     * @throws IllegalStateException If this this list cannot contain integer tags
     */
    public void forEachInt(Consumer<Integer> action) {
        checkGetType(TagType.INT);
        forEachOfType(action);
    }

    /**
     * Iterate through each long in this list
     *
     * @throws IllegalStateException If this this list cannot contain long tags
     */
    public void forEachLong(Consumer<Long> action) {
        checkGetType(TagType.LONG);
        forEachOfType(action);
    }

    /**
     * Iterate through each float in this list
     *
     * @throws IllegalStateException If this this list cannot contain float tags
     */
    public void forEachFloat(Consumer<Float> action) {
        checkGetType(TagType.FLOAT);
        forEachOfType(action);
    }

    /**
     * Iterate through each double in this list
     *
     * @throws IllegalStateException If this this list cannot contain double tags
     */
    public void forEachDouble(Consumer<Double> action) {
        checkGetType(TagType.DOUBLE);
        forEachOfType(action);
    }

    /**
     * Iterate through each string in this list
     *
     * @throws IllegalStateException If this this list cannot contain string tags
     */
    public void forEachString(Consumer<String> action) {
        checkGetType(TagType.STRING);
        forEachOfType(action);
    }

    /**
     * Iterate through each byte array in this list
     *
     * @throws IllegalStateException If this this list cannot contain byte array tags
     */
    public void forEachByteArray(Consumer<Byte[]> action) {
        checkGetType(TagType.BYTE_ARRAY);
        forEachOfType(action);
    }

    /**
     * Iterate through each integer array in this list
     *
     * @throws IllegalStateException If this this list cannot contain integer array tags
     */
    public void forEachIntArray(Consumer<Integer[]> action) {
        checkGetType(TagType.INT_ARRAY);
        forEachOfType(action);
    }

    /**
     * Iterate through each long array in this list
     *
     * @throws IllegalStateException If this this list cannot contain long array tags
     */
    public void forEachLongArray(Consumer<Long[]> action) {
        checkGetType(TagType.LONG_ARRAY);
        forEachOfType(action);
    }

    /**
     * Iterate through each NBT list in this list
     *
     * @throws IllegalStateException If this this list cannot contain list tags
     */
    public void forEachList(Consumer<NBTList> action) {
        checkGetType(TagType.LIST);
        forEachOfType(action);
    }

    /**
     * Iterate through each NBT compound in this list
     *
     * @throws IllegalStateException If this this list cannot contain compound tags
     */
    public void forEachCompound(Consumer<NBTCompound> action) {
        checkGetType(TagType.COMPOUND);
        forEachOfType(action);
    }

    /**
     * Same as {@link #forEach(Consumer)} but with a separate type parameter
     */
    private <T> void forEachOfType(Consumer<T> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;

        @SuppressWarnings("unchecked") final T[] elementData = (T[]) this.toArray();
        for (int i = 0; modCount == expectedModCount && i < this.size(); i++) {
            action.accept(elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Throw an exception if the specified type is not contained in this list
     */
    private void checkGetType(TagType attemptedType) {
        if (attemptedType != contentType) {
            throw new IllegalStateException("Cannot get " + attemptedType + " from a list of " + contentType.toString() + "s");
        }
    }

    /**
     * Throw an exception if the object should not be added to this list (its class does not match {@link #contentType}.{@link TagType#getClazz() getClazz()})
     */
    private void checkType(Object o) {
        if (!o.getClass().equals(contentType.getClazz())) {
            throw new IllegalArgumentException(String.format("Expected %s but found %s", contentType.getClazz(), o.getClass()));
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
