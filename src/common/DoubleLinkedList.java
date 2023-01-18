package common;


import java.util.ArrayList;
import java.util.List;

public class DoubleLinkedList<E> implements SimpleQueue<E>, SimpleStack<E>{

    public static <Z> DoubleLinkedList<Z> make(Z... items) {
        DoubleLinkedList<Z> result = new DoubleLinkedList<>();
        for(int i = 0; i < items.length; i++) {
            result.addLast(items[i]);
        }
        return result;
    }

    // make this publicly accessible for chapter 2
    public static class Node<X> {
        public Node(X item) {
            this.item = item;
            next = null;
            previous = null;
        }
        private X item;
        private Node<X> next;
        private Node<X> previous;

        public Node<X> getNext() {
            return next;
        }

        public Node<X> getPrevious() {
            return previous;
        }

        public X getItem() {
            return item;
        }

        public void setItem(X item) {
            this.item = item;
        }

        public void setNext(Node<X> next) {
            this.next = next;
        }

        public void setPrevious(Node<X> previous) {
            this.previous = previous;
        }
    }
    private int length; // NOTE: length calculation may be off if we access internal nodes directly
    private Node<E> first;
    private Node<E> last;
    public DoubleLinkedList() {
        this.first = null;
        this.last = null;
        length = 0;
    }

    public void addFirst(E elem) {
        length++;
        Node<E> newNode = new Node<>(elem);
        if(isEmpty()) {
            first = last = newNode;
        } else if(first == last) {
            first = newNode;
            first.next = last;
            last.previous = first;
        } else {
            first.previous = newNode;
            newNode.next = first;
            first = newNode;
        }
    }

    public void addLast(E elem) {
        length++;
        Node<E> newNode = new Node<>(elem);
        if(isEmpty()) {
            first = last = newNode;
        } else if(first == last) {
            last = newNode;
            last.previous = first;
            first.next = last;
        } else {
            last.next = newNode;
            newNode.previous = last;
            last = newNode;
        }
    }
    public void merge(DoubleLinkedList<E> other) {
        length += other.length;
        if(other.isEmpty())
            return;
        if(isEmpty()) {
            first = other.first;
            last = other.last;
        } else {
            last.next = other.first;
            last = other.last;
        }
    }
    public void removeFirst() {
        first = first.next;
        first.previous = null;
        length--;
    }
    public void removeLast() {
        last = last.previous;
        last.next = null;
        length--;
    }

    public boolean isEmpty() {
        return first == null && last == null;
    }

    // note: will throw nullpointerexception if no item
    public E getFirst() {
        return first.item;
    }
    public E getLast() {
        return last.item;
    }
    public int getLength() {
        return length;
    }
    public List<E> toList() {
        List<E> list = new ArrayList<>(getLength());
        Node<E> node = first;
        while(node != null) {
            list.add(node.item);
            node = node.next;
        }
        return list;
    }
}
