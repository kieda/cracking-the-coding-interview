package common.tree;

public class MissingChildException extends IllegalStateException{
    private String childRelation;
    private BinaryTree.Node parent;
    private BinaryTree.Node child;
    public MissingChildException(String childRelation, BinaryTree.Node ancestor, BinaryTree.Node descendant) {
        this.childRelation = childRelation;
        this.parent = ancestor;
        this.child = descendant;
    }

    @Override
    public String getMessage() {
        return "Node " + parent + " does not have " + childRelation + " " + child;
    }
}
