package common.function;

@FunctionalInterface
public interface Function3<A, B, C, Result> {
    public Result apply(A a, B b, C c);
}
