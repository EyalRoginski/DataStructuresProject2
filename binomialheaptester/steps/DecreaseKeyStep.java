package binomialheaptester.steps;

import binomialheaptester.State;
import binomialheaptester.Utils;
import binomialheaptester.yourcode.BinomialHeap;

public class DecreaseKeyStep implements Step{
    public int heapIndex;
    public String info;
    public int diff;

    public DecreaseKeyStep(int heapIndex, String info, int diff) {
        this.heapIndex = heapIndex;
        this.info = info;
        this.diff = diff;
    }

    @Override
    public String perform(State state) {
        State.SingleHeapState heapState = state.heapStates.get(this.heapIndex);
        BinomialHeap.HeapNode node = Utils.findByInfo(heapState.heap, this.info);
        assert node != null;
        heapState.heap.decreaseKey(node.item, this.diff); // Step-into here!
        int currKey = heapState.items.get(info);
        heapState.items.put(info, currKey - diff);
        return null;
    }

    public String toString(){
        return "DecreaseKey " + this.heapIndex + " " + this.info + " " + this.diff;
    }
}
