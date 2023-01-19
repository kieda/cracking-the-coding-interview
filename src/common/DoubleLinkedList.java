package common;


import java.util.ArrayList;
import java.util.List;

public class DoubleLinkedList<E> implements SimpleDeque<E> {

    public static <Z> DoubleLinkedList<Z> make(Z... items) {
        DoubleLinkedList<Z> result = new DoubleLinkedList<>();
        for(int i = 0; i < items.length; i++) {
            result.addLast(items[i]);
        }
        return result;
    }

    public void delete(Node<E> node) {
        // handles cases if the node is at the beginning, middle, end, or the only item in the list
        Node<E> next = node.getNext();
        Node<E> previous = node.getPrevious();

        if(previous == null) {
            setHead(next);
        } else {
            previous.setNext(next);
        }

        if(next == null) {
            setTail(previous);
        } else {
            next.setPrevious(previous);
        }
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
    private Node<E> head;
    private Node<E> tail;
    public DoubleLinkedList() {
        this.head = null;
        this.tail = null;
        length = 0;
    }

    public Node<E> getHead() {
        return head;
    }

    public Node<E> getTail() {
        return tail;
    }

    public void setHead(Node<E> head) {
        this.head = head;
    }

    public void setTail(Node<E> tail) {
        this.tail = tail;
    }

    public void addFirst(E elem) {
        length++;
        Node<E> newNode = new Node<>(elem);
        if(isEmpty()) {
            head = tail = newNode;
        } else if(head == tail) {
            head = newNode;
            head.next = tail;
            tail.previous = head;
        } else {
            head.previous = newNode;
            newNode.next = head;
            head = newNode;
        }
    }

    public void addLast(E elem) {
        length++;
        Node<E> newNode = new Node<>(elem);
        if(isEmpty()) {
            head = tail = newNode;
        } else if(head == tail) {
            tail = newNode;
            tail.previous = head;
            head.next = tail;
        } else {
            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;
        }
    }
    public void merge(DoubleLinkedList<E> other) {
        length += other.length;
        if(other.isEmpty())
            return;
        if(isEmpty()) {
            head = other.head;
            tail = other.tail;
        } else {
            tail.next = other.head;
            tail = other.tail;
        }
    }
    public void removeFirst() {
        head = head.next;
        head.previous = null;
        length--;
    }
    public void removeLast() {
        tail = tail.previous;
        tail.next = null;
        length--;
    }

    public boolean isEmpty() {
        return head == null && tail == null;
    }

    // note: will throw nullpointerexception if no item
    public E getFirst() {
        return head.item;
    }
    public E getLast() {
        return tail.item;
    }
    public int getLength() {
        return length;
    }
    public List<E> toList() {
        List<E> list = new ArrayList<>(getLength());
        Node<E> node = head;
        while(node != null) {
            list.add(node.item);
            node = node.next;
        }
        return list;
    }
}
