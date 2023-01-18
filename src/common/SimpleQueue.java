package common;

public interface SimpleQueue<X> {
    public void addFirst(X elem);
    public X getLast();
    public void removeLast();
    public boolean isEmpty();
}
