package binomialheaptester.steps;

import binomialheaptester.State;

import java.util.*;

public class StepGenerator{

    private int maxKey;
    private int heapCount;
    private Set<String> usedInfos;
    private Map<String, Integer> baseWeights;
    private State state;

    public StepGenerator(State state, Map<String, Integer[]> weightRanges, int maxKey, int heapCount) {
        this.state = state;
        this.baseWeights = chooseConcreteWeights(weightRanges);
        this.maxKey = maxKey;
        this.heapCount = heapCount;
        usedInfos = new HashSet<>();
    }

    public static Map<String, Integer> chooseConcreteWeights(Map<String, Integer[]> weightRanges) {
        Map<String, Integer> weights = new HashMap<>();
        for (Map.Entry<String, Integer[]> entry : weightRanges.entrySet()) {
            weights.put(
                    entry.getKey(),
                    new Random().nextInt(
                            entry.getValue()[0], entry.getValue()[1]
                    )
            );
        }
        return weights;
    }

    public Step getNextStep() {
        if (state.heapStates.size() == 0) {
            return new InitializeStep(heapCount);
        }
        Random random = new Random();
        String stepType = getRandomStepType();
        switch (stepType) {
            case "Insert" -> {
                int heapIndex = random.nextInt(state.heapStates.size());
                int key = random.nextInt(1, maxKey + 1);
                return new InsertStep(heapIndex, key, generateNewInfo());
            }
            case "DeleteMin" -> {
                return new DeleteMinStep(getRandomNonEmptyHeapIndex());
            }
            case "Delete" -> {
                int heapIndex = getRandomNonEmptyHeapIndex();
                return new DeleteStep(heapIndex, getRandomExistingInfo(heapIndex));
            }
            case "DecreaseKey" -> {
                int heapIndex = getRandomNonEmptyHeapIndex();
                String info = getRandomExistingInfo(heapIndex);
                int currKey = state.heapStates.get(heapIndex).items.get(info);
                int diff = new Random().nextInt(currKey);
                return new DecreaseKeyStep(heapIndex, info, diff);
            }
            case "Meld" -> {
                List<Integer> possibleIndices = new ArrayList<>();
                for (int i = 0; i < state.heapStates.size(); i++) {
                    possibleIndices.add(i);
                }
                Collections.shuffle(possibleIndices);
                return new MeldStep(possibleIndices.get(0), possibleIndices.get(1));
            }
        }
        return null;
    }

    private String getRandomStepType() {
        List<String> stepTypes = new ArrayList<>();
        int nonEmptyHeaps = getNumOfNonEmptyHeaps();

        for (int i = 0; i < baseWeights.get("Insert"); i++) {
            stepTypes.add("Insert");
        }
        if (nonEmptyHeaps > 0) {
            for (int i = 0; i < baseWeights.get("DeleteMin"); i++) {
                stepTypes.add("DeleteMin");
            }
            for (int i = 0; i < baseWeights.get("Delete"); i++) {
                stepTypes.add("Delete");
            }
            for (int i = 0; i < baseWeights.get("DecreaseKey"); i++) {
                stepTypes.add("DecreaseKey");
            }
        }
        if (state.heapStates.size() > 1) {
            for (int i = 0; i < baseWeights.get("Meld"); i++) {
                stepTypes.add("Meld");
            }
        }
        return stepTypes.get(new Random().nextInt(stepTypes.size()));
    }

    private int getNumOfNonEmptyHeaps() {
        int count = 0;
        for (int i = 0; i < state.heapStates.size(); i++) {
            if (state.heapStates.get(i).items.size() > 0) {
                count++;
            }
        }
        return count;
    }
    private int getRandomNonEmptyHeapIndex() {
        List<Integer> possibleIndices = new ArrayList<>();
        for (int i = 0; i < state.heapStates.size(); i++) {
            if (state.heapStates.get(i).items.size() > 0) {
                possibleIndices.add(i);
            }
        }
        return possibleIndices.get(new Random().nextInt(possibleIndices.size()));
    }

    private String getRandomExistingInfo(int heapIndex) {
        Set<String> possibleInfos = state.heapStates.get(heapIndex).items.keySet();
        Iterator<String> iterator = possibleInfos.iterator();
        int index = new Random().nextInt(1, possibleInfos.size() + 1);

        String info = null;
        for (int i = 0; i < index; i++) {
            info = iterator.next();
        }
        return info;
    }

    private String generateNewInfo() {
        for (int i = 0; i < 1000; i++) {
            String info = generateInfo();
            if (!usedInfos.contains(info)) {
                usedInfos.add(info);
                return info;
            }
        }
        System.out.println("Can't generate new unique id (info) :(");
        System.exit(1);
        return null;
    }

    private String generateInfo() {
        return UUID.randomUUID().toString().substring(0, 8);
//        try {
//            Files.readAllLines(Paths.get(Objects.requireNonNull(getClass().getResource("animals.txt")).toURI()));
//        }
//        catch (IOException | URISyntaxException e) {
//            System.out.println("Can't read resource ");
//        }
//
    }
}
