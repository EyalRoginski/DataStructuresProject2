package binomialheaptester.steps;
import binomialheaptester.State;
import binomialheaptester.Utils;
import binomialheaptester.yourcode.BinomialHeap;


public class InsertStep implements Step {
    public int heapIndex;
    public int key;
    public String info;

    public InsertStep(int heapIndex, int key, String info) {
        this.heapIndex = heapIndex;
        this.key = key;
        this.info = info;
    }

    @Override
    public String perform(State state) {
        State.SingleHeapState heapState = state.heapStates.get(this.heapIndex);
        BinomialHeap.HeapItem insertedItem = heapState.heap.insert(this.key, this.info); // Step-into here!
        if (insertedItem == null) {
            return "insert() returned null (instead of the inserted item)";
        }
        if (!insertedItem.info.equals(this.info) || insertedItem.key != this.key) {
            return String.format(
                    "insert() returned an incorrect item (not the inserted item). Expected (%d, %s), got (%d, %s)",
                    this.key, this.info, insertedItem.key, insertedItem.info
            );
        }
        BinomialHeap.HeapNode expectedInsertedNode = Utils.findByInfo(heapState.heap, info);
        if (expectedInsertedNode == null) {
            return String.format("Inserted item (%d, %s) not found in the heap", this.key, this.info);
        }
        if (expectedInsertedNode.item != insertedItem) {
            return "insert() returned an incorrect item (not the inserted item). It has the correct key " +
                    "and info, but is a different object from the one found in the heap";
        }
        heapState.items.put(info, this.key);
        return null;
    }

    public String toString(){
        return "Insert " + this.heapIndex + " " + this.key + " " + this.info;
    }


}
