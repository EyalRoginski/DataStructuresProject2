package binomialheaptester.steps;

import binomialheaptester.State;

public interface Step{

    public String perform(State state);
    public String toString();
}

