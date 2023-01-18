package common;


// todo - implementation. would help with parallelization
// binary tree as a deque.
// add or remove items from the front of the list, and we maintain an O(log n) depth for parallel traversals
// this also allows us to merge two trees in constant time given they are both balanced
public class BinaryTreeDeque<X> {
    private Node head;
    private Node first;
    private Node last;
    private class Node {
        private int size;
        private X data;
        private Node left;
        private Node right;
        private Node parent;
    }
    public void addFirst(X item) {

    }
    public void addLast(X item) {

    }
    public X getFirst() {
        return null;
    }
    public X getLast() {
        return null;
    }
    public void removeFirst() {

    }
    public void removeLast() {

    }
    public int getLength() {
        return head == null ? 0 : head.size;
    }
}


/***
 *  a
 *
 *    b
 *  a
 *     b
 *  a     c
 *          d
 *     b
 *  a     c
 *           c
 *      a        d
 *  a0     b
 */