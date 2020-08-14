package me.nullicorn.nedit.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
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
