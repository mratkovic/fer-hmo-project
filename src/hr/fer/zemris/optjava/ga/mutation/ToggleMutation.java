package hr.fer.zemris.optjava.ga.mutation;

import java.util.Random;

import hr.fer.zemris.optjava.ga.solution.IntArraySolution;

public class ToggleMutation implements IMutation<IntArraySolution> {
    Random rnd;

    public ToggleMutation(final Random rnd) {
        this.rnd = rnd;
    }

    @Override
    public IntArraySolution mutate(final IntArraySolution individual) {
        IntArraySolution d = individual.duplicate();

        int i = rnd.nextInt(individual.size());
        int index = rnd.nextInt(individual.possibleValues.length);
        d.values[i] = d.possibleValues[index];
        return d;
    }

}
