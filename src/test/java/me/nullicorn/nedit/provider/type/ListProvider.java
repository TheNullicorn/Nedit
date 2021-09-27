package me.nullicorn.nedit.provider.type;

import static me.nullicorn.nedit.provider.type.CompoundProvider.getTestEncoder;

import java.util.function.Supplier;
import me.nullicorn.nedit.provider.TagProvider;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class ListProvider extends TagProvider<NBTList[]> {

    @Override
    public NBTList[] provide() {
        return new NBTList[]{
            new NBTList(TagType.END),
            generateListOfDoubles(999),
            generateListOfCompounds(512)
        };
    }

    @Override
    public Object getExtraneousValue() {
        return generateListOfDoubles(3);
    }

    /**
     * Generates a diverse NBT list of {@code double}s, whose {@link NBTList#size() size} is
     * determined by the argument with the same name.
     */
    public static NBTList generateListOfDoubles(int size) {
        NBTList list = new NBTList(TagType.DOUBLE);
        for (int i = 0; i < size; i++) {
            list.add(Math.pow(i, Math.PI * Math.PI + i));
        }
        return list;
    }

    /**
     * Generates an NBT list of {@code NBTCompound}s. The number of compounds in the returned list
     * is determined by the {@code size} argument.
     */
    public static NBTList generateListOfCompounds(int size) {
        NBTList list = new NBTList(TagType.COMPOUND);
        for (int i = 1; i <= size; i++) {
            NBTCompound element = new NBTCompound();
            element.put("name", "Entry #" + i);
            element.put("value", (float) (i * Math.E));
            list.add(element);
        }
        return list;
    }

    public static final class IOProvider extends NBTEncodedValueProvider {

        @Override
        public Supplier<ArgumentsProvider> provider() {
            return ListProvider::new;
        }

        @Override
        public NBTEncoder<NBTList> encoder() {
            return (out, list) -> {
                TagType contentType = list.getContentType();
                NBTEncoder<Object> encoder = getTestEncoder(contentType);

                out.writeByte(contentType.getId());
                out.writeInt(list.size());
                for (Object value : list) {
                    encoder.encode(out, value);
                }
            };
        }
    }
}
