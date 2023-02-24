package chapter4;

import common.tree.BinaryTree;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * You are given a binary tree in which each node contains an integer value (which
 * might be positive or negative). Design an algorithm to count the number of paths that sum to a
 * given value. The path does not need to start or end at the root or a leaf, but it must go downwards
 * (traveling only from parent nodes to child nodes).
 */
public class PathsWithSum {
    public int numPathsRecursive(int target, BinaryTree<Integer>.Node node, int currentSum, Map<Integer, Integer> ancestorCounts) {
        if(node == null)
            return 0;
        int totalPaths = 0;
        int newSum = currentSum + node.getElem();
        if(newSum == target) {
            totalPaths++;
        }

        int delta = currentSum - target;
        totalPaths += ancestorCounts.getOrDefault(delta, 0);
        ancestorCounts.put(newSum, ancestorCounts.getOrDefault(newSum, 0) + 1);
        totalPaths += numPathsRecursive(target, node.getLeft(), newSum, ancestorCounts);
        totalPaths += numPathsRecursive(target, node.getRight(), newSum, ancestorCounts);

        if(ancestorCounts.getOrDefault(newSum, 0) <= 1) {
            ancestorCounts.remove(newSum);
        } else {
            ancestorCounts.put(newSum, ancestorCounts.get(newSum) - 1);
        }
        return totalPaths;
    }
    public int numPaths(int target, BinaryTree<Integer> tree) {
        return numPathsRecursive(target, tree.getHead(), 0, new HashMap<>());
    }
}
