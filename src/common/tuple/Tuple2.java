package common.tuple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Tuple2<U, V> extends Tuple1<U>{
    private V second;
    public Tuple2(U first, V second) {
        super(first);
        this.second = second;
    }
    public V getSecond() {
        return second;
    }

    public void setSecond(V second) {
        this.second = second;
    }

    @Override
    public Object[] getItems() {
        return new Object[]{getFirst(), getSecond()};
    }

    public static <Y, Z> Tuple2<Y, Z> make(Y first, Z second) {
        return new Tuple2<>(first, second);
    }

    public static <Y, Z> List<Tuple2<Y, Z>> of2(Object... items) {
        int finalLength = items.length / 2;
        List<Tuple2<Y, Z>> result = new ArrayList<>(finalLength);
        for(int i = 0; i < finalLength; i ++) {
            result.add(new Tuple2<Y, Z> (
                (Y) items[i * 2], (Z) items[i * 2 + 1]
            ));
        }
        return result;
    }

    public static <X, Y> List<Tuple2<X, Y>> zip(Iterator<X> firsts, Iterator<Y> seconds) {
        List<Tuple2<X, Y>> result = new ArrayList<>();
        while(firsts.hasNext() && seconds.hasNext()) {
            result.add(new Tuple2<>(firsts.next(), seconds.next()));
        }
        return result;
    }

    /**
     * Like zip, but we check that the inputs are of equal length rather than truncating
     */
    public static <X, Y> List<Tuple2<X, Y>> zipe(Iterator<X> firsts, Iterator<Y> seconds) {
        List<Tuple2<X, Y>> result = zip(firsts, seconds);
        if(firsts.hasNext())
            throw new NoSuchElementException("First item in zip has more elements");
        if(seconds.hasNext())
            throw new NoSuchElementException("Second item in zip has more elements");
        return result;
    }
}
