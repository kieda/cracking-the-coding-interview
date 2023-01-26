package common.tree;

import common.HasMin;
import common.Sort;
import common.lists.EmptyCollectionException;
import common.tuple.Tuple2;

import java.util.Collection;

public class MinHeap<X extends Comparable<X>> extends BinaryTree<X> implements HasMin<X> {
    private Node nextInsertionPoint;

    /**
     * returns the parent of the last node in the tree
     * This is the root of the next item to insert
     */
    private Node getLastParent() {
        return nextInsertionPoint;
    }
    private void setLastParent(Node nextInsertionPoint) {
        this.nextInsertionPoint = nextInsertionPoint;
    }

    /**
     * We move the nextInsertionPoint on insertions and deletions.
     * Note that the worst case for this function is O(log n) to traverse up and down the tree
     * However amortized it is O(1). A tree with depth K will have 2^(K+1) insertions before it is a complete tree again
     * So we have 2^K insertions traversing 1 node
     * 2^(K-1) insertions traversing 2 nodes
     * 2^(K-2) insertions traversing 4 nodes
     * ...
     * 1 insertion traversing 2^K nodes
     *
     * In total, after 2^(K + 1) insertions, the summation is O(2^K) = O(N)
     * Thus amortized this is O(1)
     */
    private Node findNextInsertionPoint(Node current) {
        if(current == null)
            return null;

        boolean switchedSides = false;
        while(current.getLeft() != null && current.getRight() != null) {
            if(switchedSides) {
                // traverse down to get the next available node
                current = current.getLeft();
            } else {
                switch (ParentalRelation.getRelation(current)) {
                    case LEFT:
                        // we are switching to a right branch which has open positions
                        current = current.getParent().getRight();
                    case HEAD:
                        // traverse to the leftmost available node
                        switchedSides = true;
                        break;
                    case RIGHT:
                        current = current.getParent();
                        break;
                    case NONE:
                        throwMissingChild(current.getParent(), current);
                }
            }
        }

        return current;
    }

    /**
     * Returns the last inserted node according to ordering, given that lastParent is the parent of the next node we'll
     * insert into
     */
    private Node findPreviousInsertionPoint(Node lastParent) {
        if(lastParent == null)
            return null;
        if(lastParent.getRight() != null)
            return lastParent.getRight();
        if(lastParent.getLeft() != null)
            return lastParent.getLeft();

        // otherwise, both our left and right is null
        // traverse up the tree to a point where we can traverse down a left node without going down the same path again
        loop : while(lastParent.getParent() != null) {
            switch(ParentalRelation.getRelation(lastParent)) {
                case RIGHT:
                    lastParent = lastParent.getParent();
                case HEAD:
                    break loop;
                case LEFT:
                    lastParent = lastParent.getParent();
                    break;
            }
        }

        // go to the previous subtree
        lastParent = lastParent.getLeft();

        // this may happen if lastParent is the only node in the tree. Then we return null, signifying that there was no
        // parent before this one.
        if(lastParent == null)
            return null;

        // go to the last element in the subtree
        while(lastParent.getRight() != null) {
            lastParent = lastParent.getRight();
        }

        // since the lastParent's left and right child are null, it implies that the previous subtree to the left of us
        // will have both the left and right nodes filled
        return lastParent;
    }

    /**
     * O(n) search through the binary tree. We visit each node exactly twice, one entering the subtree and another leaving it
     *
     * We use the balanced property of the tree to allow us to traverse the tree without a stack or recursion
     * This is done by traversing via a node order by incrementing an integer representing our index
     *
     * With a little XOR magic and since we know this is a complete binary tree we can find out a traversal pattern
     * without using space.
     */
    public Node findFirst(X elem) {
        if(isEmpty())
            return null;

        // perform an in-order traversal. Start from leftmost node
        Node startNode = getHead();
        while(startNode.getLeft() != null) {
            startNode = startNode.getLeft();
        }
        Node node = startNode;
        int modifier = 1;
        int shift = 0;
        int index = 1;

        int depth = 0; // we set depth at the bottom to 0, at the head is maxDepth
        boolean skipBottom = false; // set this to true if we want to start skipping the bottom entries for a non-full tree
        while(node != null) {

            // we found the node! return it.
            if(Sort.compare(node.getElem(), elem) == 0)
                return node;

            if(!skipBottom && depth > 0 && node.getLeft() == null || node.getRight() == null) {
                // every other node is now gone because the tree isn't completely full.
                shift = 1; // shift essentially cuts off the bottom nodes of the tree we're traversing through - useful when we have an incomplete trees
                modifier = 2; // modifier as 2^k will skip k depth of nodes on the bottom of the tree
                skipBottom = true; // start skipping the bottom
            }

            // actual dark magic fuckery
            int xor = ((index + modifier) ^ index) >>> shift;
            int movement = Integer.numberOfTrailingZeros(~xor) - 1;

            // cool note: if we're at depth 1 we have the bit representation  XXX10
            // if the right child is null, then we will add two, it will also end in 0 so we will avoid future leaf nodes,
            // as they will no longer exist in the tree
            // adding two to a depth 1 tree is guaranteed to not be a leaf on the bottom row, moreover it contains all
            // elements that are above depth 0

            if(movement == 0) {
                node = node.getRight();
                depth -= 1;
                while(node.getLeft() != null) {
                    node = node.getLeft();
                    depth -= 1;
                }

                if(node.getRight() != null) {
                    // error, we are supposed to fill in from left to right. This would mean that our left node is null but our right node is null
                    throw new HeapInvariantException("Node " + node + " at depth " + depth + " has a right child but no left child!");
                } else if(depth > 1 || depth < 0) {
                    // error, we should only stop at depth 1 or 0 due to balanced property
                    throw new HeapInvariantException("Node " + node + " at incorrect depth " + depth + " has null children!");
                }
                if(!skipBottom && depth == 1) {
                    // this implies that the left node that we were supposed to arrive on is not present.
                    // we increment the index by one, which is the parent or the current node.
                    // We do this one-time only, since after this point we will only visit even nodes
                    index++;
                }
            } else {
                // move up the tree by our movement
                for(int i = 0; i < movement; i++) {
                    node = node.getParent();
                    // we have traversed all of our nodes and have not found the item. Return null
                    if(node == null)
                        break;
                }
                depth += movement;
            }
            index += modifier;
        }

        return null;
    }

