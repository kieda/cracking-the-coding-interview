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
        if(node.isHead() && node.isLeaf()) {
            // head and single node tree
            setHead(null);
            return;
        } else if(node.getColor().isRed() && node.isLeaf()) {
            // red leafs can simply be replaced
            ParentRelation.getRelation(node).replaceNode(node, null);
            node.setParent(null);
            return;
        } else if(!node.isLeaf() && !node.isFull()) {
            // either left or right is not null, but not both
            boolean resetHead = node.isHead();

            Node replacement;
            if((replacement = node.getLeft()) != null) {
            } else {
                replacement = node.getRight();
            }

            replacement.setParent(node.getParent());
            ParentRelation.getRelation(node).replaceNode(node, replacement);

            if(resetHead)
                setHead(replacement);
            replacement.setColor(Color.BLACK);
            node.setLeft(null);
            node.setRight(null);
            node.setParent(null);
            return;
        } else if(node.isFull()){
            // find the replacementNode that will be put in the place of node. replacementNode will be a leaf
            Node replacementNode = (Node)node.getInorderSuccessorRandom();

            // parent, left, and right in the middle of the tree
            Node parent = node.getParent();
            Node left = node.getLeft();
            Node right = node.getRight();

            Node removalPoint = replacementNode.getParent();
            Color replacementColor = replacementNode.getColor();
            ParentRelation removalRelation = ParentRelation.getRelation(replacementNode);

            // this node is replaced with our replacement node
            ParentRelation.getRelation(node).replaceNode(node, replacementNode);
            replacementNode.setLeft(left);
            replacementNode.setRight(right);
            replacementNode.setParent(parent);
            left.setParent(replacementNode);
            right.setParent(replacementNode);

            // replacement node is swapped with this node
            node.setParent(removalPoint);
            removalRelation.setChild(removalPoint, node);

            // swap the colors
            replacementNode.setColor(node.getColor());
            node.setColor(replacementColor);

            // node is now a leaf. If it's red it is easily removed in the next following call
            // if it is black it falls through to the case below...
            remove(node);
            return;
        } else {
            // default case: node is a black leaf, node is not head
            Node parent = node.getParent();
            ParentRelation direction = ParentRelation.getRelation(node);
            Node sibling = (Node)direction.getOppositeSibling(node);
            Node close = (Node)direction.getChild(sibling);
            Node distant = (Node)direction.getOpposite().getChild(sibling);

            // remove node from this tree
            direction.replaceNode(node, null);
            node.setParent(null);

            do {
                Color parentColor = Color.getColor(parent);
                Color siblingColor = Color.getColor(sibling);
                Color closeColor = Color.getColor(close);
                Color distantColor = Color.getColor(distant);

                if(parentColor.isBlack() && siblingColor.isBlack()
                    && closeColor.isBlack() && distantColor.isBlack()) {
                    // ALL_BLACK
                    sibling.setColor(Color.RED);
                    direction = ParentRelation.getRelation(parent);
                    if((parent = parent.getParent()) == null)
                        return;
                } else if(siblingColor.isRed() && parentColor.isBlack()
                        && closeColor.isBlack() && distantColor.isBlack()) {
                    // SIBLING_RED
                    parent.rotate(direction);
                    parent.setColor(Color.RED);
                    sibling.setColor(Color.BLACK);
                } else if(parentColor.isRed() && siblingColor.isBlack()
                    && closeColor.isBlack() && distantColor.isBlack()) {
                    // PARENT_RED
                    sibling.setColor(Color.RED);
                    parent.setColor(Color.BLACK);
                    return;
                } else if(closeColor.isRed() && siblingColor.isBlack() && distantColor.isBlack()) {
                    // CLOSE_RED
                    sibling.rotate(direction.getOpposite());
                    sibling.setColor(Color.RED);
                    close.setColor(Color.BLACK);
                } else if(distantColor.isRed() && siblingColor.isBlack()) {
                    // DISTANT_RED
                    parent.rotate(direction);
                    sibling.setColor(parent.getColor());
                    parent.setColor(Color.BLACK);
                    sibling.setColor(Color.BLACK);
                    return;
                } else {
                    // should never occur
                    throw new IllegalStateException("Invalid red black tree");
                }

                sibling = (Node)direction.getOpposite().getChild(parent);
                close = (Node)direction.getChild(sibling);
                distant = (Node)direction.getOpposite().getChild(sibling);
            } while(true);
        }
    }
}
