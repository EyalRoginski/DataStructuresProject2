package binomialheaptester.yourcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
    public int numOfTrees;

    public BinomialHeap() {
        size = 0;
        last = null;
        min = null;
        numOfTrees = 0;
    }

    /**
     * 
     * pre: key > 0
     *
     * Insert (key,info) into the heap and return the newly generated HeapItem.
     *
     */
    public HeapItem insert(int key, String info) {
        if (empty()) {
            HeapNode node = new HeapNode(key, info, null, null, null, 0);
            node.next = node;
            last = node;
            min = node;
            size = 1;
            numOfTrees = 1;
            return node.item;
        }
        BinomialHeap singletonHeap = new BinomialHeap();
        HeapItem item = singletonHeap.insert(key, info);
        meld(singletonHeap);
        return item;
    }

    /**
     * Update the `min` pointer. Goes over the entire tree list.
     */
    private void updateMin() {
        if (empty()) {
            return;
        }
        min = last;
        HeapNode current = last.next;
        while (current != last) {
            if (current.item.key <= min.item.key) {
                min = current;
            }
            current = current.next;
        }
    }

    /**
     * 
     * Delete the minimal item
     *
     */
    public void deleteMin() {
        if (empty()) {
            return;
        }

        HeapNode current = min.next;
        HeapNode beforeMin = current;
        HeapNode newMin = current;
        while (current != min) {
            if (current.item.key <= newMin.item.key) {
                newMin = current;
            }
            if (current.next == min) {
                beforeMin = current;
            }
            current = current.next;
        }

        BinomialHeap minHeap = new BinomialHeap();
        if (min.rank > 0) {
            minHeap.last = min.child;
            minHeap.size = (1 << min.rank) - 1;
            minHeap.updateMin();
            minHeap.numOfTrees = min.rank;
            current = minHeap.last;
            do {
                current.parent = null;
                current = current.next;
            } while (current != minHeap.last);
        }

        numOfTrees -= 1;
        beforeMin.next = min.next;
        this.size -= minHeap.size + 1;
        if (last == min) {
            last = beforeMin;
        }
        min = newMin;
        meld(minHeap);
    }

    /**
     * 
     * Return the minimal HeapItem, null if empty.
     *
     */
    public HeapItem findMin() {
        if (empty()) {
            return null;
        }
        return min.item;
    }

    /**
     * Swaps two HeapNodes' items.
     */
    private static void swapItems(HeapNode node1, HeapNode node2) {
        HeapItem temp = node1.item;
        node1.item = node2.item;
        node1.item.node = node1;
        node2.item = temp;
        node2.item.node = node2;
    }

    /**
     * Pushes an item up its tree to restore the Heap property.
     */
    private static void heapifyUp(HeapItem item) {
        while (item.node.parent != null && item.key < item.node.parent.item.key) {
            swapItems(item.node, item.node.parent);
        }
    }

    /**
     * 
     * pre: 0<diff<item.key
     * 
     * Decrease the key of item by diff and fix the heap.
     * 
     */
    public void decreaseKey(HeapItem item, int diff) {
        item.key -= diff;
        heapifyUp(item);
        if (item.key < min.item.key) {
            min = item.node;
        }
    }

    /**
     * 
     * Delete the item from the heap.
     *
     */
    public void delete(HeapItem item) {
        decreaseKey(item, item.key - min.item.key + 1);
        deleteMin();
    }

    /**
     * Add 3 HeapNodes together, assuming they are all not null.
     */
    private static AdderResult add3(HeapNode node1, HeapNode node2, HeapNode node3) {
        ArrayList<HeapNode> l = new ArrayList<HeapNode>(Arrays.asList(node1, node2, node3));
        HeapNode result = Collections.min(l, (a, b) -> Integer.compare(a.item.key, b.item.key));
        l.remove(result);
        HeapNode first = l.get(0);
        HeapNode second = l.get(1);
        HeapNode carry = link(first, second);
        return new AdderResult(result, carry);
    }

    /**
     * Returns the next HeapNode in the list, or null if we've reached the end.
     */
    private static HeapNode advance(HeapNode node, BinomialHeap heap) {
        if (node == heap.last || node == null) {
            return null;
        }
        return node.next;
    }

    /**
     * Result class of the `fullAdder` function.
     */
    private static class AdderResult {
        public final HeapNode result;
        public final HeapNode carry;

        public AdderResult(HeapNode result, HeapNode carry) {
            this.result = result;
            this.carry = carry;
        }
    }

    /**
     * Returns the HeapNode that isn't null, assuming there's only one such
     * HeapNode.
     */
    private static HeapNode getNotNull(HeapNode node1, HeapNode node2, HeapNode node3) {
        if (node1 != null) {
            return node1;
        }
        if (node2 != null) {
            return node2;
        }
        return node3;
    }

    /**
     * Adds the HeapNodes together, assuming exactly one in null.
     */
    private static HeapNode add2(HeapNode node1, HeapNode node2, HeapNode node3) {
        if (node1 == null) {
            return link(node2, node3);
        } else if (node2 == null) {
            return link(node1, node3);
        }
        return link(node1, node2);
    }

    /**
     * Adds three HeapNodes together (null or not) and returns the result.
     */
    private static AdderResult fullAdder(HeapNode node1, HeapNode node2, HeapNode node3) {
        int notNullNum = (node1 != null ? 1 : 0) + (node2 != null ? 1 : 0) + (node3 != null ? 1 : 0);
        switch (notNullNum) {
            case 0:
                return new AdderResult(null, null);
            case 1:
                return new AdderResult(getNotNull(node1, node2, node3), null);
            case 2:
                return new AdderResult(null, add2(node1, node2, node3));
            case 3:
                return add3(node1, node2, node3);
            default:
                return null;
        }
    }

    /**
     * 
     * Meld the heap with heap2
     *
     */
    public void meld(BinomialHeap heap2) {
        if (empty()) {
            last = heap2.last;
            min = heap2.min;
            size = heap2.size;
            numOfTrees = heap2.numOfTrees;
            return;
        }

        if (heap2.empty()) {
            return;
        }

        HeapNode node1 = last.next;
        HeapNode node2 = heap2.last.next;
        HeapNode sentinel = new HeapNode();
        HeapNode previous = sentinel;
        HeapNode carry = null;
        size += heap2.size;
        numOfTrees += heap2.numTrees();

        while ((carry != null) || (node1 != null && node2 != null)) {
            if (carry != null) {
                HeapNode nextNode1 = advance(node1, this);
                HeapNode nextNode2 = advance(node2, heap2);
                boolean addNode1 = false;
                if (node1 != null) {
                    addNode1 = node1.rank == carry.rank;
                }
                boolean addNode2 = false;
                if (node2 != null) {
                    addNode2 = node2.rank == carry.rank;
                }

                if (addNode1 || addNode2) {
                    numOfTrees--;
                }

                AdderResult result = fullAdder(carry, addNode1 ? node1 : null,
                        addNode2 ? node2 : null);
                if (result.result != null) {
                    previous.next = result.result;
                    previous = previous.next;
                }
                carry = result.carry;
                if (addNode1) {
                    node1 = nextNode1;
                }
                if (addNode2) {
                    node2 = nextNode2;
                }
            } else {
                if (node1.rank == node2.rank) {
                    numOfTrees--;
                    HeapNode nextNode1 = advance(node1, this);
                    HeapNode nextNode2 = advance(node2, heap2);
                    AdderResult result = fullAdder(node1, node2, null);
                    carry = result.carry;
                    node1 = nextNode1;
                    node2 = nextNode2;
                } else {
                    if (node1.rank < node2.rank) {
                        HeapNode nextNode1 = advance(node1, this);
                        AdderResult result = fullAdder(node1, null, null);
                        carry = result.carry;
                        previous.next = result.result;
                        previous = previous.next;
                        node1 = nextNode1;
                    } else {
                        HeapNode nextNode2 = advance(node2, heap2);
                        AdderResult result = fullAdder(node2, null, null);
                        carry = result.carry;
                        previous.next = result.result;
                        previous = previous.next;
                        node2 = nextNode2;
                    }
                }
            }
        }

        if (node1 == null && node2 == null) {
            this.last = previous;
        } else if (node1 != null) {
            previous.next = node1;
        } else {
            previous.next = node2;
            this.last = heap2.last;
        }
        this.last.next = sentinel.next;
        this.min = this.min.item.key <= heap2.min.item.key ? this.min : heap2.min;

        pushMinUp();
    }

    /**
     * Pushes the `min` pointer up its tree.
     */
    private void pushMinUp() {
        while (min.parent != null) {
            min = min.parent;
        }
    }

    /**
     * 
     * Return the number of elements in the heap
     * 
     */
    public int size() {
        return size;
    }

    /**
     * 
     * The method returns true if and only if the heap
     * is empty.
     * 
     */
    public boolean empty() {
        return size == 0;
    }

    /**
     * 
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees() {
        return numOfTrees;
    }

    /**
     * Links two HeapNodes of the same rank, as seen in class.
     */
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

        public HeapNode() {

        }

        public HeapNode(
                int key,
                String info,
                HeapNode child,
                HeapNode next,
                HeapNode parent,
                int rank) {
            this.item = new HeapItem(this, key, info);
            this.child = child;
            this.next = next;
            this.parent = parent;
            this.rank = rank;
        }

        /**
         * Add a node of the same rank as child (does not check keys)
         */
        public void addAsChild(HeapNode node) {
            if (child == null) {
                child = node;
                node.next = node;
            } else {
                node.next = child.next;
                child.next = node;
                child = node;
            }
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

        public HeapItem(HeapNode node, int key, String info) {
            this.node = node;
            this.key = key;
            this.info = info;
        }
    }
}
