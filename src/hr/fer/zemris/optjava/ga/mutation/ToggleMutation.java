package hr.fer.zemris.optjava.ga.mutation;

import java.util.Random;

import hr.fer.zemris.optjava.ga.solution.IntArraySolution;

public class ToggleMutation implements IMutation<IntArraySolution> {
    Random rnd;
    double percentage;

    public ToggleMutation(final Random rnd, final double percentage) {
        this.rnd = rnd;
        this.percentage = percentage;
    }

    @Override
    public IntArraySolution mutate(final IntArraySolution individual) {
        IntArraySolution d = individual.duplicate();

        for (int i = 0; i < d.size(); ++i) {
            if (rnd.nextDouble() < percentage) {
                int index = rnd.nextInt(individual.size());
                int valIndex = rnd.nextInt(individual.possibleValues.length);
                d.values[index] = d.possibleValues[valIndex];
            }
        }
        return d;
    }

}
