package common.tree;

import common.Sort;

public class RedBlackTree<X extends Comparable<X>> extends BinarySearchTree<X>{
    public enum Color{
        RED,
        BLACK;
        public static Color getColor(RedBlackTree<? extends Comparable<?>>.Node node) {
            if(node == null)
                return BLACK;
            return node.getColor();
        }
        public boolean isRed() {
            return this == RED;
        }
        public boolean isBlack() {
            return this == BLACK;
        }
    }


    public class Node extends BinarySearchTree<X>.Node{
        private Color color;
        public Node(X elem) {
            super(elem);
            color = Color.RED;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public Node getLeft() {
            return (Node)super.getLeft();
        }

        @Override
        public Node getRight() {
            return (Node) super.getRight();
        }

        @Override
        public Node getParent() {
            return (Node) super.getParent();
        }

        public Node getGrandparent() {
            return getParent().getParent();
        }

        public Node getUncle() {
            Node parent = getParent();
            return (Node) ParentRelation.getRelation(parent).getOppositeSibling(parent);
        }

        public boolean voilatesRedParentProperty() {
            return getColor().isRed() && Color.getColor(getParent()).isRed();
        }
        public boolean violatesRedChildProperty() {
            return getColor().isRed() && (Color.getColor(getLeft()).isRed() || Color.getColor(getRight()).isRed());
        }
    }

    public Node insert(X elem) {
        Node newNode = new Node(elem);
        if(isEmpty()) {
            setHead(newNode);
            return newNode;
        }
        Node insertionPoint = lookup(elem, SearchFlags.INSERTION_POINT);
        if(Sort.compare(elem, insertionPoint.getElem()) > 0) {
            insertionPoint.setRight(newNode);
        } else {
            insertionPoint.setLeft(newNode);
        }

        Node node = newNode;
        redBlackLoop:
        while(true) {
            Node parent = node.getParent();
            if(Color.getColor(parent).isBlack()) // also catches when parent == null
                break redBlackLoop;

            // parent is red otherwise
            Node grandParent = node.getGrandparent();
            if(grandParent == null) {
                // parent is red and is the head. We just set it to black
                parent.setColor(Color.BLACK);
                break redBlackLoop;
            }

            Node uncle = node.getUncle();
            switch(Color.getColor(uncle)) {
                case RED:
                    parent.setColor(Color.BLACK);
                    grandParent.setColor(Color.RED);
                    uncle.setColor(Color.BLACK);

                    node = grandParent;
                    break;
                case BLACK:
                    GrandparentRelation relation = GrandparentRelation.getRelation(node);
                    switch(relation) {
                        case ZIGZAG:
                            // LEFT RIGHT
                            parent.leftRotate();
                            node = parent;
                            break;
                        case ZAGZIG:
                            parent.rightRotate();
                            node = parent;
                            break;
                        case ZIGZIG:
                            grandParent.rightRotate();
                            parent.setColor(Color.BLACK);
                            grandParent.setColor(Color.RED);
                            // node and uncle remain red

                            // we may exit the main loop at this point. If we don't however, it would be caught
                            // at the beginning of the next iteration as the parent is BLACK
                            break redBlackLoop;
                        case ZAGZAG:
                            grandParent.leftRotate();
                            parent.setColor(Color.BLACK);
                            grandParent.setColor(Color.RED);
                            break redBlackLoop;
                        default:
                            // should never occur. From the first two statements node should always have
                            // a parent and grandparent.
                            throw new IllegalStateException("node " + node + " has unexpected grandparent relation " + relation);
                    }
            }
        }

        return newNode;
    }

    @Override
    public Node lookup(X data) {
        return (Node)super.lookup(data);
    }

    @Override
    public Node lookup(X data, SearchFlags flags) {
        return (Node)super.lookup(data, flags);
    }

    public void remove(Node node) {
        
    }
}
