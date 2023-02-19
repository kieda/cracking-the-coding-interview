package common.tree;

public enum ParentRelation {
    HEAD,
    LEFT,
    RIGHT,
    NONE;

    public <E> void replaceNode(BinaryTree<E>.Node node, BinaryTree<E>.Node replacement) {
        BinaryTree<E>.Node parent = node.getParent();
        switch (this) {
            case HEAD:
                break;
            case LEFT:
                parent.setLeft(replacement);
                break;
            case RIGHT:
                parent.setRight(replacement);
                break;
            case NONE:
                BinaryTree.throwMissingChild(parent, node);
        }
    }

    public ParentRelation getOpposite() {
        switch (this) {
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                return this;
        }
    }

    public <E> BinaryTree<E>.Node getChild(BinaryTree<E>.Node node) {
        switch (this) {
            case LEFT:
                return node.getLeft();
            case RIGHT:
                return node.getRight();
            default:
                throw new IllegalArgumentException("Cannot get child from node with relation " + this);
        }
    }

    public <E> void setChild(BinaryTree<E>.Node node, BinaryTree<E>.Node replacement) {
        switch (this) {
            case LEFT:
                node.setLeft(replacement);
                break;
            case RIGHT:
                node.setRight(replacement);
                break;
            default:
                throw new IllegalArgumentException("Cannot set child with invalid relation " + this);
        }
    }

    public static <E> ParentRelation getRelation(BinaryTree<E>.Node node) {
        BinaryTree<E>.Node parent = node.getParent();
        if (parent == null) {
            return HEAD;
        } else if (parent.getLeft() == node) {
            return LEFT;
        } else if (parent.getRight() == node) {
            return RIGHT;
        } else {
            return NONE;
        }
    }

    public <E> BinaryTree<E>.Node getOppositeSibling(BinaryTree<E>.Node node) {
        BinaryTree<E>.Node parent = node.getParent();
        switch (this) {
            case LEFT:
                return parent.getRight();
            case RIGHT:
                return parent.getLeft();
            default:
                BinaryTree.throwMissingChild(parent, node);
        }
        // will never occur from throw statement above, but java compiler will complain otherwise.
        return null;
    }
}
