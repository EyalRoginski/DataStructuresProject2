package binomialheaptester.steps;

import binomialheaptester.State;
import binomialheaptester.yourcode.BinomialHeap;

public class DeleteMinStep implements Step{
    public int heapIndex;

    public DeleteMinStep(int heapIndex) {
        this.heapIndex = heapIndex;
    }
    @Override
    public String perform(State state) {
        State.SingleHeapState heapState = state.heapStates.get(this.heapIndex);
        int expectedMinKey = heapState.getMinKey();
        BinomialHeap.HeapItem min = heapState.heap.findMin();
        if (min == null) {
            return "findMin returned null for a non-empty heap";
        }
        if (min.key != expectedMinKey){
            return "findMin returned the node (%s, %s). The minimal key is %s".formatted(
                    min.key, min.info, expectedMinKey
            );
        }
        heapState.heap.deleteMin(); // Step-into here!
        heapState.items.remove(min.info);
        return null;
    }

    public String toString(){
        return "DeleteMin " + this.heapIndex;
    }
}
