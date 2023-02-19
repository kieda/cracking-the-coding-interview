package chapter4;

import common.tree.BinaryTree;
import common.tree.ParentRelation;

import java.util.ArrayList;
import java.util.Random;

/**
 * You are implementing a binary tree class from scratch which, in addition to
 * insert, find, and delete, has a method getRandomNode() which returns a random node
 * from the tree. All nodes should be equally likely to be chosen. Design and implement an algorithm
 * for getRandomNode, and explain how you would implement the rest of the methods.
 */
public class RandomNode {
    // simple idea: just hold an arraylist of all the nodes. Whenever a new element is added, add it to the end of the arraylist
    // no auxilary space allowed: use probabilities and go from the ground up

    public static abstract class RandomBinaryTree<E> extends BinaryTree<E> {
        public abstract void onNodeAdded(Node newNode);
        public abstract void onLeafRemoved(Node removed, Node parent);
        public abstract Node getRandomNode();
        public Node setElem(Node parent, E elem, ParentRelation direction) {
            switch(direction) {
                case HEAD:
                    Node head = getHead();
                    if(head == null) {
                        Node newNode = makeNode(elem);
                        onNodeAdded(newNode);
                        setHead(newNode);
                        return newNode;
                    } else {
                        head.setElem(elem);
                        return head;
                    }
                default:
                    Node existing = direction.getChild(parent);
                    if (existing == null) {
                        Node newNode = makeNode(elem);
                        onNodeAdded(newNode);
                        direction.setChild(parent, newNode);
                        return newNode;
                    } else {
                        existing.setElem(elem);
                        return existing;
                    }
            }
        }
        public void removeLeaf(Node leaf) {
            if(!leaf.isLeaf())
                throw new IllegalArgumentException("Node " + leaf + " is not a leaf!");
            Node parent = leaf.getParent();
            ParentRelation relation = ParentRelation.getRelation(leaf);
            if(relation == ParentRelation.HEAD) {
                setHead(null);
            } else {
                relation.replaceNode(leaf, null);
                leaf.setParent(null);
            }
            onLeafRemoved(leaf, parent);
        }
    }

    public static class ArrayRandomBinaryTree<E> extends RandomBinaryTree<E> {
        private Random random = new Random();
        private ArrayList<Node> nodes = new ArrayList<>();

        @Override
        public void onNodeAdded(Node newNode) {
            nodes.add(newNode);
        }

        @Override
        public void onLeafRemoved(Node removed, Node parent) {
            nodes.remove(removed);
        }

        public Node getRandomNode() {
            if(nodes.isEmpty())
                return null;
            return nodes.get(random.nextInt(nodes.size()));
        }
    }

    public static class TraverseRandomBinaryTree<E> extends RandomBinaryTree<E> {
        private Random random = new Random();
        @Override
        public void onNodeAdded(Node newNode) {
            SizeNode node = (SizeNode) newNode;
            while(node != null) {
                node.incrementSize();
                node = node.getParent();
            }
        }

        @Override
        public void onLeafRemoved(Node removed, Node parent) {
            SizeNode node = (SizeNode) parent;
            while(node != null) {
                node.decrementSize();
                node = node.getParent();
            }
        }

        @Override
        public Node getRandomNode() {
            SizeNode node = (SizeNode) getHead();
            if(node == null)
                return null;
            // get a random index, then traverse the tree to find it
            int index = random.nextInt(node.getTreeSize());
            while(true) {
                int leftSize = node.getLeft().getTreeSize();
                if (index < leftSize) {
                    node = node.getLeft();
                } else if (leftSize == 0) {
                    return node;
                } else {
                    index = index - leftSize - 1;
                    node = node.getRight();
                }
            }
        }

        @Override
        public Node makeNode(E elem) {
            return new SizeNode(elem);
        }

        public class SizeNode extends Node {
            private int treeSize = 0;
            public SizeNode(E elem) {
                super(elem);
            }
            public void incrementSize() {
                treeSize++;
            }
            public void decrementSize() {
                treeSize--;
            }
            public int getTreeSize() {
                return treeSize;
            }

            @Override
            public SizeNode getLeft() {
                return (SizeNode) super.getLeft();
            }

            @Override
            public SizeNode getRight() {
                return (SizeNode) super.getRight();
            }

            @Override
            public SizeNode getParent() {
                return (SizeNode) super.getParent();
            }
        }
    }

}
