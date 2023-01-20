package chapter2;

import common.lists.SingleLinkedList;
import common.tests.TestException;
import common.tests.Testable;
import common.tuple.Tuple2;
import common.tuple.Tuple3;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static common.lists.SingleLinkedList.Node;

/**
 * implement an algorithm to find the kth to last element of a singly linked list
 */
public class KthToLast<X> implements Testable {
    public Node<X> getKthToLast(SingleLinkedList<X> list, int position) {

        // 1 2 3 4 5 6 7
        //     ^   ^
        //

        Node<X> current = list.getHead();
        // move forward position steps
        for(int i = 0; i < position; i++) {
            if(current == null) {
                return null;
            }
            current = current.getNext();
        }


        Node<X> result = list.getHead();
        while(current != null && current.getNext() != null) {
            current = current.getNext();
            result = result.getNext();
        }
        return result;
    }

    public KthToLast() {
        this(Tuple2.of2());
    }
    public KthToLast(List<Tuple2<Tuple2<SingleLinkedList<X>, Integer>, Node<X>>> testCases) {
        this.testCases = testCases;
    }

    private final static List<Tuple2<Tuple2<SingleLinkedList<String>, Integer>, Node<String>>> INSTANCE_TESTCASES;
    public static final KthToLast<String> INSTANCE;
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


        INSTANCE_TESTCASES = Tuple3.<SingleLinkedList<String>, Integer, Node<String>>of3(
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
            ).stream().map(test -> Tuple2.make(Tuple2.make(test.getFirst(), test.getSecond()), test.getThird())).collect(Collectors.toUnmodifiableList());
        INSTANCE = new KthToLast<>(INSTANCE_TESTCASES);
    }


    private final List<Tuple2<Tuple2<SingleLinkedList<X>, Integer>, Node<X>>> testCases;
    private List<Tuple2<String, BiFunction<SingleLinkedList<X>, Integer, Node<X>>>> testFunctions = List.of(
        Tuple2.make("getKthToLast", this::getKthToLast)
    );

    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases, KthToLast.class, "");
    }
}
