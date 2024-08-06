package binomialheaptester;

import binomialheaptester.yourcode.BinomialHeap;

import java.util.*;

public class State {

    public List<SingleHeapState> heapStates;

    public State(){
        heapStates = new ArrayList<>();
    }


    public static class SingleHeapState {
        public HashMap<String, Integer> items; // info -> key mapping
        public BinomialHeap heap;

        public SingleHeapState() {
            this.items = new HashMap<>();
            this.heap = new BinomialHeap();
        }

        public List<String> getPossibleMinKeyInfos() {
            int minKey = 0;
            List<String> minInfos = new ArrayList<>();
            for (Map.Entry<String, Integer> entry: this.items.entrySet()) {
                if (minInfos.size() == 0) {
                    minKey = entry.getValue();
                    minInfos.add(entry.getKey());
                }
                else if (entry.getValue() == minKey){
                    minInfos.add(entry.getKey());
                }
                else if (minKey > entry.getValue()){
                    minKey = entry.getValue();
                    minInfos = new ArrayList<>();
                    minInfos.add(entry.getKey());
                }
            }
            return minInfos;
        }


        public Integer getMinKey() {
            Integer minKey = null;
            for (Map.Entry<String, Integer> entry: this.items.entrySet()) {
                if (minKey == null) {
                    minKey = entry.getValue();
                }
                else if (minKey > entry.getValue()){
                    minKey = entry.getValue();
                }
            }
            return minKey;
        }

    }
}
