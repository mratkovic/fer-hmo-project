package hr.fer.zemris.optjava.ga.mutation;

import java.util.Random;

import hr.fer.zemris.optjava.ga.solution.IntArraySolution;

public class ToggleMutation implements IMutation<IntArraySolution> {
    Random rnd;
    int k;

    public ToggleMutation(final Random rnd, final int k) {
        this.rnd = rnd;
        this.k = k;
    }

    @Override
    public IntArraySolution mutate(final IntArraySolution individual) {
        IntArraySolution d = individual.duplicate();
        for (int i = 0; i < k; ++i) {
            int index = rnd.nextInt(individual.size());
            int valIndex = rnd.nextInt(individual.possibleValues.length);
            d.values[index] = d.possibleValues[valIndex];
        }
        return d;
    }

}
