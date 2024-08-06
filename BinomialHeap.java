import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * BinomialHeap
 *
 * An implementation of binomial heap over positive integers.
 *
 */
public class BinomialHeap {
    public int size;
    public HeapNode last;
    public HeapNode min;

    /**
     * 
     * pre: key > 0
     *
     * Insert (key,info) into the heap and return the newly generated HeapItem.
     *
     */
    public HeapItem insert(int key, String info) {
        return null; // should be replaced by student code
    }

    /**
     * 
     * Delete the minimal item
     *
     */
    public void deleteMin() {
        return; // should be replaced by student code

    }

    /**
     * 
     * Return the minimal HeapItem, null if empty.
     *
     */
    public HeapItem findMin() {
        return null; // should be replaced by student code
    }

    /**
     * 
     * pre: 0<diff<item.key
     * 
     * Decrease the key of item by diff and fix the heap.
     * 
     */
    public void decreaseKey(HeapItem item, int diff) {
        return; // should be replaced by student code
    }

    /**
     * 
     * Delete the item from the heap.
     *
     */
    public void delete(HeapItem item) {
        return; // should be replaced by student code
    }

    private HeapNode add3(HeapNode node1, HeapNode node2, HeapNode node3, HeapNode previous) {
        List<HeapNode> l = Arrays.asList(node1, node2, node3);
        HeapNode min = Collections.min(l, (a, b) -> Integer.compare(a.key, b.key));
        l.remove(min);
        HeapNode first = l.get(0);
        HeapNode second = l.get(1);
        HeapNode carry = link(first, second);
        previous.next = min;
        return carry;
    }

    /**
     * 
     * Meld the heap with heap2
     *
     */
    public void meld(BinomialHeap heap2) {
        HeapNode node1 = last.next;
        HeapNode node2 = heap2.last.next;
        HeapNode previous = last;
        HeapNode carry = null;
        while (true) {
            if (carry) {
                if (node1.rank == carry.rank && node2.rank == carry.rank) {

                }
            }
        }
        // while (node1.rank != node2.rank) {
        // if (node1.rank < node2.rank) {
        // previous.next = node1;
        // previous = node1;
        // node1 = node1.next;
        // } else {
        // previous.next = node2;
        // previous = node2;
        // node2 = node2.next;
        // }
        // }
        // HeapNode next = node1.next;
        // HeapNode min = link(node1, node2);
        // min.next = next;
        // previous.next = min;
    }

    /**
     * 
     * Return the number of elements in the heap
     * 
     */
    public int size() {
        return 42; // should be replaced by student code
    }

    /**
     * 
     * The method returns true if and only if the heap
     * is empty.
     * 
     */
    public boolean empty() {
        return false; // should be replaced by student code
    }

    /**
     * 
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees() {
        return 0; // should be replaced by student code
    }

    public static HeapNode link(HeapNode node1, HeapNode node2) {
        if (node1.item.key <= node2.item.key) {
            node1.addAsChild(node2);
            return node1;
        } else {
            node2.addAsChild(node1);
            return node2;
        }
    }

    /**
     * Class implementing a node in a Binomial Heap.
     * 
     */
    public static class HeapNode {
        public HeapItem item;
        public HeapNode child;
        public HeapNode next;
        public HeapNode parent;
        public int rank;

        /**
         * Add a node of the same rank as child (does not check keys)
         */
        public void addAsChild(HeapNode node) {
            node.next = child.next;
            child.next = node;
            child = node;
            node.parent = this;
            rank++;
        }
    }

    /**
     * Class implementing an item in a Binomial Heap.
     * 
     */
    public static class HeapItem {
        public HeapNode node;
        public int key;
        public String info;
    }
}
