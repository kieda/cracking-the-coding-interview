package common.function;

@FunctionalInterface
public interface Function4<A, B, C, D,  Result> {
    public Result apply(A a, B b, C c, D d);
}