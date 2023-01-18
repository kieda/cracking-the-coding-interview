package common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tuple3<X, Y, Z> extends Tuple2<X, Y>{
    private Z third;
    public Tuple3(X first, Y second, Z third) {
        super(first, second);
        this.third = third;
    }
    public Z getThird() {
        return third;
    }
    public void setThird(Z third) {
        this.third = third;
    }

    @Override
    public Object[] getItems() {
        return new Object[]{getFirst(), getSecond(), getThird()};
    }

    public static <A, B, C> Tuple3<A, B, C> make(A first, B second, C third) {
        return new Tuple3<>(first, second, third);
    }

    public static <A, B, C> List<Tuple3<A, B, C>> of3(Object... items) {
        int finalLength = items.length / 3;
        List<Tuple3<A, B, C>> result = new ArrayList<>(finalLength);
        for(int i = 0; i < finalLength; i++) {
            result.add(new Tuple3<A, B, C> (
                    (A) items[i * 3], (B) items[i * 3 + 1], (C) items[i * 3 + 2]
            ));
        }
        return result;
    }

    public static <A, B, C> List<Tuple3<A, B, C>> zip(Iterable<A> firsts, Iterable<B> seconds, Iterable<C> thirds) {
        Iterator<A> firstLoop = firsts.iterator();
        Iterator<B> secondLoop = seconds.iterator();
        Iterator<C> thirdLoop = thirds.iterator();
        List<Tuple3<A, B, C>> result = new ArrayList<>();
        while(firstLoop.hasNext() && secondLoop.hasNext() && thirdLoop.hasNext()) {
            result.add(new Tuple3<>(firstLoop.next(), secondLoop.next(), thirdLoop.next()));
        }
        return result;
    }
}
