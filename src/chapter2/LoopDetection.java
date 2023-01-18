package chapter2;

import common.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static common.SingleLinkedList.Node;

/**
 * Given a linked list which might contain a loop, implement an algorithm that returns the node at the beginning of the loop
 * (if one exists)
 */
public class LoopDetection<X> implements Testable {

    public boolean hasLoopHashSet(SingleLinkedList<X> list) {
        Set<Node<X>> visited = new HashSet<>();
        Node<X> node = list.getHead();

        // reed
        // dr serchel goldwin
        while(node != null) {
            if(visited.contains(node))
                return true;
            visited.add(node);
            node = node.getNext();
        }
        return false;
    }

    // A -> B -> C -> D -> E -> F -> C
    // ^    |
    // ^         |
    //      ^         |
    //      ^              |
    //           ^              |
    //           ^
    //           |

    // A -> B -> A
    // ^    |
    // ^
    // X


    // A -> B -> C -> A
    // ^    |
    // ^         |
    //      ^
    // |    ^
    //      X


    public boolean hasLoopRunners(SingleLinkedList<X> list) {
        Node<X> slower = list.getHead();
        if(slower == null)
            return false;
        Node<X> faster = slower.getNext();
        boolean parity = false;
        while(faster != null && faster != slower) {
            if(parity)
                slower = slower.getNext();
            faster = faster.getNext();
            parity ^= true;
        }
        return faster != null && faster == slower;
    }

    private final static List<Tuple2<SingleLinkedList<String>, Boolean>> INSTANCE_TESTCASES;
    public static final LoopDetection<String> INSTANCE;

    static {
        SingleLinkedList<String> zeroElemList = new SingleLinkedList<>();

        SingleLinkedList<String> oneElemList = new SingleLinkedList<>();
        Node<String> A1 = new Node<>("A1");
        oneElemList.setHead(A1);

        SingleLinkedList<String> fourElemList = new SingleLinkedList<>();
        Node<String> A4 = new Node<>("A4");
        Node<String> B4 = new Node<>("B4");
        Node<String> C4 = new Node<>("C4");
        Node<String> D4 = new Node<>("D4");

        A4.setNext(B4);
        B4.setNext(C4);
        C4.setNext(D4);
        fourElemList.setHead(A4);

        SingleLinkedList<String> fiveElemList = new SingleLinkedList<>();
        Node<String> A5 = new Node<>("A5");
        Node<String> B5 = new Node<>("B5");
        Node<String> C5 = new Node<>("C5");
        Node<String> D5 = new Node<>("D5");
        Node<String> E5 = new Node<>("E5");

        A5.setNext(B5);
        B5.setNext(C5);
        C5.setNext(D5);
        D5.setNext(E5);
        fiveElemList.setHead(A5);

        INSTANCE_TESTCASES = null;/*Tuple3.<SingleLinkedList<String>, Integer, Node<String>>of3(
                zeroElemList, 0, null,
                zeroElemList, 1, null,
                oneElemList, 0, A1,
                oneElemList, 1, null,
                fourElemList, 0, D4,
                fourElemList, 1, C4,
                fourElemList, 2, B4,
                fourElemList, 3, A4,
                fourElemList, 4, null,
                fiveElemList, 0, E5,
                fiveElemList, 1, D5,
                fiveElemList, 2, C5,
                fiveElemList, 3, B5,
                fiveElemList, 4, A5,
                fiveElemList, 5, null
        ).stream().map(test -> Tuple2.make(Tuple2.make(test.getFirst(), test.getSecond()), test.getThird())).collect(Collectors.toUnmodifiableList());*/
        INSTANCE = null;//new KthToLast<>(INSTANCE_TESTCASES);
    }

    public LoopDetection(List<Tuple2<SingleLinkedList<X>, Boolean>> testCases) {
        this.testCases = testCases;
    }

    private final List<Tuple2<SingleLinkedList<X>, Boolean>> testCases;
    private List<Tuple2<String, Predicate<SingleLinkedList<X>>>> testFunctions = List.of(
        Tuple2.make("hasLoopHashSet", this::hasLoopHashSet),
        Tuple2.make("hasLoopRunners", this::hasLoopRunners)
    );

    @Override
    public void test() throws TestException {

    }
}
