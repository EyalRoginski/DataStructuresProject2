package binomialheaptester;

import binomialheaptester.yourcode.BinomialHeap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Validator {

    public static String validateState(State state) {
        for (int i = 0; i < state.heapStates.size(); i++) {
            State.SingleHeapState singleHeapState = state.heapStates.get(i);
            String errorMessage = validateSingleHeapState(singleHeapState);
            if (errorMessage != null) {
                return "(heap %d) %s".formatted(i, errorMessage);
            }
        }
        return null;
    }

    public static String validateSingleHeapState(State.SingleHeapState state) {
        String errorMessage = validateBinomialHeapStucture(state.heap);
        if (errorMessage != null) {
            return errorMessage;
        }
        List<BinomialHeap.HeapNode> actualNodes = Utils.iterateHeap(state.heap);
        Map<String, Integer> expectedNodes = state.items;
        if (expectedNodes.size() != state.heap.size) {
            return "The heap has %d nodes. Expected is %d".formatted(
                    state.heap.size, expectedNodes.size()
            );
        }
        if (expectedNodes.size() != state.heap.size()) {
            return "heap.size() returned %d nodes. Expected is %d".formatted(
                    state.heap.size, expectedNodes.size()
            );
        }
        if (expectedNodes.size() == 0 && !state.heap.empty()) {
            return "heap.empty() returned false for an empty heap";
        }
        if (expectedNodes.size() != 0 && state.heap.empty()) {
            return "heap.empty() returned true for a non-empty heap";
        }
        for (BinomialHeap.HeapNode node : actualNodes) {
            if (!expectedNodes.containsKey(node.item.info)) {
                return "Node %s should not be in the heap".formatted(node.item.info);
            }
            if (expectedNodes.get(node.item.info) != node.item.key) {
                return "Node %s should have a key of %d, not %d".formatted(
                        node.item.info, expectedNodes.get(node.item.info), node.item.key
                );
            }
        }
        if (state.items.size() > 0) {
            int expectedMinKey = state.getMinKey();
            BinomialHeap.HeapItem minItem = state.heap.findMin();
            if (minItem == null) {
                return "findMin returned null for a non empty heap.";
            }
            if (minItem.key != expectedMinKey) {
                return "Expected minimal key in the heap is %d, findMin returned (%d, %s)".formatted(
                        expectedMinKey, minItem.key, minItem.info
                );
            }
            if (minItem.node.parent != null) {
                return "findMin returned a non-root node: (%d, %s)".formatted(minItem.key, minItem.info);
            }
        }
        else {
            BinomialHeap.HeapItem minItem = state.heap.findMin();
            if (minItem != null) {
                return "findMin returned a non-null result (%d, %s) for an empty heap.".formatted(
                        minItem.key, minItem.info
                );
            }
            if (state.heap.last != null) {
                return "BinomialHeap.last is not null (%d, %s) in an empty heap.".formatted(
                        state.heap.last.item.key, state.heap.last.item.info
                );
            }
        }

        return null;
    }

    public static String validateBinomialHeapStucture(BinomialHeap heap) {
        if (heap.last == null) {
            if (heap.size != 0) {
                return "The heap is empty (last is null) but has size %d".formatted(heap.size);
            }
            return null;
        }

        Set<String> encounteredInfos = new HashSet<>();
        BinomialHeap.HeapNode currNode = heap.last;
        int expectedSize = 0;
        do {
            String errorMessage = validateBinomialTree(currNode, encounteredInfos);
            if (errorMessage != null) {
                return errorMessage;
            }

            if (currNode.next == null) {
                return "The list of roots is not circular (next of %s is null)".formatted(currNode.item.info);
            }
            if (currNode.parent != null) {
                return "Parent pointer of %s is %s, but it is in the root list".formatted(
                        currNode.item.info, currNode.parent.item.info
                );
            }
            if (currNode != heap.last && currNode.rank >= currNode.next.rank) {
                return String.format(
                        "The roots are not in (strictly) ascending rank order, %s (rank %d) comes before %s (rank %d)",
                        currNode.item.info, currNode.rank, currNode.next.item.info, currNode.rank
                );
            }
            expectedSize += Math.pow(2, currNode.rank);
            currNode = currNode.next;
        } while (currNode != heap.last);

        if (heap.size != expectedSize) {
            return "The heap has %d nodes but has a size property of %d".formatted(expectedSize, heap.size);
        }

        int expectedNumOfTrees = getExpectedNumOfTrees(expectedSize);
        int actualNumOfTrees = heap.numTrees();
        if (actualNumOfTrees != expectedNumOfTrees) {
            return "The heap has %d trees but numTrees() returned %d".formatted(expectedNumOfTrees, actualNumOfTrees);
        }

        return null;
    }
    public static String validateBinomialTree(BinomialHeap.HeapNode root, Set<String> encounteredInfos) {

        if (root == null){
            return null;
        }

        if (root.item == null) {
            return "The item of a node in the heap is null.";
        }

        if (root.item.node != root) {
            return "Mismatch between node.item and item.node pointers for a node with value %s".formatted(
                    root.item.info
            );
        }

        if (encounteredInfos.contains(root.item.info)) {
            return String.format(
                    "Info %s found twice in the heap. This means there is a duplicate node - the tester uses unique infos",
                    root.item.info
            );
        }
        encounteredInfos.add(root.item.info);

        if (root.child == null) {
            if (root.rank != 0) {
                return "Childless node %s has a rank of %d".formatted(root.item.info, root.rank);
            }
            return null;
        }

        BinomialHeap.HeapNode currChild = root.child.next; // first child
        if (currChild == null) {
            return "The list of children of %s is not circular (next of %s is null)".formatted(
                    root.item.info, root.child.item.info
            );

        }
        int childCount = 0;
        do {
            if (currChild.next == null) {
                return "The list of children of %s is not circular (next of %s is null)".formatted(
                        root.item.info, currChild.item.info
                );
            }
            if (currChild.parent == null) {
                return "Parent pointer of %s is null, but it is in the children list of %s".formatted(
                        currChild.item.info, root.item.info
                );
            }
            if (currChild.parent != root) {
                return "Parent pointer of %s is %s, but it is in the children list of %s".formatted(
                        currChild.item.info, currChild.parent.item.info, root.item.info
                );
            }
            if (currChild.rank != childCount) {
                return "%d-th child of %s has a rank of %d. Should be %d".formatted(
                        childCount, root.item.info, currChild.rank, childCount
                );
            }

            String childError = validateBinomialTree(currChild, encounteredInfos);
            if (childError != null){
                return childError;
            }

            childCount++;
            currChild = currChild.next;

        } while (currChild != root.child.next);

        if (root.rank != childCount) {
            return "%s is of rank %d, but it has %d children".formatted(
                    root.item.info, root.rank, childCount
            );
        }

        return null;
    }

    private static int getExpectedNumOfTrees(int size) {
        int numOfTrees = 0;
        while (size != 0) {
            if (size % 2 == 1) {
                numOfTrees++;
            }
            size /=2;
        }
        return numOfTrees;
    }
}
