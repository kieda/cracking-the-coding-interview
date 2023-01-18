package common;

public interface SimpleStack<X> {
    public void addFirst(X elem);
    public X getFirst();
    public void removeFirst();
    public boolean isEmpty();
}
