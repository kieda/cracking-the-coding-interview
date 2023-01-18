package chapter2;

import common.*;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import static common.SingleLinkedList.Node;

/**
 * Implement an algorithm to delete a node in the middle (i.e. any note but the first and last node, not necessarily the exact middle)
 * Of a singly linked list, given only access to that node
 */
public class DeleteMiddleNode<X> implements Testable {
    // represents final element should be removed
    private final Node<X> DUMMY = new Node<>(null);

    // question implementation
    public Node<X> deleteMiddleNode(Node<X> node) {
        // A -> B -> C
        // B -> B -> C
        // B -> C

        // A -> (null)
        // DUMMY
        SingleLinkedList.Node<X> nextNode = node.getNext();
        if(nextNode == null)
            return DUMMY;

        X nextItem = nextNode.getItem();
        node.setItem(nextItem);
        node.setNext(nextNode.getNext());
        return node;
    }

    // use this so we can define strict inputs and outputs for testing purposes
    private SingleLinkedList<X> testWrapper(SingleLinkedList<X> list, UnaryOperator<X> copyElem, int index) {
        SingleLinkedList<X> copy = list.copy(copyElem);
        Node<X> elementToRemove = copy.getHead();
        Node<X> previous = null;
        for(int i = 0; i < index; i++) {
            previous = elementToRemove;
            elementToRemove = elementToRemove.getNext();
        }
        Node<X> result = deleteMiddleNode(elementToRemove);
        if(result == DUMMY) {
            if(previous == null) {
                copy.setHead(null);
            } else {
                previous.setNext(null);
            }
        }
        return copy;
    }

    private final List<Tuple2<Tuple2<SingleLinkedList<X>, Integer>, SingleLinkedList<X>>> testCases;
    private final UnaryOperator<X> copyElemFunction;
    private final List<Tuple2<String, BiFunction<SingleLinkedList<X>, Integer, SingleLinkedList<X>>>> testFunctions;
    public DeleteMiddleNode(List<Tuple2<Tuple2<SingleLinkedList<X>, Integer>, SingleLinkedList<X>>> testCases, UnaryOperator<X> copyElemFunction) {
        this.testCases = testCases;
        this.copyElemFunction = copyElemFunction;
        this.testFunctions = List.of(
            Tuple2.make("deleteMiddleNode", (list, index) -> testWrapper(list, copyElemFunction, index))
        );
    }
    private DeleteMiddleNode() {
        this(List.of(), x -> x);
    }

    private static final SingleLinkedList<String> FIVE_ELEM = SingleLinkedList.make("A", "B", "C", "D", "E");
    public static DeleteMiddleNode<String> INSTANCE = new DeleteMiddleNode<>(
            Tuple2.of2(
                Tuple2.make(FIVE_ELEM, 3), SingleLinkedList.make("A", "B", "C", "E"),
                    Tuple2.make(FIVE_ELEM, 4), SingleLinkedList.make("A", "B", "C", "D"),
                    Tuple2.make(FIVE_ELEM, 0), SingleLinkedList.make("B", "C", "D", "E"),
                    Tuple2.make(SingleLinkedList.make("A"), 0), SingleLinkedList.make()
            ),
            String::new
    );
    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases, DeleteMiddleNode.class, "");
    }
}
