package binomialheaptester;

import binomialheaptester.yourcode.BinomialHeap;

import java.util.ArrayList;
import java.util.List;

public class Utils {


    public static BinomialHeap.HeapNode findByInfo(BinomialHeap heap, String info) {
        List<BinomialHeap.HeapNode> nodes = iterateHeap(heap);
        for (BinomialHeap.HeapNode node : nodes) {
            if (node.item.info.equals(info)){
                return node;
            }
        }
        return null;
    }

    public static List<BinomialHeap.HeapNode> iterateHeap(BinomialHeap heap) {
        List<BinomialHeap.HeapNode> result = new ArrayList<>();
        BinomialHeap.HeapNode firstRoot = heap.last;
        if (firstRoot == null) {
            return result;
        }
        BinomialHeap.HeapNode currRoot = firstRoot;
        do {
            iterateHeap(result, currRoot);
            currRoot = currRoot.next;
        }
        while (currRoot != firstRoot);

        return result;
    }

    public static void iterateHeap(List<BinomialHeap.HeapNode> currList, BinomialHeap.HeapNode root) {
        currList.add(root);
        BinomialHeap.HeapNode firstChild = root.child;
        if (firstChild == null) {
            return;
        }
        BinomialHeap.HeapNode currChild = firstChild;
        do {
            iterateHeap(currList, currChild);
            currChild = currChild.next;
        }
        while (currChild != firstChild);
    }
}
