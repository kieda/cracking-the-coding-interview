# Cracking the Coding Interview

My special solutions, many have a better runtime complexity or space than what's in the book, and where possible I implement a parallel algorithm to solve the problem. I also state the work, span, and space used for any parallel solution. 

All solutions are in Java.

Some really cool classes: 
## common.tree.AVLTree
k-bounded AVL tree. Normal AVL trees can have a balance factors of -1 to +1 to ensure maximum depth between any two nodes does not deviate more than one.
My implementation can have balance factors between -k to +k for k >= 1. This is also done with only storing the balance factor, and not any height information. This allows most cases to remain O(1) rather than having to traverse up the tree to update the height every insertion or deletion.

## common.tree.MinHeap
check out the traverseNodes method. There's a very fast way to traverse a complete binary tree using (index XOR (index + 1)) to determine the route we should traverse.

## chapter1.*
parallel implementations for every problem. OneAway is a pretty cool solution!

## chapter3.StackOfPlates
stack of plates implementation that has amortized O(1) removal from the middle nodes

## chapter4.FirstCommonAncestor
Finds the common ancestor between two nodes, without using links to parents. Unlike the book solution, this only uses O(1) space, and runs in the same runtime complexity. This solution modifies the Morris traversal to find the ancestor's nodes
