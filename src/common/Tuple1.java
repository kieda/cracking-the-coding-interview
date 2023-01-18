package common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Essentially a wrapper for an object. Useful for pointer references, for example a Boolean type
 * @param <U> item
 */
public class Tuple1<U> extends Tuple0{
    private U first;
    public Tuple1(U first) {
        this.first = first;
    }

    public U getFirst() {
        return first;
    }

    public void setFirst(U first) {
        this.first = first;
    }

    @Override
    public Object[] getItems() {
        return new Object[]{ getFirst() };
    }

    public static <X> Tuple1<X> make(X item) {
        return new Tuple1<>(item);
    }

    public static <X> List<Tuple1<X>> of1(X... items) {
        return Arrays.stream(items).map(Tuple1::new).collect(Collectors.toList());
    }

    public static <X> List<Tuple1<X>> zip(Iterable<X> items) {
        return StreamSupport.stream(items.spliterator(), false).
                map(Tuple1::new).collect(Collectors.toList());
    }
}
