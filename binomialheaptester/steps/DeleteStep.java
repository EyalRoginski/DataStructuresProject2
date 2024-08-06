package binomialheaptester.steps;

import binomialheaptester.State;
import binomialheaptester.Utils;
import binomialheaptester.yourcode.BinomialHeap;

public class DeleteStep implements Step{
    public int heapIndex;
    public String info;

    public DeleteStep(int heapIndex, String info) {
        this.heapIndex = heapIndex;
        this.info = info;
    }

    @Override
    public String perform(State state) {
        State.SingleHeapState heapState = state.heapStates.get(this.heapIndex);
        BinomialHeap.HeapNode node = Utils.findByInfo(heapState.heap, this.info);
        assert node != null;
        heapState.heap.delete(node.item); // Step-into here!
        heapState.items.remove(info);
        return null;
    }

    public String toString(){
        return "Delete " + this.heapIndex + " " + this.info;
    }
}
