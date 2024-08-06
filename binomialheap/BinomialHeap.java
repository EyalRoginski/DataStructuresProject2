package binomialheap;

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

    public static void main(String[] args) {
        BinomialHeap heap1 = new BinomialHeap();
        BinomialHeap heap2 = new BinomialHeap();
        heap1.last = new HeapNode(0, "0", null, null, null, 0);
        heap1.last.next = heap1.last;
        heap1.min = heap1.last;
        heap2.last = new HeapNode(1, "1", null, null, null, 0);
        heap2.last.next = heap2.last;
        heap2.min = heap2.last;
        System.out.println("Starting meld");
        heap1.meld(heap2);
        System.out.println(String.format("heap1 last rank: %d", heap1.last.rank));
        System.out.println(String.format("heap1 last key: %d", heap1.last.item.key));
        System.out.println(String.format("heap1 last child key: %d", heap1.last.child.item.key));
    }

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

    private static AdderResult add3(HeapNode node1, HeapNode node2, HeapNode node3) {
        List<HeapNode> l = Arrays.asList(node1, node2, node3);
        HeapNode result = Collections.min(l, (a, b) -> Integer.compare(a.item.key, b.item.key));
        l.remove(result);
        HeapNode first = l.get(0);
        HeapNode second = l.get(1);
        HeapNode carry = link(first, second);
        return new AdderResult(result, carry);
    }

    private static HeapNode advance(HeapNode node, BinomialHeap heap) {
        if (node == heap.last || node == null) {
            return null;
        }
        return node.next;
    }

    private static class AdderResult {
        public final HeapNode result;
        public final HeapNode carry;

        public AdderResult(HeapNode result, HeapNode carry) {
            this.result = result;
            this.carry = carry;
        }
    }

    private static HeapNode getNotNull(HeapNode node1, HeapNode node2, HeapNode node3) {
        if (node1 != null) {
            return node1;
        }
        if (node2 != null) {
            return node2;
        }
        return node3;
    }

    private static HeapNode add2(HeapNode node1, HeapNode node2, HeapNode node3) {
        if (node1 == null) {
            return link(node2, node3);
        } else if (node2 == null) {
            return link(node1, node3);
        }
        return link(node1, node2);
    }

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
        HeapNode node1 = last.next;
        HeapNode node2 = heap2.last.next;
        HeapNode sentinel = new HeapNode();
        HeapNode previous = sentinel;
        HeapNode carry = null;

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