    public Node insert(X elem) {
        Node insertionNode = new Node(elem);
        Node insertionPoint = getLastParent();
        if(insertionPoint == null) {
            setHead(insertionNode);
            setLastParent(insertionNode);
        } else {
            if(insertionPoint.getLeft() == null) {
                // insert it to the left
                insertionPoint.setLeft(insertionNode);
            } else if(insertionPoint.getRight() == null) {
                insertionPoint.setRight(insertionNode);
            } else {
                // exception - insertionPoint should always have an available node
                throw new HeapInvariantException("Insertion point " + insertionPoint + " does not have an available child");
            }
            boolean swapNodes = false;
            Node parent = insertionPoint;
            // if our child is greater than our parent, we swap it up
            while(parent != null && Sort.compare(parent.getElem(), insertionNode.getElem()) < 0) {
                swapNodes = true; // signifies that the next insertion point is not our current one, rather the parent that was swapped down
                parent = parent.getParent();
                insertionNode.swapUp(); // node is moved upwards by this method
            }
            if(parent == null) {
                // we swapped the node all the way up to the top
                setHead(insertionNode);
            }
            // get the next insertion point
            setLastParent(findNextInsertionPoint(swapNodes ? insertionPoint : insertionNode));
        }
        return insertionNode;
    }

    /**
     * removes the minimum element
     */
    public X extractMin() {
        if(isEmpty())
            throw new EmptyCollectionException();
        X result = getMin();
        remove(getHead());
        return result;
    }

    @Override
    public X getMin() {
        if(isEmpty())
            throw new EmptyCollectionException();
        return getHead().getElem();
    }

    /**
     * Removes a node from the Min Heap.
     * @param removeNode
     */
    public void remove(Node removeNode) {
        if(isEmpty())
            throw new EmptyCollectionException();
        Node lastParent = getLastParent();
        Node nodeToSwap = findPreviousInsertionPoint(lastParent);

        if(lastParent == removeNode) {
            // we're removing the last parent node.
            // however, the node we're swapping in will now be our last parent
            setLastParent(nodeToSwap);
        }

        if(nodeToSwap == null) {
            // null if node is the only element in the tree
            removeNode.setParent(null);
            setHead(null);
            return;
        }

        // outgoing pointers on the node we're removing
        Node removeParent = removeNode.getParent();

        // sets the parent's left or right children to a new value. NodeToSwap's parent releases its pointer.
        // RemoveNode's parent updates its pointer to the node we want to swap in.
        ParentalRelation.getRelation(removeNode)
                .replaceNode(removeNode, nodeToSwap);
        ParentalRelation.getRelation(nodeToSwap)
                .replaceNode(nodeToSwap, null);

        // nodeToSwap is now positioned where removeNode was
        nodeToSwap.setParent(removeParent);
        nodeToSwap.setLeft(removeNode.getLeft());
        nodeToSwap.setRight(removeNode.getRight());

        // removeNode outgoing pointers are nullified
        removeNode.setParent(null);
        removeNode.setLeft(null);
        removeNode.setRight(null);

        // now, we heapify to put nodeToSwap in the right position. Note that we may need to either heapify up or heapify down
        // todo: we want to ensure that the next insertion point is set to the correct value if it's swapped
        //       we can refactor out into a single method - heapify(node)
        //       which will first try and place the item with the parent
        //       then will check the children and swap down
    }

    public Collection<Node> addAll(Collection<X> items) {
        // we can add many items in O(|items|) by adding them to the array, then heapifying up from the bottom nodes
        return null;
    }

    /**
     * more efficient than inserting then extracting the min, as we only need to do a downheap traversal
     *
     * @param elem
     * @return (X itemExtracted, Node newNodeInserted)
     */
    public Tuple2<X, Node> insertThenExtractMin(X elem) {
        return null;
    }
}
