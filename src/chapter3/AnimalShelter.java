package chapter3;

import common.DoubleLinkedList;
import common.EmptyStackException;
import common.SimpleQueue;

/**
 * An animal shelter, which holds only dogs and cats, operates on a strictly"first in, first
 * out" basis. People must adopt either the "oldest" (based on arrival time) of all animals at the shelter,
 * or they can select whether they would prefer a dog or a cat (and will receive the oldest animal of
 * that type). They cannot select which specific animal they would like. Create the data structures to
 * maintain this system and implement operations such as enqueue, dequeueAny, dequeueDog,
 * and dequeueCat. You may use the built-in Linked list data structure.
 */
public class AnimalShelter {
    private static class OrderWrapper<X> {
        private final int order;
        private final X value;
        public OrderWrapper(int order, X value) {
            this.order = order;
            this.value = value;
        }

        public X getValue() {
            return value;
        }

        public int getOrder() {
            return order;
        }

        @Override
        public String toString() {
            return  value + " " + order;
        }
    }
    public interface Animal {
    }
    public static class Cat implements Animal{
        private final String name;
        public Cat() {
            this("Mr. Bigglesworth");
        }
        public Cat(String name) {
            this.name = name;
        }
        public String toString() {
            return name;
        }
    }
    public static class Dog implements Animal{
        private final String name;
        public Dog(String name) {
            this.name = name;
        }
        public Dog() {
            this("Nibbler");
        }
        public String toString() {
            return name;
        }
    }

    /**
     *   1 2 3 7 8 9
     *   4 5 6 10
     */
    public static class AnimalQueue implements SimpleQueue<Animal> {
        private SimpleQueue<OrderWrapper<Cat>> catQueue = new DoubleLinkedList<>();
        private SimpleQueue<OrderWrapper<Dog>> dogQueue = new DoubleLinkedList<>();

        public void removeLastCat() {
            catQueue.removeLast();
        }
        public Cat getLastCat() {
            return catQueue.getLast().getValue();
        }
        public void removeLastDog() {
            dogQueue.removeLast();
        }
        public Dog getLastDog() {
            return dogQueue.getLast().getValue();
        }

        private static int getOldestOrder(SimpleQueue<? extends OrderWrapper<?>> queue) {
            return queue.isEmpty() ? 0 : queue.getLast().getOrder();
        }

        @Override
        public void addFirst(Animal elem) {
            int newOrder = Math.min(getOldestOrder(dogQueue), getOldestOrder(catQueue)) + 1;
            if(elem instanceof Dog) {
                dogQueue.addFirst(new OrderWrapper<>(newOrder, (Dog)elem));
            } else if(elem instanceof Cat) {
                catQueue.addFirst(new OrderWrapper<>(newOrder, (Cat)elem));
            } else {
                throw new UnsupportedOperationException("Unsupported class : " + elem.getClass().getSimpleName());
            }
        }

        // gets the oldest animal in the animal queue
        @Override
        public Animal getLast() {
            if(isEmpty())
                throw new EmptyStackException();
            if(getOldestOrder(dogQueue) > getOldestOrder(catQueue)) {
                return catQueue.getLast().getValue();
            } else {
                return dogQueue.getLast().getValue();
            }
        }

        @Override
        public void removeLast() {
            if(isEmpty())
                throw new EmptyStackException();
            if(getOldestOrder(dogQueue) > getOldestOrder(catQueue)) {
                catQueue.removeLast();
            } else {
                dogQueue.removeLast();
            }
        }

        @Override
        public boolean isEmpty() {
            return catQueue.isEmpty() && dogQueue.isEmpty();
        }
    }
}
