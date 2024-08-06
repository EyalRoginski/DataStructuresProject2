package binomialheaptester;

import binomialheaptester.steps.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Runner {
	
    public static String failedStepsFilePath = null; // by default (if null) a new file in the current working directory will be used.

    // Use simple prints instead of a progress bar.
    // Change this to true if the progress bar doesn't work properly.
    public static boolean dontUseProgressBar = false;

    // Roughly represent the amount of times each method is called.
    public static Map<String, Integer[]> weightRanges = Map.of(
            "Insert", new Integer[]{5, 15},
            "Delete", new Integer[]{4, 8},
            "DeleteMin", new Integer[]{3, 7},
            "DecreaseKey", new Integer[]{5, 10},
            "Meld", new Integer[]{1, 4}
    );

    // controls the ratio between the test length and the number of keys used.
    // lower key range - higher chances of the same key appearing multiple times.
    public static double minKeyRangeRatio = 0.005;
    public static double maxKeyRangeRatio = 3;

    // controls the number of heaps initialized for each test case
    // heaps are only initialized at the beginning of the test (and later melded together).
    public static int minHeapCount = 5;
    public static int maxHeapCount = 50;

    // number of steps in each test case
    public static int testLength = 2000;

    // number of test cases run.
    public static int numOfTests = 10000;

    public static void main(String[] args){
        if (failedStepsFilePath == null) {
            failedStepsFilePath = Paths.get(
                    System.getProperty("user.dir"),
                    "binomial_heap_tester_failed_steps.txt"
            ).toString();
        }
        File failedStepsFile = new File(failedStepsFilePath);
        SingleTestResult result = null;
        if (failedStepsFile.exists()){
            System.out.println("Running a previously failed test case.\n");
            List<Step> failedSteps = readFailedResult();
            result = rerunFailedTest(failedSteps);
            if (result.success) {
                System.out.println("Successfully run previously failed test case. Re-run to test new cases");
            }
            else {
                System.out.println("Failed running the test case:");
                System.out.println(result.errorMessage);
                printLastSteps(result.steps);
                System.out.println("Re-run to test this case again.");
                System.out.printf("(Failed test case saved in: %s)%n", failedStepsFilePath);
            }
            saveResult(result);
            return;
        }
        else {
            System.out.printf("Running %d randomly generated test cases.%n%n", numOfTests);
            for (int i = 0; i < numOfTests; i++) {
                result = runNewTest();
                if (!result.success) {
                    System.out.println();
                    System.out.println("Failed running a test case:");
                    System.out.println(result.errorMessage);
                    printLastSteps(result.steps);
                    System.out.println("Re-run to test this case again.");
                    System.out.printf("(Failed test case saved in: %s)%n", failedStepsFilePath);
                    saveResult(result);
                    return;
                }
                updateProgressBar(i + 1, numOfTests);
            }
            System.out.println();
            System.out.printf("Successfully run %d test cases!".formatted(numOfTests));
        }
        if (result != null) {
            saveResult(result);
        }
    }

    private static void printLastSteps(List<Step> steps) {
        int numOfPrintedSteps = Math.min(steps.size(), 10);
        List<Step> lastSteps = steps.subList(steps.size() - numOfPrintedSteps, steps.size());
        System.out.printf("Last %d steps performed (of %d):%n", numOfPrintedSteps, steps.size());
        for (Step step : lastSteps) {
            System.out.println(step.toString());
        }
        System.out.println();
    }

    public static SingleTestResult rerunFailedTest(List<Step> failedSteps) {
        State state = new State();
        List<Step> performedSteps = new ArrayList<>();
        Step step;
        try {
            for (int i = 0; i < failedSteps.size(); i++) {
                step = failedSteps.get(i);
                performedSteps.add(step);
                if (i == failedSteps.size() - 1) {
                    String lastStepBreakpointHere = "Last Step Breakpoint Here!";
                }
                String errorMessage = step.perform(state);
                if (errorMessage != null) {
                    return new SingleTestResult(false, performedSteps, errorMessage);
                }
                errorMessage = Validator.validateState(state);
                if (errorMessage != null) {
                    return new SingleTestResult(false, performedSteps, errorMessage);
                }
            }
            return new SingleTestResult(true, performedSteps, null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new SingleTestResult(false, performedSteps, e.getMessage());
        }
    }

    public static SingleTestResult runNewTest(){
        Random random = new Random();
        State state = new State();
        int heapCount = random.nextInt(minHeapCount, maxHeapCount + 1);
        StepGenerator stepGenerator = new StepGenerator(state, weightRanges, chooseKeyRange(testLength + 1), heapCount);
        List<Step> steps = new ArrayList<>();

        try {
            for (int i = 0; i < testLength + 1; i++) {
                Step newStep = stepGenerator.getNextStep();
                steps.add(newStep);
                String errorMessage = newStep.perform(state);
                if (errorMessage != null) {
                    return new SingleTestResult(false, steps, errorMessage);
                }
                errorMessage = Validator.validateState(state);
                if (errorMessage != null) {
                    return new SingleTestResult(false, steps, errorMessage);
                }
            }
            return new SingleTestResult(true, steps, null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new SingleTestResult(false, steps, e.getMessage());
        }
    }

    private static void updateProgressBar(int currVal, int maxVal) {
        if (dontUseProgressBar) {
            if (currVal % 250 == 0) {
                System.out.printf("%d of %d test cases completed.%n", currVal, maxVal);
            }
            return;
        }
        int frac = (20 * currVal)/maxVal;
        String bar = String.join("", Collections.nCopies(frac, "="));
        String info = "%d of %d test cases completed.".formatted(currVal, maxVal);
        String barWithInfo = "[%-20s] %-40s".formatted(bar, info);
        System.out.print("\r" + barWithInfo);
    }

    private static int chooseKeyRange(int testLength) {
        double maxKeyRatio = new Random().nextDouble(minKeyRangeRatio, maxKeyRangeRatio);
        return (int) (testLength * maxKeyRatio);
    }

    public static class SingleTestResult {

        public boolean success;
        public String errorMessage;
        public List<Step> steps;

        public SingleTestResult(boolean success, List<Step> steps, String errorMessage) {
            this.success = success;
            this.steps = steps;
            this.errorMessage = errorMessage;
        }
    }

    private static void saveResult(SingleTestResult result) {
        if (result.success) {
            try {
                Files.deleteIfExists(new File(failedStepsFilePath).toPath());
                return;
            } catch (IOException e) {
                System.out.println("Can't delete previous failed test case. Please delete the file manually before re-running.");
                System.out.printf("Failed test case file path: %s%n", failedStepsFilePath);
                System.exit(1);
            }
        }
        try(PrintWriter writer = new PrintWriter(failedStepsFilePath)) {
            writer.println(result.errorMessage);
            writer.println();
            writer.println("Performed steps:");
            for (Step step : result.steps) {
                writer.println(step.toString());
            }
        } catch (IOException e) {
            System.out.println("Can't save failed result :(");
            System.exit(1);
        }
    }

    private static List<Step> readFailedResult() {
        List<Step> steps = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(failedStepsFilePath))) {
            // ignore header
            scanner.nextLine();
            scanner.nextLine();
            scanner.nextLine();
            while (scanner.hasNextLine()){
                String rawStep = scanner.nextLine();
                steps.add(parseStep(rawStep));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Can't read previous failed result :(");
            System.exit(1);
        }
        return steps;
    }

    private static Step parseStep(String rawStep) {
        Scanner scanner = new Scanner(rawStep);
        String stepType = scanner.next();
        switch (stepType) {
            case "Initialize" -> {
                return new InitializeStep(scanner.nextInt());
            }
            case "DecreaseKey" -> {
                return new DecreaseKeyStep(scanner.nextInt(), scanner.next(), scanner.nextInt());
            }
            case "DeleteMin" -> {
                return new DeleteMinStep(scanner.nextInt());
            }
            case "Delete" -> {
                return new DeleteStep(scanner.nextInt(), scanner.next());
            }
            case "Insert" -> {
                return new InsertStep(scanner.nextInt(), scanner.nextInt(), scanner.next());
            }
            case "Meld" -> {
                return new MeldStep(scanner.nextInt(), scanner.nextInt());
            }
            default -> {
                System.out.printf("Invalid previous result file: unrecognized step \"%s\"%n", stepType);
                System.exit(1);
            }
        }
        return null;
    }
}
