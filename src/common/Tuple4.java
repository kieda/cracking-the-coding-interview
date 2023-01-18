package common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tuple4<X, Y, Z, W> extends Tuple3<X, Y, Z>{
    private W fourth;
    public Tuple4(X first, Y second, Z third, W fourth) {
        super(first, second, third);
        this.fourth = fourth;
    }
    public W getFourth() {
        return fourth;
    }
    public void setFourth(W fourth) {
        this.fourth = fourth;
    }

    @Override
    public Object[] getItems() {
        return new Object[]{getFirst(), getSecond(), getThird(), getFourth()};
    }

    public static <A, B, C, D> Tuple4<A, B, C, D> make(A first, B second, C third, D fourth) {
        return new Tuple4<>(first, second, third, fourth);
    }

    public static <A, B, C, D> List<Tuple4<A, B, C, D>> of4(Object... items) {
        int finalLength = items.length / 4;
        List<Tuple4<A, B, C, D>> result = new ArrayList<>(finalLength);
        for(int i = 0; i < finalLength; i++) {
            result.add(new Tuple4<A, B, C, D> (
                    (A) items[i * 4], (B) items[i * 4 + 1], (C) items[i * 4 + 2], (D) items[i * 4 + 3]
            ));
        }
        return result;
    }

    public static <A, B, C, D> List<Tuple4<A, B, C, D>> zip(Iterable<A> firsts, Iterable<B> seconds, Iterable<C> thirds, Iterable<D> fourths) {
        Iterator<A> firstLoop = firsts.iterator();
        Iterator<B> secondLoop = seconds.iterator();
        Iterator<C> thirdLoop = thirds.iterator();
        Iterator<D> fourthLoop = fourths.iterator();
        List<Tuple4<A, B, C, D>> result = new ArrayList<>();
        while(firstLoop.hasNext() && secondLoop.hasNext() && thirdLoop.hasNext() && fourthLoop.hasNext()) {
            result.add(new Tuple4<>(firstLoop.next(), secondLoop.next(), thirdLoop.next(), fourthLoop.next()));
        }
        return result;
    }
}
