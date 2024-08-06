package binomialheaptester.steps;

import binomialheaptester.State;

public class InitializeStep implements Step{
    public int heapCount;

    public InitializeStep(int heapCount) {
        this.heapCount = heapCount;
    }
    @Override
    public String perform(State state) {
        for (int i = 0; i < heapCount; i++) {
            state.heapStates.add(new State.SingleHeapState());
        }
        return null;
    }

    public String toString(){
        return "Initialize " + this.heapCount;
    }
}
