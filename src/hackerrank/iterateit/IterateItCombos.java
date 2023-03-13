package hackerrank.iterateit;

import common.lists.SimpleStack;
import common.lists.SingleLinkedList;

import java.util.ArrayList;
import java.util.List;

public class IterateItCombos {

    private static class Tree{
        int coin;
        int val;
        List<Tree> nodes = null;

        @Override
        public String toString() {
            return val + "*" + coin;
        }
    }
    public static void printTree(Tree tree, SingleLinkedList<Tree> traversal) {
        if(tree == null) {
            System.out.println("None");
            return;
        }
        traversal.addFirst(tree);
        if(tree.nodes == null) {
            SingleLinkedList.Node<Tree> node = traversal.getHead();
            int accumulation = 0;
            while(node != null) {
                accumulation += node.getItem().coin * node.getItem().val;
                System.out.print(node.getItem().val + "*" + node.getItem().coin);
                node = node.getNext();
                if(node != null) {
                    System.out.print(" + ");
                }
            }

            System.out.println(" = " + accumulation);
            traversal.removeFirst();
            return;
        }
        for(Tree child : tree.nodes) {
            printTree(child, traversal);
        }
        traversal.removeFirst();
    }
    public static Tree combos(int currentValue, int idx, int[] coins) {
        if(idx == coins.length) {
            if(currentValue == 1) {
                return new Tree();
            }
            return null;
        }
        List<Tree> workingNodes = null;
        for(int i = -15; i <= 15; i++) {
            int val = coins[idx];
            int newValue = currentValue + val * i;
            Tree result = combos(newValue, idx + 1, coins);
            if(result != null) {
                result.val = i;
                result.coin = val;
                if(workingNodes == null) {
                     workingNodes = new ArrayList<>();
                }
                workingNodes.add(result);
            }
        }
        if(workingNodes != null && !workingNodes.isEmpty()) {
            Tree result = new Tree();
            result.nodes = workingNodes;
            return result;
        }
        return null;
    }

    public static void main(String[] args) {
        // this is a "minimal" combo for the following. We verify that
        // 17*85 - 9*2 - 12*174 + 4*172
        int[] coins = {2, 85, 87, 172, 174};
        Tree t = combos(0, 0, coins);

        printTree(t, new SingleLinkedList<>());
    }
}
