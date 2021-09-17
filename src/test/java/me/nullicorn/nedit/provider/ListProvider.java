package me.nullicorn.nedit.provider;

import static me.nullicorn.nedit.provider.CompoundProvider.getTestEncoder;

import java.util.function.Supplier;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * @author Nullicorn
 */
public final class ListProvider extends ArrayArgumentsProvider {

    @Override
    NBTList[] provide() {
        return new NBTList[]{
            new NBTList(TagType.END),
            generateListOfDoubles(999),
            generateListOfCompounds(512)
        };
    }

    public static NBTList generateListOfDoubles(int size) {
        NBTList list = new NBTList(TagType.DOUBLE);
        for (int i = 0; i < size; i++) {
            list.add(Math.pow(i, Math.PI * Math.PI + i));
        }
        return list;
    }

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

    public static final class IOProvider extends IOBasedArgumentsProvider<NBTList> {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return ListProvider::new;
        }

        @Override
        Encoder<NBTList> encoder() {
            return (out, list) -> {
                TagType contentType = list.getContentType();
                Encoder<Object> encoder = getTestEncoder(contentType);

                out.writeByte(contentType.getId());
                out.writeInt(list.size());
                for (Object value : list) {
                    encoder.encode(out, value);
                }
            };
        }
    }
}
