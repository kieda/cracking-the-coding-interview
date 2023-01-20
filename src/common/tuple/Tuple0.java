package common.tuple;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

public class Tuple0 implements Tuple{
    private final Object[] ITEMS = new Object[0];
    private static final Tuple0 INSTANCE = new Tuple0();

    protected Tuple0() {}

    @Override
    public Object[] getItems() {
        return ITEMS;
    }

    public static Tuple0 make() {
        return INSTANCE;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Tuple))
            return false;
        return Arrays.deepEquals(this.getItems(), ((Tuple)other).getItems());
    }
    //Function<Object, String> a = (int[] ints) -> Arrays.toString(ints);
    private static final Map<Class, Function<Object, String>> primitiveArraysToString = Map.of(
        byte[].class, o -> Arrays.toString((byte[])o),
        boolean[].class, o -> Arrays.toString((boolean[])o),
        short[].class, o -> Arrays.toString((short[])o),
        char[].class, o -> Arrays.toString((char[])o),
        int[].class, o -> Arrays.toString((int[])o),
        float[].class, o -> Arrays.toString((float[])o),
        long[].class, o -> Arrays.toString((long[])o),
        double[].class, o -> Arrays.toString((double[])o)
    );
    @Override
    public String toString() {
        StringJoiner str = new StringJoiner(", ", "(", ")");
        for(Object item : getItems()) {
            String formatted;
            if(item == null) {
                formatted = Objects.toString(item);
            } else if(primitiveArraysToString.containsKey(item.getClass())) {
                formatted = primitiveArraysToString.get(item.getClass()).apply(item);
            } else if(item instanceof Object[]) {
                formatted = Arrays.deepToString((Object[])item);
            } else {
                formatted = Objects.toString(item);
            }
            str.add(formatted);
        }
        return str.toString();
    }
}
