package hr.fer.zemris.optjava.localsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hr.fer.zemris.optjava.ga.solution.IntArraySolution;

public class SwapNeighbors implements ILocalSearch<IntArraySolution> {

    Random rnd;
    int kSwaps;

    int n;

    public SwapNeighbors(final Random rnd, final int kSwaps, final int n) {
        super();
        this.rnd = rnd;
        this.kSwaps = kSwaps;
        this.n = n;
    }

    @Override
    public List<IntArraySolution> neighbors(final IntArraySolution individual) {
        List<IntArraySolution> ns = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            IntArraySolution d = individual.duplicate();
            for (int k = 0; k < kSwaps; ++k) {
                int r1 = rnd.nextInt(individual.size());
                int r2 = rnd.nextInt(individual.size());

                int tmp = d.values[r1];
                d.values[r1] = d.values[r2];
                d.values[r2] = tmp;
            }
            ns.add(d);
        }
        return ns;
    }

}
