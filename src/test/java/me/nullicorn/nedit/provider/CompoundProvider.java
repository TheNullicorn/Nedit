package me.nullicorn.nedit.provider;

import java.util.function.Supplier;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import me.nullicorn.nedit.type.TagType;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class CompoundProvider extends NBTValueProvider {

    @Override
    NBTCompound[] provide() {
        return new NBTCompound[]{
            new NBTCompound(),
            generateSimple(),
            generateComplex(false),
            generateComplex(true)
        };
    }

    /**
     * Generates a "simple"-structured NBT compound. The returned compound contains no nested
     * compounds, lists, or arrays.
     */
    public static NBTCompound generateSimple() {
        NBTCompound sample = new NBTCompound();
        sample.put("name", "Nedit Appleseed");
        sample.put("id", (long) 1337);
        return sample;
    }

    /**
     * Generates an NBT compound that contains at least one tag of each {@link TagType} (besides
     * {@code END}). If {@code nestSelf == true}, the returned compound will also contain a copy of
     * itself, except without any further nested compounds.
     */
    public static NBTCompound generateComplex(boolean nestSelf) {
        NBTCompound sample = new NBTCompound();

        // Sample string tag.
        sample.put("player_name", "Nedit Appleseed");

        // Sample numeric tags.
        sample.put("flags", (byte) 0b00011001);
        sample.put("entity_id", (short) 502);
        sample.put("last_joined_ip", 167772161); // (bits for 10.0.0.1)
        sample.put("last_joined_timestamp", 1_631_907_935_514L);
        sample.put("eye_height", 2.5f);
        sample.put("hit_accuracy", 0.628035902d);

        // Sample array tags (got lazy with the names).
        sample.put("some_bytes", ByteArrayProvider.generateBytes(1024));
        sample.put("some_ints", IntArrayProvider.generateInts(256));
        sample.put("some_longs", LongArrayProvider.generateLongs(128));

        // Sample list tags.
        sample.put("empty_list", new NBTList(TagType.END));
        sample.put("double_list", ListProvider.generateListOfDoubles(128));
        sample.put("compound_list", ListProvider.generateListOfDoubles(64));

        if (nestSelf) {
            sample.put("nested_compound", generateComplex(false));
        }

        return sample;
    }

    public static final class IOProvider extends NBTEncodedValueProvider<NBTCompound> {

        @Override
        Supplier<ArgumentsProvider> provider() {
            return CompoundProvider::new;
        }

        @Override
        NBTEncoder<NBTCompound> encoder() {
            return (out, compound) -> {
                for (String name : compound.keySet()) {
                    Object value = compound.get(name);
                    TagType type = TagType.fromObject(value);

                    // Encode the tag's type, name, and value.
                    out.write(type.getId());
                    out.writeUTF(name);
                    getTestEncoder(type).encode(out, value);
                }
            };
        }
    }

    /**
     * Returns a test function that can be used to NBT-encode tags of the supplied {@code type}.
     */
    @SuppressWarnings("unchecked")
    static <T> NBTEncoder<T> getTestEncoder(TagType type) {
        NBTEncodedValueProvider<?> provider;

        switch (type) {
            case BYTE:
                provider = new ByteProvider.IOProvider();
                break;

            case SHORT:
                provider = new ShortProvider.IOProvider();
                break;

            case INT:
                provider = new IntProvider.IOProvider();
                break;

            case LONG:
                provider = new LongProvider.IOProvider();
                break;

            case BYTE_ARRAY:
                provider = new ByteArrayProvider.IOProvider();
                break;

            case INT_ARRAY:
                provider = new IntArrayProvider.IOProvider();
                break;

            case LONG_ARRAY:
                provider = new LongArrayProvider.IOProvider();
                break;

            case STRING:
                provider = new StringProvider.IOProvider();
                break;

            case LIST:
                provider = new ListProvider.IOProvider();
                break;

            case COMPOUND:
                provider = new CompoundProvider.IOProvider();
                break;

            default:
                throw new IllegalArgumentException("Unable to find test encoder for tag: " + type);
        }

        return (NBTEncoder<T>) provider.encoder();
    }
}
