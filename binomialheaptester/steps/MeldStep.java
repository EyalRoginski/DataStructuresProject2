package binomialheaptester.steps;

import binomialheaptester.State;

public class MeldStep implements Step {
    public int firstHeapIndex;
    public int secondHeapIndex;

    public MeldStep(int firstHeapIndex, int secondHeapIndex) {
        this.firstHeapIndex = firstHeapIndex;
        this.secondHeapIndex = secondHeapIndex;
    }

    @Override
    public String perform(State state){
        State.SingleHeapState firstHeap = state.heapStates.get(firstHeapIndex);
        State.SingleHeapState secondHeap = state.heapStates.remove(secondHeapIndex);
        firstHeap.heap.meld(secondHeap.heap); // Step-into here!
        firstHeap.items.putAll(secondHeap.items);
        return null;
    }

    public String toString(){
        return "Meld " + this.firstHeapIndex + " " + this.secondHeapIndex;
    }
}
