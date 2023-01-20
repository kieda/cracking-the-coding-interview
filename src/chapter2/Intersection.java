package chapter2;

import common.lists.SingleLinkedList;
import common.tests.TestException;
import common.tests.Testable;
import common.tuple.Tuple2;

import java.util.List;
import java.util.function.BiFunction;

import static common.lists.SingleLinkedList.Node;

/**
 * Given two singly linked lists, determine if the two lists intersect. Return the intersecting node.
 * Note that the intersection is defined based on reference, not value.
 * That is, if the kth node of the first linked list is the exact same node (by reference) as the jth node of the second
 * linked list, then they are intersecting.
 */
public class Intersection<X> implements Testable {
    public Node<X> hasIntersection(SingleLinkedList<X> listA, SingleLinkedList<X> listB) {
        /**
         * Cases:
         *    A -> B -> C -> D -> E -> F
         *         Z -> Y -^
         *
         *         Z -> Y -v
         *    A -> B -> C -> D -> E -> F
         *
         *    A -> B -> C -> D -> E -> F
         *         Z -> Y -> X -> W -> V
         *
         *    (null)
         *    (null)
         */

        // what we want to do:
        // 1. traverse both lists to the end
        // 2. if they don't match by reference, return null
        // 3. otherwise we know that they must have intersected at some point in time
        // Alternative option: reverse the two lists, then compare pointer equality till they don't match

        Node<X> listANode = listA.getHead();
        Node<X> listBNode = listB.getHead();
        Node<X> listAPrevious = null;
        Node<X> listBPrevious = null;
        while(listANode != null && listBNode != null) {
            listAPrevious = listANode;
            listBPrevious = listBNode;
            listANode = listANode.getNext();
            listBNode = listBNode.getNext();
        }

        // run to the end of the length
        int listAExtra = 0;
        int listBExtra = 0;
        while(listANode != null) {
            listAExtra++;
            listAPrevious = listANode;
            listANode = listANode.getNext();
        }
        while(listBNode != null) {
            listBExtra++;
            listBPrevious = listBNode;
            listBNode = listBNode.getNext();
        }
        if(listAPrevious != listBPrevious) {
            return null;
        }
        listANode = listA.getHead();
        listBNode = listB.getHead();
        // move forward from the start position the extra amount of space in the two lists
        for(int i = 0; i < listAExtra; i++)
            listANode = listANode.getNext();
        for(int i = 0; i < listBExtra; i++)
            listBNode = listBNode.getNext();
        while(listANode != listBNode) {
            listANode = listANode.getNext();
            listBNode = listBNode.getNext();
        }

        return listANode;
    }

    public Intersection(List<Tuple2<Tuple2<SingleLinkedList<X>, SingleLinkedList<X>>, Node<X>>> testCases) {
        this.testCases = testCases;
    }

    // Tuple2<Tuple2<SingleLinkedList<X>, SingleLinkedList<X>>, Node<X>>
    private final static List<Tuple2<Tuple2<SingleLinkedList<String>, SingleLinkedList<String>>, Node<String>>> INSTANCE_TESTCASES;
    public static final Intersection<String> INSTANCE;
    static {

        Node<String> node1C1 = new Node<>("1C1");
        Node<String> node1C2 = new Node<>("1C2");
        node1C1.setNext(node1C2);

        SingleLinkedList<String> list1A = new SingleLinkedList<>();
            // 1A1 -> 1A2 -> 1C1 -> 1C2
            Node<String> node1A1 = new Node<>("1A1");
            Node<String> node1A2 = new Node<>("1A2");

            node1A1.setNext(node1A2);
            node1A2.setNext(node1C1);
        list1A.setHead(node1A1);

        SingleLinkedList<String> list1B = new SingleLinkedList<>();
            // 1B1 -> 1B2 -> 1B3 -> 1B4 -> 1B5 -> 1C1 -> 1C2
            Node<String> node1B1 = new Node<>("1B1");
            Node<String> node1B2 = new Node<>("1B2");
            Node<String> node1B3 = new Node<>("1B3");
            Node<String> node1B4 = new Node<>("1B4");
            Node<String> node1B5 = new Node<>("1B5");

            node1B1.setNext(node1B2);
            node1B2.setNext(node1B3);
            node1B3.setNext(node1B4);
            node1B4.setNext(node1B5);
            node1B5.setNext(node1C1);
        list1B.setHead(node1B1);

        SingleLinkedList<String> list2A = new SingleLinkedList<>();
            Node<String> node2A1 = new Node<>("2A1");
            Node<String> node2A2 = new Node<>("2A2");
            Node<String> node2A3 = new Node<>("2A3");
            node2A1.setNext(node2A2);
            node2A2.setNext(node2A3);
        list2A.setHead(node2A1);

        SingleLinkedList<String> list2B = new SingleLinkedList<>();
            Node<String> node2B1 = new Node<>("2B1");
            Node<String> node2B2 = new Node<>("2B2");
            Node<String> node2B3 = new Node<>("2B3");
            Node<String> node2B4 = new Node<>("2B4");

            node2B1.setNext(node2B2);
            node2B2.setNext(node2B3);
            node2B3.setNext(node2B4);
        list2B.setHead(node2B1);
        SingleLinkedList<String> list2C = new SingleLinkedList<>();

        SingleLinkedList<String> list2D = new SingleLinkedList<>();
        list2D.setHead(node2B3);
        INSTANCE_TESTCASES = Tuple2.of2(
            Tuple2.make(list1A, list1B), node1C1,
            Tuple2.make(list2A, list2B), null,
            Tuple2.make(list2A, list2C), null,
            Tuple2.make(list2B, list2D), node2B3
        );
        INSTANCE = new Intersection<>(INSTANCE_TESTCASES);
    }

    private final List<Tuple2<Tuple2<SingleLinkedList<X>, SingleLinkedList<X>>, Node<X>>> testCases;
    private final List<Tuple2<String, BiFunction<SingleLinkedList<X>, SingleLinkedList<X>, Node<X>>>> testFunctions = List.of(
        Tuple2.make("hasIntersection", this::hasIntersection)
    );
    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases, Intersection.class, "");
    }
}
