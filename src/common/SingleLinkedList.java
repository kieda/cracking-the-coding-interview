package common;

import java.util.HashSet;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Basic singly linked list that acts as a stack.
 * @param <X>
 */
public class SingleLinkedList<X> implements SimpleStack<X>{
    public String toString() {
        StringBuilder sb = new StringBuilder();
        HashSet<Node<X>> loopDetection = new HashSet<>();
        sb.append('[');
        Node<X> node = getHead();
        while(node != null) {
            if(loopDetection.contains(node)) {
                sb.append("LOOP<").append(node.getItem()).append(">");
                break;
            } else {
                loopDetection.add(node);
            }
            sb.append(node.getItem());
            node = node.getNext();
            if(node != null)
                sb.append(" -> ");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof SingleLinkedList) {
            SingleLinkedList<X> otherList = (SingleLinkedList<X>) other;
            Node<X> thisNode = getHead();
            Node<X> otherNode = otherList.getHead();
            while(thisNode != null && otherNode != null) {
                if(!Objects.equals(thisNode.getItem(), otherNode.getItem()))
                    return false;
                thisNode = thisNode.getNext();
                otherNode = otherNode.getNext();
            }
            return thisNode == null && otherNode == null;
        }
        return false;
    }
    public SingleLinkedList<X> copy() {
        return copy(x -> x);
    }
    public SingleLinkedList<X> copy(UnaryOperator<X> copyValue) {
        SingleLinkedList<X> result = new SingleLinkedList<>();

        Node<X> node = getHead();
        Node<X> previous = null;
        while(node != null) {
            Node<X> newItem = new Node<>(copyValue.apply(node.getItem()));
            if(previous == null) {
                result.setHead(newItem);
            } else {
                previous.setNext(newItem);
            }
            previous = newItem;
            node = node.getNext();
        }
        return result;
    }
    public static <Z> SingleLinkedList<Z> make(Z... items) {
        SingleLinkedList<Z> result = new SingleLinkedList<>();
        for(int i = items.length - 1; i >= 0; i--) {
            result.addFirst(items[i]);
        }
        return result;
    }
    // expose for the purposes of chapter 2
    public static class Node<Y> {
        public Node(Y item) {
            this.item = item;
        }
        private Y item;
        private Node<Y> next;

        public Node<Y> getNext() {
            return next;
        }

        public Y getItem() {
            return item;
        }

        public void setNext(Node<Y> next) {
            this.next = next;
        }

        public void setItem(Y item) {
            this.item = item;
        }

        public String toString() {
            return "Node<" + item + ">";
        }
    }
    private Node<X> head;
    public void addFirst(X item) {
        Node<X> addition = new Node<>(item);
        addition.setNext(getHead());
        setHead(addition);
    }
    public void removeFirst() {
        if(getHead() == null)
            throw new EmptyStackException();
        setHead(getHead().getNext());
    }
    public X getFirst() {
        return getHead().getItem();
    }

    public Node<X> getHead() {
        return head;
    }

    public void setHead(Node<X> head) {
        this.head = head;
    }
    public void reverse(Node<X> stoppingPoint) {
        SingleLinkedList.Node<X> node = getHead();
        SingleLinkedList.Node<X> previous = null;
        SingleLinkedList.Node<X> next = null;
        while(node != stoppingPoint && node != null) {
            next = node.getNext();
            node.setNext(previous);
            previous = node;
            node = next;
        }
        if(node == null) {
            // will occur if stoppingPoint is null or item isn't found in the list
            // new head is at the end of the list
            setHead(previous);
        } else {
            // join the (now end) of the list with the rest
            getHead().setNext(next);
            // set the head to the part where we stopped
            setHead(stoppingPoint);
        }
    }
    public int count() {
        int count = 0;
        Node<X> node = getHead();
        while(node != null) {
            count++;
            node = node.getNext();
        }
        return count;
    }

    public void reverse() {
        reverse(null);
    }

    public boolean isEmpty() {
        return getHead() == null;
    }
}
