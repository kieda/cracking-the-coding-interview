package common.tree;

public class CannotRotateException extends RuntimeException{
    private final BinaryTree.Node node;
    public CannotRotateException(String errorMessage, BinaryTree.Node node) {
        super(errorMessage);
        this.node = node;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " on node " + node;
    }
}
