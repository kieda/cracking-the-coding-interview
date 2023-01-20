package common.lists;


/**
 * basically a doubly-linked list that goes around in a ring
 * since this goes in a circle, there is no "head" to the list.
 * Rather, we pass the node itself back to the user, which is then used to add or remove elements
 * The nodes themselves act as their own dequeues, where addfirst adds an item in front of this node, addLast adds an item
 * behind this node.
 */
public class LinkedRingBuffer<E> implements SimpleDeque<E>{
    private E item;
    private LinkedRingBuffer<E> next;
    private LinkedRingBuffer<E> previous;

    public LinkedRingBuffer() {
        next = null;
        previous = null;
        item = null;
    }
    public LinkedRingBuffer<E> getNext() {
        return next;
    }

    public LinkedRingBuffer<E> getPrevious() {
        return previous;
    }

    /**
     * removes this node.
     * Fun thing: the node removed is its own, valid single-element LinkedRingBuffer that you may now use
     */
    @Override
    public void removeFirst() {
        if(isEmpty())
            throw new EmptyCollectionException();
        if(previous == next) {
            previous = next = null;
            item = null;
        } else {
            LinkedRingBuffer<E> next = getNext();
            LinkedRingBuffer<E> previous = getPrevious();

            previous.next = next;
            next.previous = previous;
            this.next = this;
            this.previous = this;
        }
    }

    /**
     * removes the previous node
     */
    @Override
    public void removeLast() {
        if(isEmpty())
            throw new EmptyCollectionException();
        getPrevious().removeFirst();
    }

    /**
     * adds a node in front of this one
     */
    @Override
    public void addFirst(E elem) {
        if(isEmpty()) {
            this.item = elem;
            // one-element cycle
            next = previous = this;
        } else {
            // new node to be inserted into the ring
            LinkedRingBuffer<E> newNode = new LinkedRingBuffer<>();
            newNode.item = elem;

            LinkedRingBuffer<E> nextNode = getNext();
            this.next = newNode;
            newNode.next = nextNode;
            newNode.previous = this;
            nextNode.previous = newNode;
        }
    }

    @Override
    public E getFirst() {
        if(isEmpty())
            throw new EmptyCollectionException();
        return item;
    }
    @Override
    public E getLast() {
        if(isEmpty())
            throw new EmptyCollectionException();
        return getPrevious().getFirst();
    }

    @Override
    public String toString() {
        if(isEmpty())
            return "[]";

        StringBuilder loopString = new StringBuilder();
        loopString.append("[");
        boolean firstAppended = false;
        LinkedRingBuffer<E> node = this;
        // cancel on node == this && firstAppended
        while(node != this || !firstAppended) {
            if(node == this)
                firstAppended = true;
            loopString.append(node.getFirst())
                .append(" -> ");
            node = getNext();
        }
        loopString.append("(LOOP)]");
        return loopString.toString();
    }

    @Override
    public boolean isEmpty() {
        // since there really isn't a head, we just have a special case where next and previous being null
        // represents an empty ring (even though we still have a pointer to the item)
        return next == null && previous == null;
    }
}
