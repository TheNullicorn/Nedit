package me.nullicorn.nedit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import javax.xml.bind.TypeConstraintException;
import lombok.Getter;

/**
 * @author Nullicorn
 */
public class NBTList extends ArrayList<Object> {

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

    private void checkType(Object o) {
        if (!o.getClass().equals(contentType.getClazz())) {
            throw new TypeConstraintException(String.format("Expected %s but found %s", contentType.getClazz(), o.getClass()));
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
